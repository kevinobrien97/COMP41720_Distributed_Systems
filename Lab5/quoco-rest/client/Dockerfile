FROM openjdk:8-jre-alpine
# ENV CONTAINER_NAME localhost
# ENV PORT 9000
COPY target/client-0.0.1.jar /client-0.0.1.jar
CMD /usr/bin/java -jar /client-0.0.1.jar -h ${CONTAINER_NAME}
