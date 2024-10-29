
# Smart Homestay

Smart Homestay is a backend service for managing homestay reservations, transactions, and customer accounts. The service provides endpoints for customer and crew registration, login, reservation management, and transaction processing. It also integrates with Kafka to send notifications upon successful reservation creation.

![ERD Smart Homestay](./ERD%20Smart%20Homestay.png)

## Table of Contents
- [Installation](#installation)
- [Endpoints](#endpoints)
  - [Register Customer](#register-customer)
  - [Login Customer](#login-customer)
  - [Logout Customer](#logout-customer)
  - [Register Crew](#register-crew)
  - [Login Crew](#login-crew)
  - [Logout Crew](#logout-crew)
  - [Reservation](#reservation)
  - [Transaction](#transaction)
- [Kafka Integration](#kafka-integration)

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/antonroycar/smart-homestay.git
   ```
2. Navigate to the project directory:
   ```bash
   cd smart-homestay
   ```
3. Build the project:
   ```bash
   ./mvnw clean install
   ```
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
5. Make sure Kafka is configured and running to handle reservation notifications.

## Endpoints

### Register Customer

Registers a new customer.

- **Endpoint**: `POST /auth/register`
- **Request Body**:
  ```json
  {
      "username": "roycar",
      "password": "roycar",
      "name": "Anton Roycar Nababan",
      "age": 22,
      "gender": "MALE",
      "contactNumber": "08123456789",
      "address": "bandung",
      "role" : "CUSTOMER"
  }
  ```
- **Response**:
  ```json
  "Customer registered successfully"
  ```

### Login Customer

Logs in an existing customer.

- **Endpoint**: `POST /auth/login`
- **Request Body**:
  ```json
  {
      "username": "roycar",
      "password": "roycar"
  }
  ```
- **Response**:
  ```json
  {
      "token" : "TOKEN",
      "expiredAt" : 2342342423423 // milliseconds
  }
  ```

### Logout Customer

Logs out the authenticated customer.

- **Endpoint**: `DELETE /auth/logout`
- **Request Header**: `X-API-TOKEN : Token (Mandatory)`
- **Response**:
  ```json
  "Logout successfully"
  ```

### Register Crew

Registers a new crew member.

- **Endpoint**: `POST /auth/register`
- **Request Body**:
  ```json
  {
    "username": "anton",
    "password": "anton",
    "name": "Anton Roycar Nababan",
    "jobTitle": "Manager",
    "role" : "CREW"
  }
  ```
- **Response**:
  ```json
  "Crew registered successfully"
  ```

### Login Crew

Logs in an existing crew member.

- **Endpoint**: `POST /auth/login`
- **Request Body**:
  ```json
  {
      "username": "anton",
      "password": "anton"
  }
  ```
- **Response**:
  ```json
  {
      "token" : "TOKEN",
      "expiredAt" : 2342342423423 // milliseconds
  }
  ```

### Logout Crew

Logs out the authenticated crew member.

- **Endpoint**: `DELETE /auth/logout`
- **Request Header**: `X-API-TOKEN : Token (Mandatory)`
- **Response**:
  ```json
  "Logout successfully"
  ```

### Reservation

Creates a reservation for a customer.

- **Endpoint**: `POST /api/reservations`
- **Request Header**: `X-API-TOKEN : Token (Mandatory)`
- **Request Body**:
  ```json
  {
      "checkInDate": "2024-12-01",
      "checkOutDate": "2024-12-05",
      "adults": 2,
      "children": 1,
      "roomTypes": [
          {
              "roomType": "SINGLE",
              "quantity": 1
          },
          {
              "roomType": "DOUBLE",
              "quantity": 1
          }
      ]
  }
  ```
- **Response Body**:
  ```json
  {
      "reservationId": "671f0026fbb9cc3f92866449",
      "accountId": "671efbee7a99c639ce4799b0",
      "roomTypeDetails": [
          {
              "type": "SINGLE",
              "description": "Single Room",
              "price": 100.0,
              "quantity": 1
          },
          {
              "type": "DOUBLE",
              "description": "Double Room",
              "price": 150.0,
              "quantity": 1
          }
      ],
      "guestDetails": {
          "adults": 2,
          "children": 1,
          "quantity": 3
      },
      "dateRange": {
          "checkInDate": "2024-12-01T00:00:00.000+00:00",
          "checkOutDate": "2024-12-05T00:00:00.000+00:00"
      }
  }
  ```

### Transaction

Processes a transaction for a reservation.

- **Endpoint**: `POST /api/transactions`
- **Request Header**: `X-API-TOKEN : Token (Mandatory)`
- **Request Body**:
  ```json
  {
      "reservationId": "671efc177a99c639ce4799b2"
  }
  ```
- **Response Body**:
  ```json
  {
    "transactionId": "671f02bffbb9cc3f92866453",
    "reservation": {
      "reservationId": "671f0026fbb9cc3f92866449",
      "accountId": "671efbee7a99c639ce4799b0",
      "roomTypeDetails": [
        {
          "type": "SINGLE",
          "description": "Single Room",
          "price": 100.0,
          "quantity": 1
        },
        {
          "type": "DOUBLE",
          "description": "Double Room",
          "price": 150.0,
          "quantity": 1
        }
      ],
      "guestDetails": {
        "adults": 2,
        "children": 1,
        "quantity": 3
      },
      "dateRange": {
        "checkInDate": "2024-12-01T00:00:00.000+00:00",
        "checkOutDate": "2024-12-05T00:00:00.000+00:00"
      }
    },
    "totalAmount": 250.0,
    "status": "PENDING",
    "transactionDate": "2024-10-28T03:19:27.910+00:00"
  }
  ```

## Kafka Integration

This service integrates with Kafka to send notifications when a reservation is created. Ensure Kafka is set up and running to receive messages from the `reservation-created` topic.
