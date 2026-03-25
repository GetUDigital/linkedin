# Spring Boot - Complete Study Guide

## Table of Contents
1. [Core Concepts](#core-concepts)
2. [Dependency Injection & IoC](#dependency-injection--ioc)
3. [Web Development](#web-development)
4. [Data Access & Persistence](#data-access--persistence)
5. [Security & Authentication](#security--authentication)
6. [Advanced Topics](#advanced-topics)
7. [Testing](#testing)
8. [Monitoring & Actuator](#monitoring--actuator)
9. [Configuration Management](#configuration-management)
10. [Microservices & Cloud](#microservices--cloud)

---

## Core Concepts

### Spring Boot Overview
- **What is Spring Boot?**
  - Extension of Spring Framework that simplifies development
  - Provides auto-configuration and embedded servers
  - Reduces boilerplate code and configuration
  
- **Key Features**
  - Auto-configuration
  - Embedded application servers (Tomcat, Jetty, Undertow)
  - Production-ready features
  - Convention over configuration
  - Standalone JAR applications

- **Spring Boot Annotations**
  - `@SpringBootApplication` - Main entry point, combines @Configuration, @EnableAutoConfiguration, @ComponentScan
  - `@EnableAutoConfiguration` - Enables auto-configuration
  - `@SpringBootTest` - Integration testing annotation

- **Application Properties**
  - `application.properties` and `application.yml` files
  - Property sources hierarchy
  - Environment variables and command-line arguments
  - Default values and overrides

### Embedded Servers
- Tomcat (default)
- Jetty
- Undertow
- Server configuration properties (`server.port`, `server.servlet.context-path`)

### Starter Dependencies
- Spring Boot Starters simplify dependency management
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-test`
- Custom starters development

---

## Dependency Injection & IoC

### IoC Container Basics
- **Inversion of Control (IoC)**
  - Framework manages object creation and lifecycle
  - Dependencies are injected rather than created manually
  - Benefits: loose coupling, easier testing, flexibility

- **Bean Definition**
  - Objects managed by Spring container
  - Created, configured, and managed by Spring
  - Lifecycle: instantiation → property injection → initialization → destruction

- **Bean Registration Methods**
  - XML configuration (legacy)
  - Java configuration with `@Configuration` and `@Bean`
  - Annotation-based with `@Component`, `@Service`, `@Repository`, `@Controller`
  - Auto-discovery with `@ComponentScan`

### Dependency Injection Types
- **Constructor Injection** (recommended)
  ```java
  public MyService(Dependency dep) {
      this.dependency = dep;
  }
  ```

- **Setter Injection**
  ```java
  @Autowired
  public void setDependency(Dependency dep) {
      this.dependency = dep;
  }
  ```

- **Field Injection**
  ```java
  @Autowired
  private Dependency dependency;
  ```

### Annotations & Qualifiers
- `@Autowired` - Automatic wiring of dependencies
- `@Inject` - Standard annotation (JSR-330)
- `@Qualifier` - Specify which bean to inject when multiple candidates
- `@Primary` - Mark preferred bean when multiple candidates exist
- `@Lazy` - Lazy initialization of beans
- `@Scope` - Define bean scope (singleton, prototype, request, session)

### Bean Lifecycle
- **Initialization**
  - `@PostConstruct` - Execute after bean construction
  - `InitializingBean` interface
  - `init-method` attribute

- **Destruction**
  - `@PreDestroy` - Execute before bean destruction
  - `DisposableBean` interface
  - `destroy-method` attribute

### Component Stereotypes
- `@Component` - Generic Spring component -- ( that marks a Java class as a Spring-managed bean, allowing Spring to automatically detect and create its instance.)

- `@Service` - Business logic layer
- `@Repository` - Data access layer (includes persistence exception translation)
- `@Controller` - Web request handler
- `@RestController` - REST endpoint handler

### Bean Scopes
- `singleton` - Single instance per Spring container (default)
- `prototype` - New instance every time
- `request` - One instance per HTTP request (web context)
- `session` - One instance per HTTP session (web context)
- `application` - Application-wide singleton
- Custom scopes

### Profiles
- `@Profile` - Activate beans for specific profiles
- `spring.profiles.active` property
- `application-{profile}.properties` files
- Use cases: dev, test, staging, production configurations

---

## Web Development

### Spring MVC

#### Controllers
- `@Controller` - Returns view names
- `@RestController` - Returns JSON/XML (REST API)
- `@RequestMapping` - Map HTTP requests to methods
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`

#### Request Handling
- `@PathVariable` - Extract values from URL path
  ```java
  @GetMapping("/users/{id}")
  public User getUser(@PathVariable Long id) { }
  ```

- `@RequestParam` - Query parameters
  ```java
  @GetMapping("/search")
  public List<Item> search(@RequestParam String query) { }
  ```

- `@RequestBody` - Request body mapping
  ```java
  @PostMapping("/users")
  public User createUser(@RequestBody User user) { }
  ```

- `@RequestHeader` - Header values
  ```java
  @GetMapping("/data")
  public String getData(@RequestHeader String Authorization) { }
  ```

- `@CookieValue` - Cookie values

#### Response Handling
- `ResponseEntity` - Control status codes, headers, body
  ```java
  return new ResponseEntity<>(user, HttpStatus.CREATED);
  ```

- `@ResponseStatus` - HTTP status code annotation
- Content negotiation (JSON, XML, HTML)

#### Validation
- `@Valid` - Trigger validation
- `@Validated` - Method-level validation
- `BindingResult` - Validation errors
- Common annotations:
  - `@NotNull`, `@NotBlank`, `@NotEmpty`
  - `@Size`, `@Min`, `@Max`
  - `@Email`, `@Pattern`
  - `@DateTimeFormat`, `@NumberFormat`
- Custom validators

#### Exception Handling
- `@ExceptionHandler` - Handle specific exceptions in controllers
- `@ControllerAdvice` - Global exception handling
- `@RestControllerAdvice` - Global exception handling for REST
- `ResponseEntityExceptionHandler` - Base class for exception handlers
- Global error responses with custom structures

#### Content Negotiation
- `Accept` and `Content-Type` headers
- `produces` and `consumes` attributes
- Multiple response types (JSON, XML)

### REST API Design
- HTTP methods (GET, POST, PUT, DELETE, PATCH)
- HTTP status codes (200, 201, 400, 404, 500)
- RESTful naming conventions
- Pagination, filtering, sorting
- API versioning strategies

### View Resolution
- Template engines: Thymeleaf, FreeMarker, JSP, Groovy
- View resolver configuration
- Model and View

---

## Data Access & Persistence

### Spring Data JPA

#### Repository Pattern
- `CrudRepository` - Basic CRUD operations
- `JpaRepository` - Extended JPA operations
- `PagingAndSortingRepository` - Pagination and sorting
- Custom repository interfaces

#### Query Methods
- Method naming conventions for automatic query generation
  ```java
  findBy, findAllBy, getBy
  countBy, deleteBy, existsBy
  OrderBy, Top, First, Distinct
  And, Or, Not
  ```

- Example: `List<User> findByEmailAndStatus(String email, Status status)`

#### Custom Queries
- `@Query` annotation - Write custom JPQL queries
  ```java
  @Query("SELECT u FROM User u WHERE u.email = ?1")
  User findByEmail(String email);
  ```

- Native queries with `nativeQuery = true`
- Parameter binding with `@Param`

#### Pagination & Sorting
- `Pageable` interface
- `Page<T>` return type
- `Slice<T>` for large datasets
- `Sort` object for ordering

#### Entity Relationships
- `@OneToOne` - One-to-one relationship
- `@OneToMany` - One-to-many relationship
- `@ManyToOne` - Many-to-one relationship
- `@ManyToMany` - Many-to-many relationship
- Lazy vs Eager loading (`fetch = FetchType.LAZY/EAGER`)
- `@JoinColumn` - Configure foreign key
- Cascade operations (`cascade = CascadeType.*`)
- Orphan removal

#### Entity Configuration
- `@Entity` - Mark class as JPA entity
- `@Table` - Specify table name
- `@Id` - Primary key
- `@GeneratedValue` - Auto-generation strategy
- `@Column` - Column configuration
- `@Transient` - Exclude field from persistence
- `@Embedded`, `@Embeddable` - Value objects

#### Entity Listeners
- `@EntityListeners` - Lifecycle callbacks
- `@PrePersist`, `@PostPersist`
- `@PreUpdate`, `@PostUpdate`
- `@PreRemove`, `@PostRemove`
- `@PostLoad`

### Database Configuration
- JDBC properties (`spring.datasource.*`)
- Connection pooling (HikariCP)
- Multiple data sources
- Database initialization scripts

### Transaction Management
- `@Transactional` - Declarative transaction management
- Transaction propagation (REQUIRED, REQUIRES_NEW, NESTED, etc.)
- Isolation levels (READ_UNCOMMITTED, READ_COMMITTED, etc.)
- Read-only transactions
- Rollback rules
- `TransactionManager` - Programmatic transaction control

### Liquibase & Flyway
- Database migration tools
- Version control for database schema
- Automated schema updates
- Rollback capabilities

### MongoDB & NoSQL
- Spring Data MongoDB
- `@Document` annotation
- Query methods similar to JPA
- `MongoTemplate` for custom queries
- Embedded documents and relationships

---

## Security & Authentication

### Spring Security

#### Core Concepts
- Authentication - Who are you?
- Authorization - What can you do?
- Filter chain architecture
- Security context (`SecurityContextHolder`)

#### Authentication
- `Authentication` interface - Represents authenticated principal
- `AuthenticationProvider` - Authenticates credentials
- `AuthenticationManager` - Manages authentication
- Username/password authentication
- Custom authentication providers

#### Authorization
- Role-based access control (RBAC) - `ROLE_USER`, `ROLE_ADMIN`
- Attribute-based access control (ABAC)
- `@PreAuthorize` - Pre-execution authorization
- `@PostAuthorize` - Post-execution authorization
- `@Secured` - Legacy annotation
- `@RolesAllowed` - JSR-250 annotation
- `hasRole()`, `hasAnyRole()`, `hasAuthority()` expressions

#### Configuration
- `WebSecurityConfigurerAdapter` - Customize security configuration
- `HttpSecurity` - Configure HTTP security
- `AuthenticationManagerBuilder` - Configure authentication
- Method-level security with `@EnableGlobalMethodSecurity`

#### JWT (JSON Web Tokens)
- Token structure: Header.Payload.Signature
- Token generation and validation
- Claims - User information in token
- Expiration and refresh tokens
- JJWT library for JWT implementation

#### OAuth2 & OpenID Connect
- OAuth2 flows (Authorization Code, Implicit, Client Credentials, Resource Owner Password)
- Resource servers and authorization servers
- Access tokens and refresh tokens
- OpenID Connect for authentication

#### Password Encoding
- `PasswordEncoder` interface
- `BCryptPasswordEncoder` - Secure password hashing
- `NoOpPasswordEncoder` - Plain text (not recommended)
- Delegation in multi-algorithm scenarios

#### CORS & CSRF
- Cross-Origin Resource Sharing (CORS)
- CSRF token protection
- `@CrossOrigin` annotation
- CORS configuration in `HttpSecurity`

#### Security Headers
- X-Frame-Options
- X-Content-Type-Options
- X-XSS-Protection
- Strict-Transport-Security (HSTS)

---

## Advanced Topics

### AOP (Aspect-Oriented Programming)

#### Core Concepts
- **Concerns** - Functionality independent from business logic
- **Cross-cutting concerns** - Logging, security, transaction management
- Separation of concerns

#### AOP Terminology
- `Aspect` - Module containing advice and pointcuts
- `Joinpoint` - Point in program execution
- `Pointcut` - Expression matching joinpoints
- `Advice` - Code executed at joinpoint
- `Weaving` - Process of applying aspects

#### Annotations
- `@Aspect` - Declare aspect class
- `@Pointcut` - Define pointcut expression
- `@Before` - Execute before joinpoint
- `@After` - Execute after joinpoint
- `@AfterReturning` - Execute after normal return
- `@AfterThrowing` - Execute after exception
- `@Around` - Execute around joinpoint

#### Pointcut Expressions
- `execution()` - Match method execution
- `args()` - Match method arguments
- `@annotation()` - Match methods with annotation
- `@target()` - Match target class with annotation
- Logical operators: `&&`, `||`, `!`

#### Use Cases
- Logging and monitoring
- Transaction management
- Security checks
- Caching
- Error handling

### Caching

#### Cache Abstraction
- `@Cacheable` - Cache method result
- `@CacheEvict` - Remove cache entries
- `@CachePut` - Update cache
- `@Caching` - Multiple cache operations
- `@CacheConfig` - Class-level cache configuration

#### Cache Providers
- Simple in-memory cache (default)
- Redis
- Memcached
- Caffeine
- Guava
- Hazelcast

#### Cache Key Generation
- Default key generation
- Custom key generation with `@CacheKey`
- SpEL in cache annotations

#### Configuration
- `spring.cache.type` property
- Multiple cache managers
- Cache expiration and size limits

### Scheduling & Async Processing

#### Scheduling
- `@Scheduled` - Schedule method execution
- Fixed rate: `@Scheduled(fixedRate = 5000)`
- Fixed delay: `@Scheduled(fixedDelay = 5000)`
- Cron expressions: `@Scheduled(cron = "0 0 * * * *")`
- `@EnableScheduling` - Enable scheduling

#### Async Processing
- `@Async` - Asynchronous method execution
- `@EnableAsync` - Enable async support
- Return types: `void`, `Future<T>`, `CompletableFuture<T>`, `ListenableFuture<T>`
- Exception handling in async methods
- Custom executor configuration

#### Concurrent Programming
- `CompletableFuture` - Non-blocking operations
- `ReactiveStream` and `Project Reactor`
- Thread pools and executors
- Task executors

### Message Queues

#### RabbitMQ Integration
- `spring-boot-starter-amqp`
- `RabbitTemplate` - Send messages
- `@RabbitListener` - Consume messages
- Queue, exchange, and binding configuration
- Message conversion and serialization

#### Apache Kafka Integration
- `spring-boot-starter-kafka`
- `KafkaTemplate` - Send messages
- `@KafkaListener` - Consume messages
- Topics, partitions, and consumer groups
- Message key and ordering

#### JMS (Java Message Service)
- `spring-boot-starter-jms`
- `JmsTemplate` - Send messages
- `@JmsListener` - Consume messages
- Queue vs Topic messaging

### Event Publishing
- `ApplicationEventPublisher` - Publish events
- `@EventListener` - Listen to events
- Custom events extending `ApplicationEvent`
- Async event listeners
- Transactional event listeners with `@TransactionalEventListener`

---

## Testing

### Unit Testing

#### JUnit 5 (Jupiter)
- `@Test` - Mark test method
- `@DisplayName` - Custom test name
- `@BeforeEach`, `@AfterEach` - Setup and teardown
- `@BeforeAll`, `@AfterAll` - Class-level setup/teardown
- Parameterized tests with `@ParameterizedTest`
- `@ValueSource`, `@CsvSource`, `@MethodSource`

#### Assertions
- `assertEquals()`, `assertNotEquals()`
- `assertTrue()`, `assertFalse()`
- `assertNull()`, `assertNotNull()`
- `assertThrows()` - Exception testing
- `assertTimeout()` - Performance testing
- Hamcrest matchers

#### Mocking
- Mockito library
- `@Mock` - Create mocks
- `@InjectMocks` - Inject mocks into class
- `when().thenReturn()` - Stub method calls
- `verify()` - Assert method calls
- `ArgumentCaptor` - Capture arguments

### Integration Testing

#### Spring Boot Test Slices
- `@SpringBootTest` - Full application context
- `@WebMvcTest` - Web layer only
- `@DataJpaTest` - JPA layer only
- `@DataMongoTest` - MongoDB layer only
- `@JsonTest` - JSON serialization
- `@RestClientTest` - REST client testing

#### Test Configuration
- `@TestConfiguration` - Test-specific configuration
- `@AutoConfigureMockMvc` - MockMvc setup
- `@MockBean` - Mock Spring beans
- `@SpyBean` - Spy on Spring beans
- `TestRestTemplate` - REST API testing

#### MockMvc
- Perform HTTP requests
- `perform()`, `andExpect()`, `andReturn()`
- Status assertions
- Content assertions
- Header assertions
- Model assertions

#### Testing Data Access
- `@DataJpaTest` - Test repository layer
- TestEntityManager
- `@Sql` - Run SQL scripts
- Embedded databases (H2, HSQL)

#### Container Testing
- TestContainers - Docker containers for testing
- Testing with real databases, Redis, Kafka
- Integration testing without mocks

---

## Monitoring & Actuator

### Spring Boot Actuator

#### Core Concepts
- Built-in endpoints for monitoring and management
- Production-ready features
- Metrics and health checks

#### Endpoints
- `/actuator/health` - Application health status
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information
- `/actuator/env` - Environment properties
- `/actuator/loggers` - Logger configuration
- `/actuator/caches` - Cache statistics
- `/actuator/httptrace` - HTTP request/response logs
- `/actuator/threaddump` - Thread dump
- `/actuator/heapdump` - Heap dump

#### Health Indicators
- Built-in indicators: Database, Redis, RabbitMQ, etc.
- Custom health indicators extending `AbstractHealthIndicator`
- Health groups
- Health endpoints configuration

#### Metrics
- Micrometer integration
- Meters: counters, gauges, timers, distribution summaries
- Tags for metric organization
- Custom metrics
- Export to monitoring systems

#### Monitoring Systems Integration
- Prometheus - Metrics collection
- Grafana - Visualization
- InfluxDB - Time-series database
- CloudWatch - AWS monitoring
- Azure Monitor

#### Security of Actuator
- `management.endpoints.web.exposure.include/exclude`
- Securing endpoints with Spring Security
- Role-based access to endpoints

### Logging

#### Logging Configuration
- `application.properties` and `application.yml`
- `logging.level.*` - Set log levels
- `logging.file.name` - Log file location
- `logging.file.max-size` - Log rotation size
- `logging.file.max-history` - Keep days of logs

#### Logging Framework
- SLF4J - Logging facade
- Logback - Default logging implementation
- Log4j2 alternative
- `logback-spring.xml` - Logback configuration

#### Logging Best Practices
- Appropriate log levels (TRACE, DEBUG, INFO, WARN, ERROR)
- Sensitive data protection
- Structured logging
- Performance impact

#### Logging in Code
- Logger injection
- Using SLF4J with placeholders `{}`
- Exception logging
- Performance-aware logging

---

## Configuration Management

### Properties & YAML

#### Properties File
- `application.properties` format
- Key-value pairs
- Hierarchical properties with dots
- Special characters escaping

#### YAML File
- `application.yml` format
- Hierarchical structure
- Lists and maps
- More readable than properties

#### Profile-Specific Configuration
- `application-{profile}.properties`
- `application-{profile}.yml`
- Profile activation: `spring.profiles.active`
- Active profiles from environment, command-line, or IDE

#### Property Sources (Priority)
1. Command-line arguments
2. Java system properties
3. Environment variables
4. `application-{profile}.properties`
5. `application.properties`
6. Default properties

### @ConfigurationProperties

#### Basic Usage
- Bind properties to Java classes
- Type-safe configuration
- Validation support
- IDE auto-completion

#### Features
- `@ConfigurationProperties` annotation
- Prefix specification: `@ConfigurationProperties(prefix = "app")`
- Nested objects
- Collections and maps
- Custom converters
- Property validation with Bean Validation

#### Relaxed Binding
- Property name flexibility
- `app.my-property`, `app.myProperty`, `APP_MY_PROPERTY` equivalence
- Environment variable naming

### Environment & Property Resolution
- `Environment` interface - Access properties programmatically
- `@Value` annotation - Inject single property values
- Property placeholders in configurations
- Default values: `@Value("${app.name:DefaultApp}")`

### External Configuration
- System environment variables
- System properties
- Command-line arguments: `java -jar app.jar --app.name=MyApp`
- Property files in classpath
- Property files outside classpath
- Config Server integration (Spring Cloud)

### Profiles & Conditional Configuration
- `@ConditionalOnProperty` - Bean condition based on property
- `@ConditionalOnClass`, `@ConditionalOnMissingClass`
- `@ConditionalOnBean`, `@ConditionalOnMissingBean`
- `@ConditionalOnResource` - Property-based bean creation

---

## Microservices & Cloud

### Spring Cloud

#### Service Discovery
- Eureka server and clients
- Service registration
- Service discovery
- Health checking
- Load balancing

#### Configuration Server
- Centralized configuration management
- Multiple environments (dev, test, prod)
- Configuration refresh without restart
- Encryption support for sensitive data

#### API Gateway
- Routing requests to microservices
- Spring Cloud Gateway
- Path-based routing
- Rate limiting
- Request/response transformation
- Load balancing

#### Circuit Breaker Pattern
- Resilience4j library
- Hystrix (legacy)
- Detecting failures
- Fallback mechanisms
- State transitions: CLOSED, OPEN, HALF_OPEN
- Metrics and monitoring

#### Load Balancing
- Ribbon (legacy)
- Spring Cloud LoadBalancer
- Round-robin and random strategies
- Custom load balancing rules

#### Message-Driven Architecture
- Event-driven communication
- Spring Cloud Stream
- Publish-subscribe model
- Channel bindings
- Message routing

### Containerization & Deployment

#### Docker
- Dockerfile for Spring Boot applications
- Multi-stage builds
- Image optimization
- Container registry (Docker Hub, AWS ECR)

#### Kubernetes
- Deployment, service, ConfigMap, Secret resources
- Spring Boot on Kubernetes
- Health probes (liveness, readiness)
- Resource requests and limits
- Service mesh integration (Istio)

#### Cloud Platforms
- AWS (EC2, ECS, Lambda)
- Azure (App Service, Container Instances)
- Google Cloud (Cloud Run, Kubernetes Engine)
- Heroku, Cloud Foundry
- Serverless deployment

### Observability

#### Distributed Tracing
- Spring Cloud Sleuth
- Trace ID and span ID
- Integration with Zipkin or Jaeger
- Cross-service tracing

#### Logging Aggregation
- ELK Stack (Elasticsearch, Logstash, Kibana)
- Splunk, DataDog
- Structured logging (JSON)
- Log correlation with trace IDs

#### Metrics & Monitoring
- Prometheus scraping metrics
- Grafana dashboards
- Alerting rules
- Custom metrics

---

## Quick Reference

### Common Annotations
| Annotation | Purpose |
|---|---|
| `@SpringBootApplication` | Main application class |
| `@Component` | Register as Spring bean |
| `@Service` | Business logic layer |
| `@Repository` | Data access layer |
| `@Controller` | Web request handler |
| `@RestController` | REST endpoint handler |
| `@Autowired` | Dependency injection |
| `@Qualifier` | Bean selection |
| `@Configuration` | Java-based configuration |
| `@Bean` | Define bean in configuration |
| `@Profile` | Conditional bean based on profile |
| `@Transactional` | Transaction management |
| `@Async` | Asynchronous execution |
| `@Scheduled` | Method scheduling |
| `@Cacheable` | Cache method result |
| `@PreAuthorize` | Authorization check |
| `@RequestMapping` | HTTP request mapping |
| `@PathVariable` | URL path variable |
| `@RequestParam` | Query parameter |
| `@RequestBody` | Request body mapping |
| `@ResponseBody` | Response body |
| `@ExceptionHandler` | Exception handling |
| `@ControllerAdvice` | Global exception handler |
| `@Entity` | JPA entity |
| `@Table` | Entity table name |
| `@Id` | Primary key |
| `@GeneratedValue` | Auto ID generation |
| `@OneToMany` | One-to-many relationship |
| `@ManyToOne` | Many-to-one relationship |
| `@Test` | JUnit test method |
| `@MockBean` | Mock Spring bean |
| `@SpringBootTest` | Integration test |
| `@WebMvcTest` | Web layer test |

### Common Properties
```yaml
# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/db
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Logging
logging.level.root=INFO
logging.level.com.app=DEBUG
logging.file.name=logs/app.log

# Profiles
spring.profiles.active=dev

# Management (Actuator)
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=always

# Cache
spring.cache.type=redis

# Security
spring.security.user.name=user
spring.security.user.password=password
```

---

## Study Tips

1. **Practice Code** - Write and run code examples for each topic
2. **Build Projects** - Create small projects integrating multiple concepts
3. **Read Documentation** - Refer to official Spring Boot documentation
4. **Interview Questions** - Prepare answers for common interview questions
5. **Real-World Scenarios** - Think about practical use cases
6. **Performance Considerations** - Understand performance implications
7. **Security Best Practices** - Always consider security aspects
8. **Testing** - Write tests for your code
9. **Version Awareness** - Stay updated with latest Spring Boot versions
10. **Community Resources** - Follow Spring blogs and Stack Overflow

---

## Resources

- [Spring Boot Official Documentation](https://spring.io/projects/spring-boot)
- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Baeldung - Spring Boot Tutorials](https://www.baeldung.com/)
- [Spring Boot in Action (Book)](https://www.manning.com/books/spring-in-action)

---

**Last Updated:** March 2026
**Status:** Complete Study Guide
