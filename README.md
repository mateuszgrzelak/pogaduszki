# Simple chat project
A simple project that uses [RabbitMQ](https://www.rabbitmq.com/), [WebSocket](https://en.wikipedia.org/wiki/WebSocket), [MongoDB](https://www.mongodb.com/) and [Spring](https://spring.io/).

The website is available at [http://185.243.54.201/](http://185.243.54.201:8080/)

Authentication is done by Spring Security where details about users are stored in MongoDB. All messages are sending through WebSocket protocol and are stored in MongoDB.
 
RabbitMQ and MongoDB can be downloaded using [Docker](https://www.docker.com/).
 
### Installation

**MongoDB:** In CLI (Command Line Interpreter) enter:
```
docker run -d --name some-mongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=<login> -e MONGO_INITDB_ROOT_PASSWORD=<password> mongo
```

**RabbitMQ:** First create Dockerfile that contains:
```
FROM rabbitmq:3-management
RUN rabbitmq-plugins enable --offline rabbitmq_management
RUN rabbitmq-plugins enable --offline rabbitmq_stomp
RUN rabbitmq-plugins enable --offline rabbitmq_web_stomp
EXPOSE 61613
```
Then in CLI enter: 
```
docker build -f Dockerfile -t rabbitmq:chat .
```

After that in CLI enter: 
```
docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 61613:61613 -e RABBITMQ_DEFAULT_USER=<user> -e RABBITMQ_DEFAULT_PASS=<password> rabbitmq:chat
```

This project uses authentication in MongoDB and RabbitMQ. 

After downloading the source code and building the jar file in CLI enter: 
```
java -Drabbitmq.login=<user> -Drabbitmq.password=<password> -Dspring.data.mongodb.username=<user> -Dspring.data.mongodb.password=<password> -DIPAddress=<optional_ip_address or *> -jar czat.jar >logs.log 2>errors.log
```

## Login page

<p align="center"> 
<img src="https://github.com/mateuszgrzelak/pogaduszki/blob/master/github_images/login.png">
</p>

## Signup page

<p align="center"> 
<img src="https://github.com/mateuszgrzelak/pogaduszki/blob/master/github_images/register.png">
</p>

## Main page

<p align="center"> 
<img src="https://github.com/mateuszgrzelak/pogaduszki/blob/master/github_images/main.png">
</p>

