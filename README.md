# Project Readme

[//]: # (## General guidelines)

[//]: # (* Please spend some time to make a proper `ReadME` markdown file, explaining all the steps necessary to execute your source code.)

[//]: # (* Do not hardcode IP address or port numbers, try to collect these configurable information from config file/env variables/cmd input args.)

[//]: # (* Attach screenshots of your testing done on your local environment.)

## Brief overview of the project
* This project comes with 2 separate clients and 2 separate servers, for TCP and
UDP each
* There are 2 separate packages, for `client` and `server`

### Sample configuration

#### Project structure
* The following is our project structure.
```bash
├── README.md
├── client
│   ├── AbstractClient.java
│   ├── ClientAppTCP.java
│   ├── ClientAppUDP.java
│   ├── ClientLogger.java
│   ├── IClient.java
│   ├── TCPClient.java
│   └── UDPClient.java
├── client_log.txt
├── distributed_systems.iml
├── server
│   ├── AbstractServer.java
│   ├── IServer.java
│   ├── KeyValueStore.java
│   ├── ServerAppTCP.java
│   ├── ServerAppUDP.java
│   ├── ServerLogger.java
│   ├── TCPServer.java
│   └── UDPServer.java
└── server_log.txt
```
* Compile the code using `javac server/*.java client/*.java`
* To run the 
  * TCP server `java server.ServerAppTCP <tcp-port-number>`
  * UDP server `java server.ServerAppUDP <udp-port-number>`
* To run the 
  * TCP client `java client/ClientAppTCP <host-name> <port-number>`
  * UDP client `java client/ClientAppUDP <host-name> <port-number>`
* TCP client communicates with TCP server and UDP client communicates with UDP server
* All the client and server logs are generated automatically even if they don't exist in the project
  * Client logs are generated as `client_log.txt`
  * Server logs are generated as`server_log.txt`


# EXECUTIVE SUMMARY

# Assignment Overview
The purpose of this assignment was to develop a client-server application using TCP(Transmission Control Protocol) and UDP (User Datagram Protocol). Here the main aim was to understand the difference between the implementations of these protocols since the former is connection oriented whereas the latter is connectionless. Here the server would store key-value pairs in a HashMap data structure and the client would request for the following operations: GET(key), PUT(key, value), DELETE(key), GETALL() and DELETEALL().
The GET(key) function takes in user given key as input and sends it to the client which in turn searches the key in the KeyValueStore(HashMap) and returns the respective values. If the key is not found then appropiate message is thrown. The PUT(key,value) function takes in key and value as parameters and is used to add a new key and its respective value to the KeyValueStore. The DELETE(key)
function accepts a user given key as paramter and searches for that key in KeyValueStore and deletes the key from the store.
The additional task was to implement GETALL() functionality that doesnt require any paramter but it returns the list of all key value pairs that are present in the KeyValueStore. The DELETEALL() functionality is a simple function that deletes all the keys and its respective values from the KeyValueStore.
Additionaly our task was to design and develop clean code that is modular and handle errors efficiently. For this I have created abstract classes for both server and client to encapsulate common functionalities. For efficient eror handling I am using Exception Handling to catch exceptions.

The scope of the assignment included implementing functionalities such as PUT, GET, GETALL, DELETE, and DELETEALL on both the client and server sides. Plus this assignment supports single threaded application i.e the client and server processes just a single message at a time. Multithreaded processesnwould be covered in the next assignment. The assignment aimed to demonstrate understanding of socket programming, TCP and UDP communication, error handling, and data serialization.

# Technical Impression
During the assignment, implementing UDP communication provided insight into handling lightweight and efficient communication but also required additional consideration for reliability and data integrity. TCP, on the other hand, offered reliable, connection-oriented communication but with increased overhead due to its acknowledgment mechanism and congestion control. Handling reliability and data integrity with UDP involved implementing checksums and data chunking for larger payloads. TCP's reliability features, such as guaranteed delivery and ordered data transmission, simplified the implementation but required managing connections and handling potential bottlenecks. Overall, incorporating both UDP and TCP protocols provided a comprehensive understanding of their respective strengths and trade-offs in network communication.

There were several challenges encountered during this assignment:
1) Ensuring reliable communication over UDP protocol. 
--UDP is connectionless thats why there are high chances of losing packets over a network. To ensure reliable communication and take care of any malformed requests I introduced checksums in the UDP packet. the client sends a UDP packet with checksum appended. The server upon receiving the request would verify the checksum and if verifies then only processes the request.

2) Implementing additional tasks that is GETALL() and DELETEALL().
-- deleting all the key value pairs from the store was fairly straight-forward. for this I made use of clear() function present in HashMap class.
-- for the GETALL() functionality it was required to get all the key value pairs from the store. This task was fairly simple in the case of TCP protocol since it is coonection oriented and the whole message is transferred theerfore ensuring reliable communication.
-- I made use of keySet() function  of HashMap to retrieve all such pairs and then return the data to the client.
-- However this was not that simple in the case of UDP where the protocol is connection-less. Here the size of the buffer is limited therefore all the data cannot be transferred in a single packet. hence I mad euse of a mechanism called chunks where I sent chunks of data for processing at a single time. I used the MAX_CAPACITY of buffer size which is 65507 to send the max number of data. 
-- Throughout this client and server would be running coninuously and listeing for requests on its respective sockets. The loop would only break when the size of data is less than the MAX_CAPACITY of the buffer. It would then mean the end of data.

# USE_CASE
IoT Device Communication:

--In Internet of Things (IoT) scenarios, where lightweight devices communicate with a central server, single-threaded client-server applications can be suitable.
--IoT devices often have limited resources, and a single-threaded server can efficiently handle communication with multiple devices without the overhead of managing multiple threads.
--For example, a smart home system where sensors send data to a central server for monitoring and control purposes could utilize a single-threaded server to handle incoming requests from various devices.