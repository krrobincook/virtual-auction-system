# Online Auction System — Project Plan

## 1. Project overview

The Online Auction System is a secure web application that allows authorized users to list items, create auctions, place bids, view bidding history, and monitor auction status in real time.

### Planned stack

- **Backend:** Java 17, Spring Boot, Spring Web, Spring Data JPA/Hibernate, Spring Security, JWT, Bean Validation
- **Database:** MySQL
- **Real-time updates:** WebSocket/STOMP
- **Frontend:** React, React Router, Axios, a shared state solution such as Context API or Redux Toolkit
- **Build and testing:** Maven, JUnit 5, Spring Boot Test, Mockito, React Testing Library

## 2. Current progress baseline

The repository is currently backend-first. The Spring Boot application starts from the generated project structure and user registration is the only partially implemented business flow.

| Area | Current state | Next action |
|---|---|---|
| Spring Boot application | Present; Java 17 and Spring Boot configured | Keep the package structure and complete the domain layers |
| Dependencies | Web, JPA, Security, Validation, MySQL, Lombok, testing are present | Add JWT and WebSocket dependencies when those flows are implemented |
| User entity/repository | Entity and `findByEmail`/`existsByEmail` queries exist | Add authentication, profile validation, and role-based access |
| Registration | `POST /api/auth/register` exists; password hashing and default `USER` role are implemented | Add login, JWT issuance, duplicate/error responses, and tests |
| Auction domain | Entity, repository, service, controller, and status files are placeholders | Implement the auction lifecycle and CRUD APIs |
| Bid domain | Entity, repository, service, controller, and request DTO are placeholders | Implement validation, persistence, winner selection, and history |
| JWT security | Security classes are placeholders; only basic route rules exist | Implement token generation, authentication filter, and stateless security |
| WebSocket | Configuration file is a placeholder | Add STOMP topics for bid and auction-status events |
| Error handling | Exception classes and global handler are placeholders | Add typed exceptions and consistent API error responses |
| React frontend | Not present in the repository yet | Create a separate React app and connect it to the backend |
| Automated tests | Only the generated context test exists | Add unit, integration, security, real-time, and frontend tests |

> Verification note: the Maven test command is currently blocked by the environment trying to create `C:\.m2\repository`. Configure a writable Maven local repository before treating the build as verified.

## 3. Goals and success criteria

### Goals

1. Allow a user to register and securely authenticate.
2. Allow authenticated users to create, update, publish, and monitor auctions.
3. Allow authenticated users to place valid bids on active auctions.
4. Display the current highest bid and complete bidding history for every auction.
5. Provide an accurate countdown timer and close auctions when their end time is reached.
6. Allow users to participate in multiple auctions at the same time.
7. Push bid and auction-status changes to connected clients without requiring manual refresh.
8. Protect private user data and restrict operations according to ownership and role.

### Success criteria

- A user can complete registration, login, auction creation, browsing, bidding, and logout from the React UI.
- A bid is accepted only when the auction is active, the bidder is authorized, and the amount exceeds the current required amount.
- Concurrent bids cannot produce an incorrect winner or lower current price.
- An auction closes exactly once, records its winner when applicable, and rejects later bids.
- Two clients viewing the same auction receive the same current bid and status updates.
- Backend and frontend tests cover the main success and failure paths.

## 4. Functional scope

### Authentication and users

- Register with name, email, and password.
- Login with email and password and receive a JWT access token.
- Load the current user profile.
- Enforce `USER` and `ADMIN` roles.
- Never return password hashes in API responses.

### Auctions

- Create an auction with title, description, image URL or image reference, starting price, optional minimum increment, start time, and end time.
- View paginated active, upcoming, completed, and owned auctions.
- View auction details including current bid and bidding history.
- Update or cancel an auction only when allowed by its lifecycle and ownership rules.
- Close auctions automatically at the end time.

### Bidding

- Place a bid on an active auction.
- Validate that the bid is higher than the current highest bid plus the minimum increment.
- Reject bids from the auction owner, unauthenticated users, closed auctions, and invalid amounts.
- Persist every accepted bid as an immutable history record.
- Show the current highest bidder according to the privacy policy; the full audit history is available to authorized viewers.

### Real-time experience

- Countdown timer is derived from the server-provided end time, not from client-only state.
- Broadcast accepted bids to subscribers of the auction.
- Broadcast auction start, close, cancellation, and winner updates.
- Re-fetch authoritative auction state after reconnecting to avoid stale client state.

## 5. Proposed architecture

### Backend layers

```text
React client
   | REST / WebSocket
Spring Security + JWT filter
   |
Controllers -> DTOs/validation -> Services -> Repositories -> MySQL
                                      |
                              domain events / WebSocket publisher
```

Recommended package additions:

```text
config/
controller/
controller/dto/
entity/
exception/
repository/
security/
service/
service/impl/
event/
mapper/
```

Keep entities internal to the backend boundary. Use response DTOs so persistence fields, password hashes, and implementation details are not exposed directly.

### Frontend structure

```text
src/
  api/             Axios client and endpoint modules
  auth/            Auth context, protected routes, token handling
  components/      Shared UI components
  features/
    auctions/      List, detail, create/edit, timer, filters
    bids/          Bid form and bidding history
  pages/            Login, register, dashboard, auction pages
  hooks/            Countdown, WebSocket, pagination hooks
  routes/           Route definitions and guards
  types/            API/domain types
```

## 6. Core data model

### `User`

- `id`, `name`, `email`, `passwordHash`, `role`, `createdAt`, `updatedAt`
- Unique index on `email`

### `Auction`

- `id`, `title`, `description`, `imageUrl`
- `sellerId` → `User`
- `startingPrice`, `currentPrice`, `minimumIncrement`
- `startTime`, `endTime`, `status`
- `winnerId` → `User` (nullable)
- `createdAt`, `updatedAt`, `version`

Suggested statuses: `DRAFT`, `SCHEDULED`, `ACTIVE`, `CLOSED`, `CANCELLED`.

### `Bid`

- `id`, `auctionId` → `Auction`, `bidderId` → `User`
- `amount`, `placedAt`
- Optional request/client reference for idempotency and audit tracing
- Indexes on `(auction_id, placed_at)` and `(auction_id, amount)`

Use `BigDecimal` for monetary values and UTC timestamps (`Instant` or `OffsetDateTime`) at the API and persistence boundaries.

## 7. Delivery phases

### Phase 0 — Foundation and cleanup

- [ ] Configure a writable Maven repository and verify the current build.
- [ ] Add `application.yml` profiles for local, test, and production settings.
- [ ] Add database migration support, preferably Flyway, and create the initial schema.
- [ ] Replace placeholder classes with explicit interfaces or implementations only when needed.
- [ ] Standardize naming, constructor injection, package casing, and API response conventions.
- [ ] Add a project README with local setup instructions.

**Exit condition:** application starts with a local database and the baseline test suite passes.

### Phase 1 — Authentication and authorization

- [ ] Complete registration validation and conflict handling.
- [ ] Implement login request/response DTOs.
- [ ] Implement `JwtService` for token creation and validation.
- [ ] Implement `JwtAuthenticationFilter` and stateless session configuration.
- [ ] Configure `PasswordEncoder` as a Spring bean and use it through dependency injection.
- [ ] Add `GET /api/users/me`.
- [ ] Apply role and ownership rules to protected endpoints.
- [ ] Add tests for registration, login, invalid credentials, expired tokens, and forbidden actions.

**Exit condition:** only valid JWT-authenticated users can access protected resources, and sensitive fields are not serialized.

### Phase 2 — Auction management

- [ ] Implement `Auction` and `AuctionStatus` as JPA domain types.
- [ ] Implement `AuctionRepository` queries for active, upcoming, completed, and seller-owned auctions.
- [ ] Implement request/response DTOs with validation.
- [ ] Implement auction service rules for create, update, publish, cancel, list, and detail operations.
- [ ] Implement `AuctionController` with pagination, filtering, and sorting.
- [ ] Add scheduled status transitions for start and end times.
- [ ] Add service and integration tests for ownership and lifecycle rules.

**Exit condition:** an authenticated seller can create an auction, and users can browse accurate auction states.

### Phase 3 — Bidding and history

- [ ] Implement `Bid` entity and repository queries.
- [ ] Implement `BidRequest` and bid response DTOs.
- [ ] Implement `BidService` with amount, status, ownership, and authorization validation.
- [ ] Make bid acceptance atomic using a transaction and optimistic locking (`version`) or an equivalent database locking strategy.
- [ ] Update `currentPrice` and winner candidate only after a bid is successfully persisted.
- [ ] Implement `POST /api/auctions/{auctionId}/bids`.
- [ ] Implement `GET /api/auctions/{auctionId}/bids` with pagination and ordering.
- [ ] Add tests for competing bids, equal/lower bids, closed auctions, seller bids, and missing auctions.

**Exit condition:** every accepted bid is durable, ordered, validated, and reflected in the auction’s current price.

### Phase 4 — Real-time updates

- [ ] Add Spring WebSocket/STOMP dependencies and configuration.
- [ ] Define authenticated subscription destinations, for example `/topic/auctions/{auctionId}`.
- [ ] Publish bid events only after the transaction succeeds.
- [ ] Publish lifecycle events for activation, closure, cancellation, and winner selection.
- [ ] Add reconnect and resynchronization behavior in the client.
- [ ] Test that clients receive events and that unauthorized users cannot subscribe to private destinations.

**Exit condition:** multiple clients see accepted bids and status changes without refreshing, while REST remains the source of truth.

### Phase 5 — React frontend

- [ ] Create the React app with TypeScript and a consistent styling/component approach.
- [ ] Add login and registration screens with validation and error states.
- [ ] Add protected routing and token lifecycle handling.
- [ ] Build auction listing with search, status filters, sorting, and pagination.
- [ ] Build auction detail page with server-synchronized countdown, current bid, bid form, and bidding history.
- [ ] Build create/edit auction form with date/time validation.
- [ ] Add dashboard views for owned auctions, active bids, won auctions, and multiple simultaneous auctions.
- [ ] Add WebSocket subscription management, optimistic loading states, reconnect behavior, and toast notifications.
- [ ] Add responsive layouts and accessible form/status components.
- [ ] Add React component and flow tests.

**Exit condition:** a user can complete the primary auction journey without using the API directly.

### Phase 6 — Hardening and release

- [ ] Add integration tests against a disposable MySQL/Testcontainers environment.
- [ ] Add API documentation with OpenAPI/Swagger.
- [ ] Add structured logging, request IDs, and audit logs for authentication and bid events.
- [ ] Add rate limiting or abuse controls for login and bidding endpoints.
- [ ] Validate CORS, secure cookie/token storage strategy, HTTPS, secret management, and production headers.
- [ ] Add CI checks for formatting, tests, dependency vulnerabilities, and frontend build.
- [ ] Containerize backend, frontend, and database for local/staging environments.
- [ ] Add monitoring for failed bids, WebSocket disconnects, auction closure jobs, and database errors.
- [ ] Run an end-to-end acceptance test with multiple users and concurrent bids.

**Exit condition:** the system is deployable, observable, secure, and verified against the success criteria.

## 8. Initial API contract

All protected endpoints require a valid bearer token unless explicitly noted.

| Method | Endpoint | Purpose | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Register a user | Public |
| POST | `/api/auth/login` | Authenticate and issue JWT | Public |
| GET | `/api/users/me` | Return current profile | User |
| POST | `/api/auctions` | Create an auction | User |
| GET | `/api/auctions` | Search and paginate auctions | Public or User |
| GET | `/api/auctions/{id}` | View auction details | Public or User |
| PUT | `/api/auctions/{id}` | Edit an eligible owned auction | Owner |
| POST | `/api/auctions/{id}/publish` | Publish a draft auction | Owner |
| POST | `/api/auctions/{id}/cancel` | Cancel an eligible auction | Owner/Admin |
| POST | `/api/auctions/{id}/bids` | Place a bid | User |
| GET | `/api/auctions/{id}/bids` | View bidding history | Public or User |
| GET | `/api/users/me/bids` | View the current user’s bids | User |
| GET | `/api/users/me/auctions` | View the current user’s auctions | User |

Use a consistent error shape, for example:

```json
{
  "timestamp": "2026-10-26T12:00:00Z",
  "status": 409,
  "code": "BID_TOO_LOW",
  "message": "Bid must exceed the current price and minimum increment.",
  "path": "/api/auctions/42/bids"
}
```

## 9. Security and data protection checklist

- [ ] Hash passwords with a configured `PasswordEncoder`; never log or return them.
- [ ] Keep JWT signing secrets outside source control.
- [ ] Use short-lived access tokens and define a refresh strategy if required.
- [ ] Validate and normalize email addresses and user input.
- [ ] Enforce authorization in services, not only in the React UI.
- [ ] Check auction ownership for every seller action.
- [ ] Use server time for auction boundaries and monetary decisions.
- [ ] Use transactions and concurrency control for bid acceptance.
- [ ] Configure CORS for known frontend origins only.
- [ ] Avoid exposing private bidder information beyond the agreed product policy.
- [ ] Add audit records for authentication, auction changes, bids, and administrative actions.

## 10. Testing strategy

### Backend

- Unit tests for services, validators, JWT logic, and auction state transitions.
- Repository tests for query correctness.
- MVC tests for request validation, status codes, and error payloads.
- Security tests for public, authenticated, owner-only, and admin-only routes.
- Integration tests for database persistence, scheduled closure, and concurrent bids.

### Frontend

- Component tests for forms, timers, bid history, loading, errors, and empty states.
- API mocking tests for success, validation errors, expired sessions, and conflict responses.
- WebSocket tests for updates, reconnects, and resynchronization.
- End-to-end tests for register → login → create auction → bid → close auction.

### Important edge cases

- Two bids arrive at nearly the same time.
- A bid arrives exactly at the auction end boundary.
- A user reconnects after missing real-time events.
- An auction has no bids when it closes.
- A seller tries to bid on their own auction.
- A user opens several auction tabs simultaneously.
- A client clock differs significantly from the server clock.

## 11. Suggested implementation order

Work vertically so each milestone produces a usable slice:

1. Registration and login API plus tests.
2. JWT protection and current-user endpoint.
3. Auction create/list/detail flow.
4. Bid placement and bidding-history flow.
5. Auction closure and winner selection.
6. React authentication and auction browsing.
7. React bid flow and multiple-auction dashboard.
8. WebSocket updates and reconnect handling.
9. Hardening, documentation, deployment, and end-to-end verification.

## 12. Definition of done

A feature is complete when its backend service, API contract, authorization rules, persistence behavior, UI state, automated tests, and error states are implemented. The feature must also be documented and verified locally with the configured database and test commands.

The release is complete when a new user can register, log in, browse multiple auctions, open an auction detail page, place valid bids, see the complete history and live current price, and observe the auction close with the correct winner.

