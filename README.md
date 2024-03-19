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


--------------------------------------------------------------------------------------------------------------------------------------

Project 2 -- Implementing RMI


* Compile the code using `javac shared/*.java server/*.java client/*.java`
* To run the 
  * RMI server `java server.RMIServer <port-number>`
* To run the 
  * RMI client `java client/RMIClient <host-name> <port-number>`

# EXECUTIVE SUMMARY

# Assignment Overview
The purpose of this assignment was to develop a client-server application using Java RMI (Remote Method Invocation) for communication between the client and the server. 
Remote Method Invocation (RMI) allows us to get a reference to an object on a remote host and use it as if it were on our virtual machine. We can invoke methods on the remote objects, passing real objects as arguments and getting real objects as returned values.(Similar to Remote Procedure Call (RPC) in C). RMI uses object serialization, dynamic class loading and security manager to transport Java classes safely. Thus we can ship both code and data around the network. RMI is multithreaded already but is not thread-safe. A method dispatched by the RMI runtime to a remote object implementation may or may not execute in a separate thread. The RMI runtime makes no guarantees with respect to mapping remote object invocations to threads. Since remote method invocation on the same remote object may execute concurrently, a remote object implementation needs to make sure its implementation is thread-safe.
Stub is the local code that serves as a proxy for a remote object. The skeleton is another proxy that lives on the same host as the real object. The skeleton receives remote method invocations from the stub and passes them on to the object.


The scope of the assignment involved implementing a simple key-value store where the client could perform operations such as PUT, GET, GETALL, DELETE, and DELETEALL on the server's key-value store. The assignment aimed to demonstrate understanding of RMI, multi-threading, and basic client-server architecture.


# Technical Impression
During the assignment, I gained practical experience in implementing a distributed application using Java RMI.

While implementing the Remote Method Invocation (RMI) I followed the following steps :
1) Create the remote interface
2) Provide the implementation of the remote interface
3) Compile the implementation class and create the stub and skeleton objects
5) Create and start the server application
6) Create and start the client application

Challenges I faced during this project: 
-- I encountered challenges in properly setting up the RMI registry, defining the remote interface, and handling remote method invocations. 
-- Implementing thread safe functionality to handle multiple client requests concurrently required careful consideration of synchronization and use of Concurrent HashMap.

RMI has an inbuild thread pool and is multhreaded that is it can handle concurrent requests simultaneously.
RMI automatically will execute each client in a separate thread.  There will only be one instance of the server object no matter how many clients, so we need to make sure that shared data is synchronized appropriately.  In particular, any data that may be accessed by more than one client at a time must be synchronized. 
When a server object is exported through UnicastRemoteObject, a server socket is created in a new thread either on specified port or default port chosen by RMI which eventually blocks in accept call. The invocation of remote operation on client stub initiates a connection request with the remote server. This is usually done by the tranport layer in client RMI by using the remote reference contained by the stub. This remote reference contains the host, port on which the remote object is exported.
Once the server receives the connection request, it creates a new thread and forwards the new socket returned by accept call to it. After forwarding the new socket to the object in a new thread, the listener thread again blocks in accept call to service another client request. Now server reads remote objectid, method hash value, marshalled parameters from the socket written by the client and finally dispatches the operation to the appropriate remote object.
Once client finishes the remote operation, tranport layer in the client RMI will not close this connection. It will keep it alive for some time before completely closing it.

So if multiple clients call the server, all the method invocations can happen simultaneously, causing race conditions. The same
method may also be run by more than one thread on behalf of one or more clients. Hence we must write the server to be thread-safe.

To make the concurrent requests thread safe I make use of the keyword syncronized in the interface implementation function.
The synchronized keyword locks the resources to a thread so that no other thread can access it at a time. The synchronized keyword prevents the program statement from getting reordered. The synchronized keyword ensures the locking and unlocking of threads before and after getting inside the synchronized block. 
Moreover for protection over edit data I make use of Concurrent HashMap. ConcurrentHashMap is an enhancement of HashMap as we know that while dealing with Threads in our application. ConcurrentHashMap is a thread-safe implementation of the Map interface in Java, which means multiple threads can access it simultaneously without any synchronization issues. It’s part of the java.util.concurrent package. One of the key features of the ConcurrentHashMap is that it provides fine-grained locking, meaning that it locks only the portion of the map being modified, rather than the entire map. This makes it highly scalable and efficient for concurrent operations. Additionally, the ConcurrentHashMap provides various methods for atomic operations such as putIfAbsent(), replace(), and remove().
In ConcurrentHashMap, at a time any number of threads can perform retrieval operation but for updated in the object, the thread must lock the particular segment in which the thread wants to operate. This type of locking mechanism is known as Segment locking or bucket locking.


Overall, the assignment provided valuable insights into distributed systems, RMI communication, and concurrent programming. In the future, improvements could include better error handling, enhanced logging mechanisms, and optimization for scalability and performance.