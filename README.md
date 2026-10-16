# Automated Withdrawal System

A full-stack withdrawal notice system for **Mabilo Investments**. Investors can view their portfolio, submit withdrawal notices that are validated against business rules, review their withdrawal history, and download a CSV statement.

Built with **Spring Boot** (backend) and plain **HTML / CSS / JavaScript** (frontend).

## Features

- **Portfolio dashboard**: an investor's name, age, total balance and investment products
- **Withdrawal form**: submits a withdrawal notice, with client-side and server-side validation
- **Withdrawal history**: a table of all of an investor's withdrawals, newest first
- **CSV statement**: downloadable, with optional filters for type and date range
- **Clear error messages**: business rule failures and validation errors are returned as consistent JSON and shown on the page

## Business rules

| Rule | Behaviour |
|---|---|
| Retirement age | `RETIREMENT` withdrawals are only allowed if the investor's age is **over 65** (exactly 65 is rejected) |
| Balance limit | A withdrawal may not exceed the portfolio balance |
| 90% cap | A withdrawal may not exceed **90%** of the portfolio balance (exactly 90% is allowed) |

All money values use `BigDecimal` and `compareTo`, never `double`, to avoid floating-point rounding errors.

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot, Spring Web, Spring Data JPA, Bean Validation |
| Database | H2 (in-memory) |
| Frontend | HTML, CSS, JavaScript (`fetch` API) |
| Testing | JUnit 5, Mockito |
| Build | Maven (Maven Wrapper included) |
| Other | Lombok |

## Project structure

```
Automated-Withdrawal-System/
├── backend/
│   └── src/
│       ├── main/java/com/mabilo/withdrawal_system/
│       │   ├── config/        CORS configuration and seed data
│       │   ├── controller/    REST endpoints
│       │   ├── dto/           request and response objects
│       │   ├── exception/     custom exceptions and the global handler
│       │   ├── model/         JPA entities
│       │   ├── repository/    Spring Data repositories
│       │   └── service/       business rules
│       └── test/              unit tests
├── frontend/
│   ├── index.html
│   ├── css/style.css
│   └── js/script.js
└── README.md
```

The backend follows a layered design: **controller → service → repository → database**. Controllers contain no business logic, the service layer holds the rules, and DTOs keep the JPA entities out of the API.

## Getting started

### Prerequisites

- JDK 21
- Git
- VS Code with the **Live Server** extension (or any static file server that can serve the `frontend` folder on port 5500)

### 1. Clone the repository

```bash
git clone https://github.com/XolaniMba/Automated-Withdrawal-System.git
cd Automated-Withdrawal-System
```

### 2. Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

On Windows PowerShell, use `.\mvnw.cmd spring-boot:run`.

The API starts on `http://localhost:8080`. The H2 database is in-memory and is **reset on every restart**. Two test investors are loaded at startup (see below).

### 3. Run the frontend

Open `frontend/index.html` in VS Code, right-click, and choose **Open with Live Server**. The page opens on `http://127.0.0.1:5500`.

The backend only allows requests from `http://localhost:5500` and `http://127.0.0.1:5500` (CORS), so the page needs to be served on port 5500.

### 4. Run the tests

```bash
cd backend
./mvnw test
```

## Test data

| ID | Investor | Age | Portfolio balance | Products |
|---|---|---|---|---|
| 1 | Thabo Mokoena | 70 | R500,000.00 | Retirement Annuity (R300,000), Unit Trust (R200,000) |
| 2 | Naledi Dlamini | 40 | R120,000.00 | Tax-Free Savings (R120,000) |

Investor 1 can make retirement withdrawals. Investor 2 cannot, because she is under 65.

## API documentation

Base URL: `http://localhost:8080/api`

### Get an investor's portfolio

`GET /investors/{id}/portfolio`

```json
{
  "investorName": "Thabo Mokoena",
  "investorAge": 70,
  "balance": 500000.00,
  "products": [
    { "id": 1, "name": "Retirement Annuity", "type": "RETIREMENT", "balance": 300000.00 },
    { "id": 2, "name": "Unit Trust", "type": "GENERAL", "balance": 200000.00 }
  ]
}
```

Returns `200 OK`, or `404 Not Found` if the investor or portfolio does not exist.

### Create a withdrawal notice

`POST /withdrawals`

Request body:

```json
{ "investorId": 1, "amount": 100000, "type": "RETIREMENT" }
```

| Field | Rules |
|---|---|
| `investorId` | required |
| `amount` | required, must be greater than zero |
| `type` | required, not blank (`RETIREMENT` or `GENERAL`) |

Response (`201 Created`):

```json
{
  "id": 1,
  "status": "PENDING",
  "type": "RETIREMENT",
  "amount": 100000,
  "dateRequested": "2026-10-15T10:06:50.123456"
}
```

### List an investor's withdrawals

`GET /investors/{id}/withdrawals`

Returns `200 OK` with a list of withdrawals (newest first), or `404 Not Found` if the investor does not exist.

### Export a CSV statement

`GET /investors/{id}/withdrawals/export`

| Query parameter | Optional | Example | Effect |
|---|---|---|---|
| `type` | yes | `GENERAL` | only that withdrawal type (case-insensitive) |
| `from` | yes | `2026-10-01` | withdrawals on or after this date |
| `to` | yes | `2026-10-31` | withdrawals on or before this date |

Returns `200 OK` with `Content-Type: text/csv` and `Content-Disposition: attachment; filename=withdrawals.csv`:

```
Id,Type,Amount,Status,Date Requested
2,GENERAL,20000.00,PENDING,2026-10-15T10:07:30.456789
1,RETIREMENT,100000.00,PENDING,2026-10-15T10:06:50.123456
```

### Error responses

All errors use the same shape:

```json
{
  "timestamp": "2026-10-15T10:08:12.345678",
  "status": 400,
  "error": "Bad Request",
  "message": "Withdrawal amount cannot exceed 90% of your balance"
}
```

| Status | When |
|---|---|
| `400 Bad Request` | a business rule is broken, or the request body fails validation (the message lists each invalid field) |
| `404 Not Found` | the investor or portfolio does not exist |

### Quick test with curl

```bash
curl http://localhost:8080/api/investors/1/portfolio

curl -X POST http://localhost:8080/api/withdrawals \
  -H "Content-Type: application/json" \
  -d '{"investorId":1,"amount":100000,"type":"RETIREMENT"}'

curl "http://localhost:8080/api/investors/1/withdrawals/export?type=RETIREMENT"
```

## Testing

`WithdrawalServiceTest` uses JUnit 5 and Mockito (repositories are mocked, so no database is needed) and covers:

- a valid withdrawal is saved with status `PENDING`
- a retirement withdrawal at age 65 is rejected (boundary case)
- an amount over the balance is rejected
- an amount over 90% of the balance is rejected
- an amount of exactly 90% is allowed (boundary case)
- an unknown investor returns "not found"

## Advanced requirements covered

- Global exception handling (`@RestControllerAdvice`)
- DTO layer
- Input validation (Bean Validation on the request DTO)
- Unit tests
- UI validation (the form checks the amount and type before calling the API)

## Screenshots

<!-- Screenshots will be added here once the project is complete. -->

## AI usage disclosure

AI was used in this project and is disclosed here as required.

**Tool:** Claude (Anthropic), used as a step-by-step tutor and pair-programming assistant.

**How it was used:**
- explaining concepts (JPA relationships, DTOs, the controller/service/repository layers, Bean Validation, Mockito, CORS) before each step was built
- proposing the project structure and providing starter code for several classes, which were then explained to me
- reviewing code I wrote, and pointing out compile errors, logic errors and copy-paste mistakes
- helping to diagnose build, git and path problems
- drafting pull request descriptions and this README

**What I did myself:** I typed all of the code into the project, ran and tested it, and fixed the problems that came up. Parts I wrote or corrected myself after review include the repository interfaces, the DTO fields and validation annotations, the business-rule conditions in `WithdrawalService`, the CSV filtering logic, the history and CSV download code on the frontend, and the unit tests (including fixing failing tests). Parts that were supplied as starter code and then explained to me include the service and controller skeletons, the CORS configuration, the seed data and the stylesheet.

I understand the code in this repository and can explain how each part works.

## Known limitations

- The H2 database is in-memory, so all data is lost when the backend restarts
- Withdrawals are saved with status `PENDING`, and the portfolio balance is not reduced, so there is no approval flow yet
- There is no authentication, and the investor is chosen by ID on the page
- The CORS configuration only allows the Live Server origins on port 5500