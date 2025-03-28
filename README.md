# PhoneBook API

## Project Overview

The **PhoneBook API** is a RESTful web service designed to manage users and their contacts. It allows users to create, read, update, and delete both user profiles and their associated contacts. The API is built using **Spring Boot** and **Java**, with a focus on clean architecture and separation of concerns. This API supports CRUD operations for both **User** and **Contact** entities.

## Features

- **User Management**:
    - Create a new user.
    - Retrieve user details by ID.
    - Update existing user information.
    - Delete a user by ID.
    - Search users by phone number.

- **Contact Management**:
    - Add a new contact to a user.
    - Retrieve all contacts of a user, with optional search filters for first name, last name, or phone number.
    - Update contact information.
    - Delete a contact by ID.

- **Pagination**:
    - Supports pagination for both users and contacts, allowing efficient retrieval of large datasets.


- **Swagger UI Documentation**:
    - Comprehensive API documentation is available through Swagger at the endpoint: [Swagger UI](http://localhost:8080/swagger-ui/index.html).

## Technologies Used

- **Spring Boot**: A framework for building Java-based web applications quickly.
- **MongoDB**: A NoSQL database to store user and contact information.
- **Swagger UI**: For interactive API documentation.
- **Spring Data MongoDB**: A part of Spring Data, providing easy integration with MongoDB.
- **Spring Boot Starter Validation**: To perform input validation on API requests.
- **Spring Boot Starter Web**: To create RESTful web services.


## Swagger UI Documentation

To explore the full API documentation, you can navigate to the Swagger UI at the following URL:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

This page provides an interactive interface for testing all the endpoints of the API.

## Project Setup

### Prerequisites

- **Java 17** or later
- **Maven** (for building the project)
- **Spring Boot** (included in the project)

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/NaorFahima/phonebook-api.git
   ```

2. Navigate to the project directory:
   ```bash
   cd phonebook-api
   ```

3. Build and run the application using Docker:
   ```bash
   docker-compose up --build
   ```

4. The application will start on `http://localhost:8080`.

### Testing the API

Once the application is running, you can test the API using **Postman**, **curl**, or any other API testing tool.  
You have in the project postman file 'Phonebook User API.postman_collection' with api examples that can import to your postman
## Error Handling

The API uses custom exceptions to handle errors, and it returns appropriate HTTP status codes along with error messages. The most common error responses are:

- **404 Not Found**: When the requested user or contact does not exist.
- **400 Bad Request**: For invalid input data.
- **409 Conflict**: When a user with the provided phone number already exists.

## Conclusion

This PhoneBook API provides a robust solution for managing users and contacts. It is simple to use and well-documented through Swagger.
