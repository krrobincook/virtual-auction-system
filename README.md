# Virtual Auction System

## Project Overview

The Virtual Auction System is a Spring Boot backend for conducting online auctions. Users can register, authenticate with JWT, create and manage auctions, place bids, view bidding information, and receive real-time auction updates over WebSocket/STOMP.

## Features

- User registration
- JWT authentication
- Role-based authorization with `USER` and `ADMIN` roles
- Auction creation and management
- Automatic auction timer and lifecycle transitions
- Auction statuses: `PENDING`, `ACTIVE`, `ENDED`, and `CANCELLED`
- Bid placement through REST APIs
- Bidding history
- Highest-bid lookup
- Transactional concurrent-bid protection with database locking
- Real-time WebSocket/STOMP auction updates
- Global exception handling with consistent JSON error responses
- API documentation in this README

## Technology Stack

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- WebSocket/STOMP
- Maven
- Lombok

## Architecture

REST requests follow the standard layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL
```

Successful bid and auction lifecycle changes are also published to subscribed clients:

```text
REST request
    ↓
Business logic and transaction
    ↓
Database commit
    ↓
WebSocket event
    ↓
Connected clients
```

Bid placement remains REST-based and authoritative. WebSocket is used to broadcast successful updates after the transaction commits.

## Project Structure

```text
src/main/java/com/auction/virtual_auction_system/
├── config/       Spring Security and WebSocket configuration
├── controller/   REST controllers and request/response DTOs
├── entity/       JPA entities and enums
├── event/        Transactional auction events and WebSocket publisher
├── exception/    Custom exceptions, error DTO, and global handler
├── repository/   Spring Data JPA repositories
├── security/     JWT service and authentication filter
└── service/      Business services, implementations, and scheduler
```

## Database

The application uses MySQL and Spring Data JPA/Hibernate. The main entities are:

- `User` — stores the user's name, unique email, BCrypt password, role, and creation time.
- `Auction` — stores auction details, seller, starting/current price, start/end times, and lifecycle status.
- `Bid` — stores the bid amount, bid time, bidder, and related auction.

`Auction.currentPrice` is updated when a valid bid is accepted. Bidding uses transactional locking and price validation to protect concurrent bids.

## Authentication

Registration and login are available without a JWT. After login, the API returns a JWT token. Send it with protected requests using:

```http
Authorization: Bearer <JWT_TOKEN>
```

Passwords are stored using BCrypt. JWT authentication is stateless, and failed authentication/authorization requests return the standard API error format.

## API Endpoints

Base URL during local development: `http://localhost:8080`

### Authentication

| Method | Endpoint | Authentication | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a new user |
| `POST` | `/api/auth/login` | Public | Authenticate and receive a JWT |

Example registration body:

```json
{
  "name": "Amit",
  "email": "amit@example.com",
  "password": "Password@123"
}
```

Example login body:

```json
{
  "email": "amit@example.com",
  "password": "Password@123"
}
```

### Auctions

All auction endpoints require a JWT unless stated otherwise.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auctions` | Create an auction |
| `GET` | `/api/auctions` | List all auctions |
| `GET` | `/api/auctions/{id}` | Get an auction by ID |
| `PUT` | `/api/auctions/{id}` | Update an owned auction when allowed |
| `POST` | `/api/auctions/{id}/cancel` | Cancel an owned upcoming auction |

Example auction body:

```json
{
  "title": "Vintage Camera",
  "description": "A collectible film camera",
  "startingPrice": 30000.00,
  "startTime": "2026-11-01T10:00:00",
  "endTime": "2026-11-01T18:00:00"
}
```

### Bids

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auctions/{auctionId}/bids` | Place a bid on an active auction |
| `GET` | `/api/auctions/{auctionId}/bids` | Get bidding history |
| `GET` | `/api/auctions/{auctionId}/highest-bid` | Get the highest bid |
| `GET` | `/api/auctions/{auctionId}/bids/highest-bid` | Compatibility alias for highest bid |

Example bid body:

```json
{
  "amount": 35000.00
}
```

### Real-Time WebSocket Updates

Connect using native STOMP over WebSocket:

```text
ws://localhost:8080/ws
```

Subscribe to an auction-specific topic:

```text
/topic/auctions/{auctionId}
```

The server publishes `BID_PLACED`, `AUCTION_STARTED`, `AUCTION_ENDED`, and `AUCTION_CANCELLED` messages. A bid event includes the auction ID, bid ID, bidder name, bid amount, current price, status, bid time, and event timestamp. Clients can subscribe to multiple auction topics simultaneously.

## Error Responses

REST errors use a consistent structure:

```json
{
  "timestamp": "2026-10-31T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Auction not found with id: 10",
  "path": "/api/auctions/10"
}
```

Validation failures may additionally include a `validationErrors` object. Stack traces, credentials, JWT secrets, SQL details, and other internal information are not returned to clients.

## Running the Application

1. Create the `virtual_auction` MySQL database.
2. Configure the datasource username/password and JWT settings in `src/main/resources/application.properties`.
3. Start the application with Maven:

```bash
mvn spring-boot:run
```

The default server port is `8080`.

