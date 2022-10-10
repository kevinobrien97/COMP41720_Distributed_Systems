FROM openjdk:8-jre-alpine
COPY target/broker-0.0.1-jar-with-dependencies.jar /broker-0.0.1-jar-with-dependencies.jar
CMD /usr/bin/java -jar /broker-0.0.1-jar-with-dependencies.jar ${CONTAINER_NAME}
