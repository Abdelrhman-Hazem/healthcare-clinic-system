# Healthcare Clinic System

Healthcare Clinic System showcasing Spring Boot REST best practices for the KFH assessment. It covers patient onboarding, doctor lookup, appointment scheduling and secure session-based authentication with JWT and active token tracking.

## Features
- Full CRUD workflow for patients (registration, listing with appointments, soft delete)
- Doctor lookup backed by cached asynchronous service and profile-based data seeding
- Appointment scheduling (async) and updates with validation and audit logging
- JWT-based authentication with session management (10-minute sessions, 3-minute tokens with refresh capability)
- Redis-based caching and session management
- Swagger/OpenAPI, global exception handling, validation, async execution, Spring profiles (dev/test/prod), DevTools (dev only) and Actuator
- H2 in-memory DB for dev/test, pluggable JDBC for prod
- Layered architecture with MapStruct for DTO mapping

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- Redis (for caching and session management)

### Running the Application
```bash
./mvnw clean spring-boot:run       # runs with dev profile by default
SPRING_PROFILES_ACTIVE=test ./mvnw test
```

### Default Credentials (dev/test profiles)
- Username: `admin`
- Password: `Admin@123`

### Authentication Flow
1. **Login**: Call `/api/auth/login` to obtain a JWT token
2. **Use Token**: Include `Authorization: Bearer <token>` header in all authenticated requests
3. **Refresh Token**: Call `/api/auth/refresh` before token expires (within 3 minutes) to get a new token
4. **Logout**: Call `/api/auth/logout` to revoke the active token

**Note**: JWT tokens expire after 3 minutes, but sessions remain active for 10 minutes. Use the refresh endpoint to get a new token before expiration.

## Key Endpoints

### Postman Collection
Complete postman collection with examples [here](https://documenter.getpostman.com/view/26735712/2sB3dJyroR)

### Authentication Endpoints (Public)
| Endpoint | Method | Description |
| --- | --- | --- |
| `/api/auth/login` | POST | Login and receive JWT token |
| `/api/auth/refresh` | POST | Refresh JWT token (requires valid session) |
| `/api/auth/logout` | POST | Revoke active token |

### Protected Endpoints (Require Authentication)
| Endpoint | Method | Description | Auth Required |
| --- | --- | --- | --- |
| `/api/doctors` | GET | Cached doctor lookup | ✅ Yes |
| `/api/patients` | POST | Register patient | ✅ Yes |
| `/api/patients` | GET | List patients with appointments  | ✅ Yes |
| `/api/patients/{id}` | DELETE | Soft-delete patient | ✅ Yes |
| `/api/appointments` | POST | Schedule appointment (async) | ✅ Yes |
| `/api/appointments/{id}` | PUT | Update appointment | ✅ Yes |

**All protected endpoints require**: `Authorization: Bearer <token>` header

### Public Endpoints
- `/swagger-ui.html` - Swagger UI documentation
- `/api-docs` - OpenAPI specification
- `/actuator/**` - Actuator endpoints
- `/h2-console/**` - H2 database console (dev only)

## Authentication & Security

### JWT Session Management
- **Session Duration**: 10 minutes
- **Token Duration**: 3 minutes
- **Token Refresh**: Available via `/api/auth/refresh` endpoint
- **Session Storage**: Redis-backed distributed session management

### Error Responses
- **403 Forbidden**: Invalid or missing token

### Example Request
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}'

# Get Doctors (requires authentication)
curl -X GET http://localhost:8080/api/doctors \
  -H "Authorization: Bearer <token>"
```

## Architecture

### Package Structure
- `com.kfh.clinic.api`: REST controllers, API DTOs, and API-to-Application mappers
- `com.kfh.clinic.application`: Business logic services, application DTOs, and Application-to-Infrastructure mappers
- `com.kfh.clinic.infrastructure`: Entities, repositories, security configuration, JWT services, and data seeding
- `com.kfh.clinic.config`: Application configuration (Redis, Security, OpenAPI, etc.)

### Key Technologies
- **Java 17**
- **Spring Boot 3.5**
- **Spring Cache**: Annotation-based caching with Redis backend
- **Spring @Async**: Asynchronous method execution
- **MapStruct**: Type-safe DTO mapping between layers
- **Redis**: Distributed caching and session management
- **JWT**: Token-based authentication
- **SpringDoc OpenAPI**: API documentation

## Testing
```bash
./mvnw test
```

### Test Coverage
- Unit tests for all service layer methods
- Tests cover success scenarios, validation errors, and edge cases
- Mock-based testing with Mockito

## Profiles

### Development (`dev`)
- H2 in-memory database
- Auto-seeded doctors and admin user
- Redis caching enabled
- Spring DevTools enabled
- SQL logging enabled
- H2 console enabled

### Test (`test`)
- Isolated H2 in-memory database
- Lightweight settings for unit tests
- Redis connection configured
- No DevTools

### Production (`prod`)
- External database (configured via environment variables)
- Redis for caching and sessions
- Environment-driven secrets
- No DevTools
- SQL logging disabled

## Configuration

### Redis Configuration
Redis is used for:
- **Session Management**: Active JWT session tracking
- **Caching**: Doctors and patients data caching

Redis connection is configured via `spring.data.redis.*` properties in profile-specific property files.

### JWT Configuration
- Secret key: Configured via `clinic.jwt.secret`
- Token validity: 3 minutes (`clinic.jwt.access-token-validity-minutes`)
- Session validity: 10 minutes (`clinic.jwt.session-validity-minutes`)

## Possible Enhancements
- JwtToken to be refreshed with each API call
- Http status 401 for unauthenticated & 403 for unauthorized 
- Better Data validation, e.g. regex for email & phone number
- Better handling for Async call; exception, use msg queue
- Appointment timeslots
- Prevent double booking of doctor time OR patient time
- Soft delete appointment with deleting the user
