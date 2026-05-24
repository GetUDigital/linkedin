# Internal Working of Spring Boot

> A beginner-friendly guide to understanding how Spring Boot works under the hood.

---

## Table of Contents

1. [What is a Bean?](#what-is-a-bean)
2. [Dependency Injection (DI)](#dependency-injection-di)
3. [IoC Container](#ioc-container)
4. [Application Context](#application-context)
5. [Component Scanning](#component-scanning)
6. [Auto-Configuration](#auto-configuration)
7. [What Happens When You Run a Spring Boot Application?](#what-happens-when-you-run-a-spring-boot-application)
8. [@Component in Depth](#component-in-depth)
9. [Handling Multiple Implementations](#handling-multiple-implementations)

---

## High-Level Internal Flow

Here is a bird's-eye view of how Spring Boot wires everything together:

```
Plain Java Classes (POJOs)
 ├── StripePayment
 └── RazorpayPayment
          │
          ▼
   Component Scanning          ← Spring scans your code for annotated classes
          │
          ▼
      IoC Container             ← Spring creates and manages objects (beans)
          ▲
          │
 External Configurations
 ├── Environment variables
 └── application.properties / application.yml
          │
          ▼
  Application Context
   (Your collection of ready-to-use Beans)
```

---

## What is a Bean?

In Spring, a **Bean** is simply a Java object that is created and managed by the Spring framework — not by you.

Normally in Java, you create objects yourself:

```java
PaymentService service = new RazorpayPayment();
```

In Spring, you let the framework create and manage it:

```java
@Component
public class RazorpayPayment implements PaymentService { ... }
```

Spring takes care of creating this object, keeping it alive, and injecting it wherever it is needed. You just declare it — Spring does the heavy lifting.

---

## Dependency Injection (DI)

**Dependency Injection** is the mechanism Spring uses to automatically provide an object with everything it needs to function.

Think of it like a restaurant kitchen. Instead of a chef going out to buy ingredients themselves, the ingredients are *delivered* to them. The chef just focuses on cooking.

In code, instead of this:

```java
public class OrderService {
    PaymentService payment = new RazorpayPayment(); // Chef buys their own ingredients
}
```

You write this:

```java
@Component
public class OrderService {
    private final PaymentService payment;

    // Spring "delivers" the PaymentService automatically
    public OrderService(PaymentService payment) {
        this.payment = payment;
    }
}
```

Spring sees that `OrderService` needs a `PaymentService`, finds the right bean, and injects it — no manual wiring needed.

---

## IoC Container

**IoC** stands for **Inversion of Control**. This is the core idea behind Spring.

Normally, *your code* is in control — it decides when to create objects and how to
connect them. With IoC, you *invert* that control and hand it over to the Spring framework.

The **IoC Container** is the engine that:

- Reads your application configuration (annotations, XML, or Java config)
- Creates all the required beans
- Injects dependencies into those beans
- Manages the entire lifecycle of beans (creation → use → destruction)

You write the rules; Spring does the work.

---

## Application Context

The **Application Context** is the IoC Container in action — it is the central registry that holds all your beans once they have been created and wired together.

Think of it as a *map* or *phone directory* of every object your application needs. When any part of your code needs a `PaymentService` or `UserRepository`, it asks the Application Context, which hands back the correct, fully-initialised bean.

Common implementations include:

- `AnnotationConfigApplicationContext` — for pure Java/annotation config
- `SpringApplication` (via `SpringApplication.run(...)`) — the standard Spring Boot entry point

---

## Component Scanning

**Component Scanning** is how Spring *discovers* your beans automatically.

When your application starts, Spring scans the packages in your project, looking for classes annotated with Spring stereotypes. When it finds one, it registers it as a bean in the IoC Container.

By default, Spring Boot scans the package where your main class lives and all sub-packages beneath it.

```java
@SpringBootApplication  // This triggers component scanning
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```

You can also configure the scan location manually:

```java
@ComponentScan(basePackages = "com.myapp.services")
```

---

## Auto-Configuration

**Auto-Configuration** is one of Spring Boot's most powerful features. It automatically sets up your application based on the dependencies (JARs) on your classpath.

For example:

- If Spring Boot detects `spring-data-jpa` on the classpath, it automatically sets up a datasource, entity manager, and transaction manager.
- If it detects `spring-web`, it configures a web server (Tomcat by default).

You can always override this auto-configured setup with your own beans or properties. Auto-Configuration is a *smart default*, not a hard constraint.

This is why you rarely need to write boilerplate configuration code in Spring Boot — it is already done for you.

---

## What Happens When You Run a Spring Boot Application?

When you call `SpringApplication.run(...)`, a lot happens in the background:

1. **Bootstrap** — A `SpringApplication` instance is created.
2. **Load configurations** — Environment variables, `application.properties`, and other external configs are loaded.
3. **Create IoC Container** — An `ApplicationContext` is created.
4. **Component Scanning** — Spring scans your packages for annotated classes.
5. **Auto-Configuration** — Spring Boot applies relevant auto-configurations based on your classpath.
6. **Bean Creation** — All discovered beans are instantiated and dependencies are injected.
7. **Start embedded server** — If it is a web app, the embedded Tomcat/Jetty/Undertow server starts.
8. **Application is ready** — The `ApplicationReadyEvent` fires and your app is live.

---

## @Component in Depth

`@Component` is the most fundamental annotation for telling Spring to manage a class as a bean.

```java
@Component
public class RazorpayPayment implements PaymentService {
    @Override
    public String pay() {
        return "Payment via Razorpay";
    }
}
```

By adding `@Component`, you are telling the Spring container: **"Please create an instance of this class and manage it for me."**

Spring will then:

1. Detect this class during component scanning
2. Instantiate it
3. Register it in the Application Context
4. Inject it wherever it is needed

### Specialised Variants of @Component

Spring provides more descriptive annotations that are built on top of `@Component`. They all work the same way, but the name communicates the *role* of the class:

| Annotation | Intended Use |
|---|---|
| `@Component` | Generic Spring-managed component |
| `@Service` | Business logic / service layer |
| `@Repository` | Data access / database layer |
| `@RestController` | REST API controller (web layer) |
| `@Controller` | MVC controller (web layer) |

Using the right annotation makes your code more readable and also enables additional behaviour (e.g. `@Repository` enables automatic exception translation for database errors).

### Ways to Inject a Bean

**1. Constructor Injection (Recommended)**

This is the preferred approach. It is explicit, easy to test, and the field can be marked `final` to ensure immutability.

```java
@Component
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

**2. Field Injection with @Autowired**

Shorter, but less testable and cannot use `final`.

```java
@Component
public class OrderService {
    @Autowired
    private PaymentService paymentService;
}
```

> **Tip:** Prefer constructor injection for production code. It makes dependencies explicit and supports immutability.

---

## Handling Multiple Implementations

A common situation: you have one interface (`PaymentService`) and multiple implementations (`RazorpayService`, `StripeService`). How does Spring know which one to inject?

### Option 1: @Qualifier

Use `@Qualifier` to name your beans and specify exactly which one you want injected.

```java
@Component("razorpay")
public class RazorpayService implements PaymentService {
    @Override
    public void pay() { System.out.println("Paying via Razorpay"); }
}

@Component("stripe")
public class StripeService implements PaymentService {
    @Override
    public void pay() { System.out.println("Paying via Stripe"); }
}
```

Then when injecting, tell Spring which one to use:

```java
@Component
public class CheckoutService {

    @Autowired
    @Qualifier("razorpay")  // Explicitly pick RazorpayService
    private PaymentService paymentService;
}
```

This gives you fine-grained control at the injection site.

---

### Option 2: @ConditionalOnProperty

A more flexible and production-friendly approach is to conditionally activate a bean based on a configuration property.

In `application.properties`:

```properties
payment.provider=razorpay
```

Then annotate your beans with the condition:

```java
@Component
@ConditionalOnProperty(name = "payment.provider", havingValue = "razorpay")
public class RazorpayPaymentService implements PaymentService {
    @Override
    public String pay() {
        System.out.println("Payment from: Razorpay");
        return "Razorpay Payment";
    }
}

@Component
@ConditionalOnProperty(name = "payment.provider", havingValue = "stripe")
public class StripePaymentService implements PaymentService {
    @Override
    public String pay() {
        System.out.println("Payment from: Stripe");
        return "Stripe Payment";
    }
}
```

Now you can switch payment providers just by changing a single line in your config file — no code changes needed. This pattern is widely used in real-world applications for feature flags and environment-specific configuration.

---

## Summary

| Concept | What It Does |
|---|---|
| **Bean** | A Java object created and managed by Spring |
| **Dependency Injection** | Spring automatically provides objects with what they need |
| **IoC Container** | The engine that creates, wires, and manages beans |
| **Application Context** | The live registry of all your beans |
| **Component Scanning** | Spring auto-discovers annotated classes |
| **Auto-Configuration** | Spring Boot sets up sensible defaults based on your classpath |
| **@Component** | Marks a class to be picked up and managed as a bean |
| **@Qualifier** | Disambiguates which bean to inject when multiple options exist |
| **@ConditionalOnProperty** | Activates a bean only when a config property matches |
