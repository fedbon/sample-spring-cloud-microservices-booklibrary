## Sample Spring Cloud Microservices Book Library

### Overview

This repository contains a sample implementation of a book library system built using Spring Cloud microservices architecture. The project is divided into several microservices, each responsible for a specific functionality such as managing books, authors, comments, user authentication, and more.

### Features

- **Book Management**: Allows users to perform a wide range of operations on books, including browsing, searching, adding, updating, and deleting books from the system. Users can view detailed information about each book, including its title, author, genre, publication date, and ratings. Additionally, users can interact with comments associated with each book, posting their own comments and viewing comments made by other users.

- **Author Management**: Provides comprehensive functionalities related to authors, allowing users to explore author profiles, including biographical information, list of books authored, and statistics such as the number of votes received from users. Users can also search for authors based on various criteria and interact with author-related data seamlessly.

- **Comment Management**: Manages user comments on books efficiently, offering features such as fetching comments by book ID or user ID, counting comments by user ID, and sorting comments based on criteria like popularity or recency. Users can post comments, reply to existing comments, and engage in discussions within the community.

- **User Authentication**: Implements a robust user authentication system to ensure secure access to the system's functionalities. Supports user registration with validation, authentication via username and password, and token-based authentication using JWT (JSON Web Tokens) for stateless and secure communication between client and server.

- **Genre Management**: Facilitates easy exploration and discovery of books by enabling users to browse and retrieve information about book genres available in the system. Users can explore different genres, view popular books within each genre, and discover new books based on their interests.

- **Gateway Server**: Serves as the central entry point to the system, routing incoming requests from clients to the appropriate microservices. It provides a unified interface for clients to interact with the various functionalities offered by the system, abstracting away the complexities of the underlying microservices architecture.

- **Service Discovery**: Leverages Eureka server for service registration and discovery, enabling seamless communication and collaboration between microservices. Eureka allows microservices to dynamically register themselves with the service registry upon startup and discover other services based on their logical names, ensuring robustness and scalability in distributed systems.


### Architecture

The architecture of the system follows the principles of microservices, where each microservice is designed to be independently deployable and scalable. The system employs reactive programming with Spring WebFlux to handle asynchronous and non-blocking I/O operations efficiently.

### Setup Instructions

1. **Clone Repository**: Clone this repository to your local machine.

2. **Build Microservices**: Build each microservice using Maven or Gradle. Ensure that all dependencies are resolved.

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

### Registration

- **Sing Up**
To register a new user, send a POST request to `/api/v1/auth/signup` with the following JSON payload:

```json
{
  "username": "max_payne",
  "password": "12345",
  "first_name": "Max",
  "last_name": "Payne"
}
```
- **Sing In**
To log in with an existing user, send a POST request to /api/v1/auth/signin with the following JSON payload:
- 
```json
{
  "username": "max_payne",
  "password": "12345"
}
```
- **Get My Account Information**: To retrieve the account information of the currently authenticated user, send a GET request to /api/v1/user/my with the appropriate authorization token in the header.
- **Get User Information by ID**: To fetch information about a specific user by their ID, send a GET request to /api/v1/user/{user_id} with the appropriate authorization token in the header.
- **Validate Token**: To validate an authentication token, send a POST request to /api/v1/auth/validate?token={auth_token}. Note that the {auth_token} placeholder should be replaced with the actual authentication token.
- **Get Book by ID**: To retrieve information about a specific book by its ID, send a GET request to /api/v1/books/{book_id} with the appropriate authorization token in the header.
- **Get All Books**: To fetch information about all books in the system, send a GET request to /api/v1/books with the appropriate authorization token in the header.
- **Order Books by CreateAt Field**: To order books by the createdAt field in descending order, send a GET request to /api/v1/books?order=createdAt&desc=true with the appropriate authorization token in the header.
- **Get All Books by Genre ID**: To retrieve information about all books belonging to a specific genre, send a GET request to /api/v1/books?genre={genre_id} with the appropriate authorization token in the header.
- **Sort All Books Voted by User ID**: To sort all books voted by a specific user ID by the negative field, send a GET request to /api/v1/books?userId={user_id}&sort=negative with the appropriate authorization token in the header.
- **Get All Comments by User ID**: To fetch all comments made by a specific user, send a GET request to /api/v1/comments/user/{user_id} with the appropriate authorization token in the header.
Note: Ensure that you have placed the JWT token in the http-client.private.env.json file to enable the auth_token placeholder in the authorization header.

### Contributors

- Fedor Bondarev

### License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.