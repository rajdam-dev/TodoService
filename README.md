# TodoService

## Description

A Spring Boot service for managing a to-do list.

This service allows creating, updating, and tracking to-do items, including automatic handling of past-due items.

## 📌 Features

- Create a to-do item with description and due date
- Update description of an item
- Mark an item as DONE or NOT_DONE
- Automatically mark items as PAST_DUE when overdue
- Retrieve:
    - All NOT_DONE items (default)
    - All items (including DONE and PAST_DUE)
- Get details of a specific item

## Assumptions

- A to-do item can have one of these statuses:
  NOT_DONE, DONE, PAST_DUE
- Once an item becomes PAST_DUE, it cannot be modified
- Default behavior of GET API:
  - GET /todos returns both NOT_DONE and PAST_DUE items
  - GET /todos?includeDone=true returns all items
- creationTime is set automatically by the backend and cannot be provided by the client
- completionTime is set automatically when marking as DONE and cleared when marking as NOT_DONE
- I had two options for handling detection of Past-Due items and chose the scheduled job option:
  - via a scheduled background job
  - lazily during the GET request
- Since we use the H2 in-memory database, data is lost when the application is stopped
- The service has one global to-do list and doesn't support multiple users

## Tech stack used

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 in-memory database
- Lombok
- Maven
- JUnit 5, Mockito, MockMvc
- Docker

## How to build the service
  - From root directory of the project, run `mvn clean package`, which will build the service and produce a jar file here - `target/todoservice-0.0.1-SNAPSHOT.jar`

## How to run automatic tests
  - From root directory of the project, run `mvn clean test`

## How to run the service locally
  - There are multiple ways to run the service locally -
    - From root directory of the project, run `java -jar target/todoservice-0.0.1-SNAPSHOT.jar`
    - With Docker: 
      - Build image with: `docker build -t todoservice .`
      - Run container with: `docker run -p 8080:8080 todoservice`
  - After this, you can access the service end points at `http://localhost:8080/todos`
  - Here is the list of end points:
    - `POST /todos` - creates a todo item
    - `GET /todos` - retrieves todo items which are not completed
    - `GET /todos?includeDone=true` - retrieves all todo items
    - `GET /todos/{id}` - retrieves a todo item by id
    - `PATCH /todos/{id}` - partial updates to a todo item
    - `PUT /todos/{id}/done` - marks a todo item as done
    - `PUT /todos/{id}/not-done` - marks a todo item as not done
