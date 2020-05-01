## A simple chat project written in Java that uses RabbitMQ, WebSocket, MongoDB and Spring.

 Authentication is done by Spring Security where details about users are stored in MongoDB. All messages are sending through WebSocket protocol and are stored in MongoDB.
 To download RabbitMQ and MongoDB you can use Docker and following commands:

MongoDB: 
```
docker run -d --name some-mongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=<login> -e MONGO_INITDB_ROOT_PASSWORD=<password> mongo
```

RabbitMQ: First create Dockerfile that contains:
```
FROM rabbitmq:3-management
RUN rabbitmq-plugins enable --offline rabbitmq_management
RUN rabbitmq-plugins enable --offline rabbitmq_stomp
RUN rabbitmq-plugins enable --offline rabbitmq_web_stomp
EXPOSE 61613
```
Then in CLI (Command Line Interpreter) enter: `docker build -f Dockerfile -t rabbitmq:chat .`

After that in CLI enter: 
```
docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 61613:61613 -e RABBITMQ_DEFAULT_USER=<user> -e RABBITMQ_DEFAULT_PASS=<password> rabbitmq:chat
```

This project uses authentication in MongoDb and RabbitMQ. 

After downloading the source code and building the jar file in CLI enter: 
```
java -Drabbitmq.login=<user> -Drabbitmq.password=<password> -Dspring.data.mongodb.username=<user> -Dspring.data.mongodb.password=<password> -DIPAddress=<optional_ip_address or *> -jar czat.jar >logs.log 2>errors.log
```

![Login page](https://github.com/mateuszgrzelak/pogaduszki/blob/master/github_images/login.png)

![Register page](https://github.com/mateuszgrzelak/pogaduszki/blob/master/github_images/register.png)

![Main page](https://github.com/mateuszgrzelak/pogaduszki/blob/master/github_images/main.png)

