To run this solution with docker first run the docker-compose using the below command:

```docker-compose up --build```

The run the client to send clients to the broker and print out quotations:

```mvn exec:java -pl client```


If not using docker, run each of the quotation services as well as the broker in separate terminals using the below commands:

```mvn spring-boot:run -pl auldfellas```
```mvn spring-boot:run -pl dodgydrivers```
```mvn spring-boot:run -pl girlpower```
```mvn spring-boot:run -pl broker```
```mvn exec:java -pl client```