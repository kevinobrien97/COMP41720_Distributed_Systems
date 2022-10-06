FROM openjdk:8-jre-alpine
# ENV CONTAINER_NAME localhost
COPY target/girlpower-0.0.1-jar-with-dependencies.jar /girlpower-0.0.1-jar-with-dependencies.jar
CMD /usr/bin/java -jar /girlpower-0.0.1-jar-with-dependencies.jar ${CONTAINER_NAME}
