# Healthcare Clinic System

Healthcare Clinic System showcasing Spring Boot REST best practices for the KFH assessment. It covers patient onboarding, doctor lookup, appointment scheduling and secure session-based authentication with JWT and active token tracking.

## Features
- Full CRUD workflow for patients (registration, listing with appointments, soft delete)
- Doctor lookup backed by cached asynchronous service and profile-based data seeding
- Appointment scheduling and updates with validation and audit logging
- JWT-based login/logout with active session store and security filter chain
- Swagger/OpenAPI, global exception handling, validation, caching, async execution, Spring profiles (dev/test/prod), DevTools (dev) and Actuator
- H2 in-memory DB for dev/test, pluggable JDBC for prod

## Getting Started
```bash
./mvnw clean spring-boot:run       # runs with dev profile by default
SPRING_PROFILES_ACTIVE=test ./mvnw test
```

### Default Credentials (dev/test profiles)
- Username: `admin`
- Password: `Admin@123`

Use `/api/auth/login` to obtain a JWT token, then include `Authorization: Bearer <token>` on subsequent calls. `/api/auth/logout` revokes the active token.

## Key Endpoints
| Endpoint | Method | Description |
| --- | --- | --- |
| `/api/auth/login` | POST | Login and receive JWT |
| `/api/auth/logout` | POST | Revoke active token |
| `/api/patients` | POST | Register patient |
| `/api/patients` | GET | List patients with appointments |
| `/api/patients/{id}` | DELETE | Soft-delete patient |
| `/api/doctors` | GET | Async/cached doctor lookup |
| `/api/appointments` | POST | Schedule appointment |
| `/api/appointments/{id}` | PUT | Update appointment |

Swagger UI available at `/swagger-ui.html` with OpenAPI spec at `/api-docs`.

## Testing
```bash
./mvnw test
```
Includes service-level unit tests for patient registration and appointment scheduling logic.

## Profiles
- `dev`: H2, auto-seeded data, caching via Caffeine
- `test`: isolated H2 DB, lightweight settings for unit tests
- `prod`: external database + environment-driven secrets
