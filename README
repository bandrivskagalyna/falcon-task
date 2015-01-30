# Falcon Test project



This project is my solution for following task:



Setup a running env. aligned with the technologies mentioned below.
Develop the application in Play framework, in Java language (http://www.playframework.com//).
A REST endpoint is taking a dummy json input, and the server puts the REST payload on a Redis pub/sub channels (or the one the candidate knows the best for this task).
A Consumer is running the application and processes the freshly received message and persists the message in a NoSQL database (Redis or the one the candidate knows the best).
A REST endpoint is implemented for getting all the messages persisted in json format.
The message should also be pushed through Websockets for listening browser clients at the time the message was received on the REST endpoint (http://www.playframework.com/)
A simple html page is implemented to show the real time message delivery



# Solution


This project uses the following tools and technologies:

- Java 8

- PLay Framework 2.3 for Java

- Redis 2.4.6

- WebSocket




There are two Rest endpoint:
POST /newMessage - takes dummy json input and publishes it to Redis chanel. The following json format is used:
 {"messageText": "text"}


GET  /allMessages - returns all messages from Redis DB in json format




There is a simple web page implemented using WebSockets that shows real-time message delivery log.
It will only show those messages that are delivered while it is open:
GET  /deliveredMessages




Redis configuration properties are stored in ../conf/aplication.conf file
