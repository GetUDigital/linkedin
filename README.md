# LinkedIn Clone — Spring Boot API

A production-ready REST API built with **Spring Boot 3**, **MySQL** (relational data) and **MongoDB** (documents & feeds).

---

## Tech Stack

| Layer          | Technology                          |
|----------------|-------------------------------------|
| Framework      | Spring Boot 3.2                     |
| Language       | Java 17                             |
| Relational DB  | MySQL 8 + Spring Data JPA           |
| Document DB    | MongoDB + Spring Data MongoDB       |
| Security       | Spring Security + JWT (jjwt 0.12)   |
| Validation     | Jakarta Bean Validation             |
| API Docs       | SpringDoc OpenAPI (Swagger UI)      |
| Build          | Maven                               |

---

## Database Split Strategy

### MySQL stores (structured, relational, transactional)
- `users`, `companies`, `experience`, `education`
- `skills`, `user_skills`
- `connections`, `jobs`, `job_applications`

### MongoDB stores (flexible schema, high write volume)
- `posts` — rich reactions map, visibility, media
- `comments` — threaded replies with parentId
- `conversations` + `messages` — chat history
- `notifications` — high-write, short-lived, TTL-friendly

---

## Getting Started

### 1. Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8 running on `localhost:3306`
- MongoDB running on `localhost:27017`

### 2. Create MySQL database
```sql
CREATE DATABASE linkedin_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
Then run `linkedin_schema.sql` to create all tables.

### 3. Configure credentials
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    username: your_mysql_user
    password: your_mysql_password
  data:
    mongodb:
      uri: mongodb://localhost:27017/linkedin_mongo

app:
  jwt:
    secret: yourSuperSecretKeyAtLeast256BitsLong!
```

### 4. First run — auto DDL
On the very first run, change `ddl-auto` to `create` in `application.yml`:
```yaml
jpa:
  hibernate:
    ddl-auto: create   # change back to 'validate' after first run
```

### 5. Run
```bash
mvn spring-boot:run
```

### 6. Swagger UI
Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## API Endpoints

### Authentication (public)
| Method | Endpoint             | Description         |
|--------|----------------------|---------------------|
| POST   | /api/auth/register   | Register new user   |
| POST   | /api/auth/login      | Login, get tokens   |
| POST   | /api/auth/refresh    | Refresh access token|

### Users (JWT required)
| Method | Endpoint               | Description            |
|--------|------------------------|------------------------|
| GET    | /api/users/me          | Your profile           |
| PUT    | /api/users/me          | Update your profile    |
| GET    | /api/users/{id}        | View a user's profile  |
| GET    | /api/users/search?q=   | Search users           |

### Posts (JWT required)
| Method | Endpoint                      | Description              |
|--------|-------------------------------|--------------------------|
| POST   | /api/posts                    | Create post              |
| GET    | /api/posts/feed               | Public feed (paginated)  |
| GET    | /api/posts/user/{userId}      | User's posts             |
| GET    | /api/posts/{postId}           | Single post              |
| PUT    | /api/posts/{postId}           | Update post              |
| DELETE | /api/posts/{postId}           | Delete post              |
| POST   | /api/posts/{postId}/react     | React to post            |
| DELETE | /api/posts/{postId}/react     | Remove reaction          |

### Comments (JWT required)
| Method | Endpoint                                        | Description       |
|--------|-------------------------------------------------|-------------------|
| POST   | /api/posts/{postId}/comments                    | Add comment       |
| GET    | /api/posts/{postId}/comments                    | Get comments      |
| GET    | /api/posts/{postId}/comments/{id}/replies       | Get replies       |
| DELETE | /api/posts/{postId}/comments/{id}               | Delete comment    |

### Connections (JWT required)
| Method | Endpoint                              | Description               |
|--------|---------------------------------------|---------------------------|
| POST   | /api/connections/request/{receiverId} | Send connection request   |
| PATCH  | /api/connections/{id}/respond         | Accept / reject request   |
| DELETE | /api/connections/{id}                 | Remove connection         |
| GET    | /api/connections                      | Your connections          |
| GET    | /api/connections/pending              | Pending requests          |

### Messaging (JWT required)
| Method | Endpoint                                      | Description            |
|--------|-----------------------------------------------|------------------------|
| POST   | /api/messages/send                            | Send message           |
| POST   | /api/messages/conversations/{id}/reply        | Reply in conversation  |
| GET    | /api/messages/conversations                   | Your conversations     |
| GET    | /api/messages/conversations/{id}              | Messages in convo      |
| GET    | /api/messages/conversations/{id}/unread       | Unread count           |

### Jobs (JWT required)
| Method | Endpoint                    | Description           |
|--------|-----------------------------|-----------------------|
| GET    | /api/jobs                   | Browse jobs           |
| GET    | /api/jobs/search?q=         | Search jobs           |
| GET    | /api/jobs/{jobId}           | Job details           |
| POST   | /api/jobs/{jobId}/apply     | Apply to job          |
| GET    | /api/jobs/my-applications   | Your applications     |

### Notifications (JWT required)
| Method | Endpoint                          | Description              |
|--------|-----------------------------------|--------------------------|
| GET    | /api/notifications                | Get notifications        |
| GET    | /api/notifications/unread-count   | Unread count             |
| PATCH  | /api/notifications/mark-all-read  | Mark all read            |
| DELETE | /api/notifications/read           | Clear read notifications |

---

## Project Structure

```
src/main/java/com/linkedin/api/
├── config/           # SecurityConfig, OpenApiConfig, CurrentUser
├── controller/       # REST controllers (one per domain)
├── document/         # MongoDB documents (Post, Comment, Message…)
├── dto/
│   ├── request/      # Incoming request bodies
│   └── response/     # Outgoing response shapes
├── entity/           # JPA entities (MySQL tables)
├── exception/        # Custom exceptions + GlobalExceptionHandler
├── repository/
│   ├── mongo/        # Spring Data MongoDB repositories
│   └── mysql/        # Spring Data JPA repositories
├── security/         # JwtUtils, JwtAuthFilter, UserDetailsServiceImpl
└── service/          # Business logic layer
```

---

## Security Notes

- All endpoints except `/api/auth/**` require a valid `Bearer` JWT token.
- Passwords are hashed with BCrypt (strength 10).
- Access token TTL: 24 hours. Refresh token TTL: 7 days.
- Change `app.jwt.secret` to a cryptographically random 256-bit value in production.

---

## Next Steps / Extensions

- Add `SkillController` for adding/removing skills from profile
- Add `ExperienceController` / `EducationController` for CRUD on profile sections
- Add `CompanyController` for company management
- Integrate Redis for session / rate limiting
- Add WebSocket support for real-time messaging
- Add file upload (S3 / MinIO) for profile & cover photos
- Add Spring Cache (`@Cacheable`) on profile and feed endpoints
- Write integration tests with `@SpringBootTest` + Testcontainers
