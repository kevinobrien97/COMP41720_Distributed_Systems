package service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Session;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import service.message.QuotationRequestMessage;
import service.message.QuotationResponseMessage;
import java.util.HashMap;
import java.util.Map;
import service.message.ClientApplicationMessage;

public class Broker {

    // hashmap to associate an ID with the ClientApplicationMessage
    static Map<Long, ClientApplicationMessage> cache = new HashMap<>();

    public static void main(String[] args) {
        // default value for host - so that it runs outside of a docker container
		 String host = "localhost";
        
        // this will assign it the hostname provided by the docker-compose
        if (args.length > 0) {
            host = args[0];
        }
		try {
            // set up connection
			ConnectionFactory factory =
						new ActiveMQConnectionFactory("failover://tcp://"+host+":61616");
			Connection connection = factory.createConnection();
			connection.setClientID("broker");
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE); 

            // topic and queue to interact with quotation services
            Topic clientApplicationTopic = session.createTopic("APPLICATIONS");
            Queue quotationsRequestQueue = session.createQueue("QUOTATIONS");

            // 2 queues to interact with client
            Queue applicationRequestQueue = session.createQueue("REQUEST");
            Queue completedApplicationQueue = session.createQueue("RESPONSES");
			
			MessageProducer producerApplication = session.createProducer(clientApplicationTopic);
            MessageConsumer consumerQuotations = session.createConsumer(quotationsRequestQueue);

			MessageConsumer consumerReceivedApplications = session.createConsumer(applicationRequestQueue);
            MessageProducer producerCompletedApplications = session.createProducer(completedApplicationQueue);

			connection.start();

            // thread to receive applications from the client, then send completed responses back after a pause
            new Thread(() -> {
                while (true) {
                    try {
                        Message message = consumerReceivedApplications.receive();
                        if (message instanceof ObjectMessage) {
                            Object content = ((ObjectMessage) message).getObject();
                            if (content instanceof QuotationRequestMessage) {
                                QuotationRequestMessage quotationRequest = (QuotationRequestMessage) content;
                                // only if the client has not been sent before
                                if (!cache.containsKey(quotationRequest.id)) {
                                    // create new entry in hashmap
                                    cache.put(quotationRequest.id, new ClientApplicationMessage(quotationRequest.id));
                                    Message request = session.createObjectMessage(quotationRequest);
                                    producerApplication.send(request);
                                }
                                
                                // sleep the thread to allow the quotation services to return quotes which can then be added to the ClientApplicationMessage object in the below thread
                                Thread.sleep(8000);

                                // thread has slept for 8 secs and below thread should have received quotes so now send back to broker

                                // get the appplication message and send it to the RESPONSES queue
                                ClientApplicationMessage completedApplication = cache.get(quotationRequest.id);
                                Message quotationSet = session.createObjectMessage(completedApplication);
                                producerCompletedApplications.send(quotationSet);
                            }
                            // remove the message from the queue
                            message.acknowledge();
                        } else {
                            System.out.println("Unknown message type in broker: " +
                            message.getClass().getCanonicalName());
                    }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            // separate thread to receive messages from the quotation services and add them to the ClientApplicationMessage objects in the cache
            new Thread(() -> {
                // continue loop indefinitely
                while (true) {
                    try {
                        Message message = consumerQuotations.receive();
                        if (message instanceof ObjectMessage) {
                            Object content = ((ObjectMessage) message).getObject();
                            if (content instanceof QuotationResponseMessage) {
                                QuotationResponseMessage quotationResponse = (QuotationResponseMessage) content;
                                
                                // if there is an object in the cache already with the same ID
                                if (cache.containsKey(quotationResponse.id)) {
                                    // get the ClientApplicationMessage object associated with the ID and add the quote to its quotations array
                                    ClientApplicationMessage application = cache.get(quotationResponse.id);
                                    application.quotations.add(quotationResponse.quotation);
                                }
                            }
                            // remove the message from the queue
                            message.acknowledge();
                        } else {
                            System.out.println("Unknown message type in broker: " +
                            message.getClass().getCanonicalName());
                    }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (JMSException e) {
			e.printStackTrace();
		}

    }   
}
