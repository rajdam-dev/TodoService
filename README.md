# TodoService

## Description

A Spring Boot service for managing a to-do list.
This service allows creating, updating, and tracking to-do items, including automatic handling of past-due items.

## Assumptions

- A to-do item can have one of these statuses: NOT_DONE, DONE, PAST_DUE
- Once an item becomes PAST_DUE, it cannot be modified
- Default behavior of GET API:
  - GET /todos returns both NOT_DONE and PAST_DUE items
  - GET /todos?includeDone=true returns all items
- creationTime is set automatically by the backend and cannot be provided by the client
- completionTime is set automatically when marking as DONE and cleared when marking as NOT_DONE
- Update behavior:
  - dueTime can only be updated for NOT_DONE items
  - description can only be updated for NOT_DONE and DONE items
  - PUT /todos/{id}/done - explicit end point to mark a todo item as done
  - PUT /todos/{id}/not-done - explicit end point to mark a todo item as not done
- I had these two options for handling detection of Past-Due items. I chose the scheduled job option:
  - via a scheduled background job
  - lazily during the GET request
- The service has one global to-do list and doesn't support multiple users
- Note: I did not add `delete` functionality because that would break history/auditability and the status lifecycle of items (DONE, NOT_DONE, PAST_DUE).

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
  - There are multiple ways to run the service locally-
    - From root directory of the project, run `java -jar target/todoservice-0.0.1-SNAPSHOT.jar`
    - With Docker: 
      - Build image with: `docker build -t todoservice .`
      - Run container with: `docker run -p 8080:8080 todoservice`
  - You can access the service end points at `http://localhost:8080/todos`

## List of API end points
  - Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`
  - Here is the list:
    - `POST /todos` - creates a todo item
    - `GET /todos` - retrieves todo items which are not completed
    - `GET /todos?includeDone=true` - retrieves all todo items
    - `GET /todos/{id}` - retrieves a todo item by id
    - `PATCH /todos/{id}` - partial updates to a todo item (description and/or due date)
    - `PUT /todos/{id}/done` - marks a todo item as done
    - `PUT /todos/{id}/not-done` - marks a todo item as not done

## Code Coverage
- This project uses JaCoCo for code coverage. To generate coverage report, run: `mvn clean test`
- The report will be generated in target/site/jacoco/index.html
- Current coverage is 91%
