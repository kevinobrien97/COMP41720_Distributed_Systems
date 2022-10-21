FROM openjdk:8-jre-alpine
COPY target/broker-0.0.1.jar /broker-0.0.1.jar
CMD /usr/bin/java -jar /broker-0.0.1.jar --server.urls=$URLS
