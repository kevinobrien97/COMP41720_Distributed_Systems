To run this project the ActiveMQ Docker image must be running locally. This can be done using the below command:

docker run --name='activemq' -p 8161:8161 -p 61616:61616 -it rmohr/activemq:latest