The docker-compose file contains code that is commented out to run the client alongside each of the other 4 containers
As it takes some time to set up each of the web services before they can be advertised, this requires the client to sleep before it loops 
through the clients and requests quotations from the broker (which will not have found advertised QSs until they have been advertised)
I slept the client for 15 seconds although this may need to be increased depending on the machine being used

With the current set up, run the docker-compose using 
`docker-compose up --build`

and in a separate terminal in the client folder run
`docker build -t client:latest .`

followed by the below, which passes the local network to the client image
`docker run --network=host client:latest`