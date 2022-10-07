package service.dodgydrivers;

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
import service.core.Quotation;


public class Receiver {

    static DDQService ddqService = new DDQService();
    public static void main (String[] args) {

        // default value for host - so that it runs outside of a docker container
        String host = "localhost";
        
        // this will assign it the hostname provided by the docker-compose
        if (args.length > 0) {
            host = args[0];
        }

        ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://"+host+":61616");

        try {
            Connection connection = factory.createConnection();
            connection.setClientID("dodgydrivers");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Queue queue = session.createQueue("QUOTATIONS");
            Topic topic = session.createTopic("APPLICATIONS");
            MessageConsumer consumer = session.createConsumer(topic);
            MessageProducer producer = session.createProducer(queue);

            connection.start();
            while (true) {
                // Get the next message from the APPLICATION topic
                Message message = consumer.receive();
                // Check it is the right type of message
                if (message instanceof ObjectMessage) {
                    // It’s an Object Message
                    Object content = ((ObjectMessage) message).getObject();

                    if (content instanceof QuotationRequestMessage) {
                        // It’s a Quotation Request Message
                        QuotationRequestMessage request = (QuotationRequestMessage) content;

                        // Generate a quotation and send a quotation response message…
                        Quotation ddqQuotation = ddqService.generateQuotation(request.info);
                        Message response = session.createObjectMessage(
                                    new QuotationResponseMessage(request.id, ddqQuotation));
                        producer.send(response);
                    }
                } else {
                    System.out.println("Unknown message type: " +
                    message.getClass().getCanonicalName());
                }
            } 

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
