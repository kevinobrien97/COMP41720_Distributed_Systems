This project can be run using Docker with the following command:

`docker-compose up --build`

This will begin the ActiveMQ message broker, the 3 quotation services as well as the broker. The client can then be run using maven and interact with the services running on Docker using the following command:

`mvn exec:java -pl client`



To run this project without Docker the ActiveMQ Docker image must be running locally. This can be done using the below command:

`docker run --name='activemq' -p 8161:8161 -p 61616:61616 -it rmohr/activemq:latest`

And then each of the services can be run in individual terminals, keeping the client to the end.