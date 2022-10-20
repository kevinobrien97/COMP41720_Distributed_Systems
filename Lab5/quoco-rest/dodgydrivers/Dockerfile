FROM openjdk:8-jre-alpine
# ENV CONTAINER_NAME localhost
COPY target/dodgydrivers-0.0.1.jar /dodgydrivers-0.0.1.jar
CMD /usr/bin/java -jar /dodgydrivers-0.0.1.jar ${CONTAINER_NAME}
