## Sample Spring Cloud Microservices Book Library

### Overview

This repository contains a sample implementation of a book library system built using Spring Cloud microservices architecture. The project is divided into several microservices, each responsible for a specific functionality such as managing books, authors, comments, user authentication, and more.

### Features

- **Book Management**: Allows users to browse, search, and retrieve information about books stored in the system. Users can also view comments and ratings associated with each book.

- **Author Management**: Provides functionalities related to authors, including retrieving author information and counting the number of users who have voted for a specific author.

- **Comment Management**: Manages user comments on books, including fetching comments by book ID, user ID, and counting comments by user ID.

- **User Authentication**: Supports user registration, authentication, and token validation for secure access to the system's functionalities.

- **Genre Management**: Enables users to browse and retrieve information about book genres available in the system.

- **Gateway Server**: Acts as an entry point to the system, handling requests from clients and routing them to the appropriate microservices.

- **Service Discovery**: Utilizes Eureka server for service discovery and registration, allowing microservices to dynamically discover and communicate with each other.

### Architecture

The architecture of the system follows the principles of microservices, where each microservice is designed to be independently deployable and scalable. The system employs reactive programming with Spring WebFlux to handle asynchronous and non-blocking I/O operations efficiently.

### Setup Instructions

1. **Clone Repository**: Clone this repository to your local machine.

2. **Build Microservices**: Build each microservice using Maven or Gradle. Ensure that all dependencies are resolved.

3. **Run Eureka Server**: Start the Eureka server by running the `EurekaDiscoveryServerApplication` class.

4. **Run Microservices**: Start each microservice individually, ensuring that they register themselves with the Eureka server.

5. **Access Gateway Server**: Once all microservices are running, access the gateway server to interact with the system's functionalities.

### Technologies Used

- **Spring Boot**: For building microservices and handling core functionalities.
- **Spring Cloud**: For implementing service discovery, routing, and other cloud-native features.
- **Reactive Programming**: Utilizing Spring WebFlux and Reactor for handling asynchronous operations.
- **Netflix Eureka**: For service registration and discovery.
- **Spring Security**: For implementing user authentication and authorization.
- **WebClient**: For making HTTP requests to other microservices.

### Contributors

- [Fedor Bondarev]
- [...]

### License

This project is licensed under the [MIT License] License - see the [LICENSE](LICENSE) file for details.