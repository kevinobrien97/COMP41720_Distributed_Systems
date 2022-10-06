FROM openjdk:8-jre-alpine
# ENV CONTAINER_NAME localhost
COPY target/dodgydrivers-0.0.1-jar-with-dependencies.jar /dodgydrivers-0.0.1-jar-with-dependencies.jar
CMD /usr/bin/java -jar /dodgydrivers-0.0.1-jar-with-dependencies.jar ${CONTAINER_NAME}
