## Sample Spring Cloud Microservices Book Library

### Overview

This repository contains a sample implementation of a book library system built using Spring Cloud microservices architecture. The project is divided into several microservices, each responsible for a specific functionality such as managing books, authors, comments, user authentication, and more.

### Features

- **Book Management**: Allows users to perform CRUD operations on books, view detailed information such as title, author, genre, publication date, and ratings, and interact with comments.
- **Author Management**: Provides author profiles, including biographical information, authored books, and statistics. Users can search for authors and explore their data.
- **Comment Management**: Manages user comments on books, including fetching by book or user ID, counting by user ID, and sorting by popularity or recency. Users can post, reply, and engage in discussions.
- **User Authentication**: Implements secure user registration, authentication via username/password, and JWT-based token authentication.
- **Genre Management**: Facilitates book exploration by browsing genres, viewing popular books, and discovering new ones.
- **Gateway Server**: Central entry point routing requests to microservices, providing a unified interface for clients.
- **Service Discovery**: Uses Eureka server for service registration and discovery, ensuring seamless communication between microservices.

### Architecture

The architecture of the system follows the principles of microservices, where each microservice is designed to be independently deployable and scalable. The system employs reactive programming with Spring WebFlux to handle asynchronous and non-blocking I/O operations efficiently.

### Setup Instructions

1. **Clone Repository**: Clone this repository to your local machine.
2. **Build Microservices**: Build each microservice using Maven (`by default`) or Gradle. Ensure that all dependencies are resolved.
3. **Run Eureka Server**: Start the Eureka server by running the `EurekaDiscoveryServerApplication` class.
4. **Run Microservices**: Start each microservice individually, ensuring that they register themselves with the Eureka server.
5. **Access Gateway Server**: Once all microservices are running, access the gateway server to interact with the system's functionalities.

## Technologies Used

- **Spring Boot**: For building microservices and handling core functionalities.
- **Spring Cloud**: For implementing service discovery, routing, and other cloud-native features.
- **Reactive Programming**: Utilizing Spring WebFlux and Reactor for handling asynchronous operations.
- **Netflix Eureka**: For service registration and discovery.
- **Spring Security**: For implementing user authentication and authorization.
- **WebClient**: For making HTTP requests to other microservices.
- **MongoDB**: As the database for storing and managing data across microservices.
- **JWT (JSON Web Tokens)**: For securely transmitting information between parties as a JSON object.
- **Resilience4j**: For implementing fault tolerance and resilience in microservices, providing features like circuit breakers, retries, and rate limiting.
- **Kafka**: For building real-time streaming data pipelines and applications, enabling microservices to communicate asynchronously and decoupling systems.
- **Docker**: For containerizing microservices, enabling easy deployment and scalability in various environments.
- **Maven**: For project management and dependency management, ensuring consistent builds and easy dependency resolution.
- **MapStruct**: For simplifying the process of mapping objects between layers, improving code readability and maintainability.

### Using the `booklibrary-microservices.http` File for Testing

To test the program, you can use the `booklibrary-microservices.http` file, which contains a collection of HTTP requests. Follow these steps to utilize the file within IntelliJ IDEA Ultimate:

1. **View HTTP Requests**: Locate the `booklibrary-microservices.http` file in the project structure. This file contains a collection of HTTP requests that can be executed directly within IntelliJ IDEA Ultimate.

2. **Execute HTTP Requests**: Within IntelliJ IDEA Ultimate, you can execute the HTTP requests listed in the `booklibrary-microservices.http` file by simply clicking on the request and selecting the "Run" option.

3. **Review Responses**: After executing each request, review the response received from the server to ensure that the operation was successful.

4. **Test Different Endpoints**: Explore different endpoints and functionalities by executing various requests provided in the `booklibrary-microservices.http` file. This includes endpoints for user authentication, book management, author management, comment management, and more.

5. **Modify Requests (Optional)**: If needed, you can modify the requests in the `booklibrary-microservices.http` file to test specific scenarios or functionalities.

6. **Monitor Console Output**: Keep an eye on the console output within IntelliJ IDEA to view logs and information generated during the execution of HTTP requests.

By following these steps, you can effectively use the `booklibrary-microservices.http` file to test the program's functionalities directly within IntelliJ IDEA Ultimate or any other HTTP client that supports cURL or HTTP request importing.

**Note**: Ensure that you have placed the JWT token in the http-client.private.env.json file to enable the auth_token placeholder in the authorization header.

### Contributors

**Fedor Bondarev**

### License

This project is licensed under the [MIT License](LICENSE).