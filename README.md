# PeopleFlow: Employees Management #
A RESTful API service for managing employees.
- The application exposes RESTful endpoints for creating, retrieving, and updating employees.
- Employee state machine allows transitions as follows: 
	```
	ADDED -> (CHECK) -> INCHECK -> (APPROVE) -> APPROVED -> (ACTIVATE) -> ACTIVE
	```
- When an employee is created or its state is updated, an event is produced and sent to the Kafka broker. 

## Requirements ##
For building and running the application, you need:
- Docker
- Docker Compose


## Running the Application ##
To build and run the application locally: 
- Build and run the Docker containers using:
	```console
	docker-compose up
	```
- Navigate to the API contract page to explore the available endpoints: http://localhost:8080/api/swagger-ui.html

## Copyright ##
Shaimaa Sabry. PeopleFlow. 2021.

