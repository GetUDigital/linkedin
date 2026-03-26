# Java 8 Features Guide

## Table of Contents
1. [Lambda Expressions](#1-lambda-expressions)
2. [Stream API](#2-stream-api)
3. [Optional Class](#3-optional-class)
4. [Default & Static Methods in Interfaces](#4-default--static-methods-in-interfaces)
5. [New Date/Time API](#5-new-datetime-api)
6. [Collectors](#6-collectors)
7. [CompletableFuture — Chaining](#7-completablefuture--chaining)

---

## 1. Lambda Expressions

**What:** Anonymous functions that allow you to pass behavior as arguments, making code more concise and functional.

### Syntax
```
(parameters) -> expression
```

### Examples

```java
// Runnable with no parameters
Runnable r = () -> System.out.println("Hello");

// Single parameter
Function<String, String> greet = name -> "Hello " + name;

// Multiple parameters
MathOperation add = (a, b) -> a + b;
```

---

## 2. Stream API

**What:** A pipeline for processing sequences of elements with filter, map, reduce operations — supports lazy evaluation.

### Core Operations

| Type | Methods |
|------|---------|
| Intermediate (lazy) | `filter`, `map`, `flatMap`, `sorted`, `distinct`, `limit`, `peek` |
| Terminal (eager) | `collect`, `forEach`, `reduce`, `count`, `findFirst`, `anyMatch`, `toList` |

---

## 3. Optional Class

**What:** A container that may or may not hold a non-null value. Eliminates null checks and `NullPointerException` risks.

### Creating Optional

```java
Optional<String> empty   = Optional.empty();
Optional<String> present = Optional.of("Java 8");
Optional<String> maybe   = Optional.ofNullable(possiblyNullValue);
```

### Using Optional

```java
Optional<String> opt = Optional.of("hello");

// Check and get
if (opt.isPresent()) { System.out.println(opt.get()); }

// Preferred: functional style
opt.ifPresent(System.out::println);

// Provide defaults
String val1 = opt.orElse("default");
String val2 = opt.orElseGet(() -> computeDefault());
String val3 = opt.orElseThrow(() -> new RuntimeException("Missing!"));
```

### Anti-patterns to Avoid

```java
// BAD: defeats the purpose
if (opt.isPresent()) { return opt.get(); }

// GOOD
return opt.orElse(null);

// BAD: Optional as a field or method parameter
class Foo { Optional<String> name; }  // unnecessary overhead

// GOOD: Optional as a return type only
public Optional<User> findById(long id) { ... }
```

---

## 4. Default & Static Methods in Interfaces

**What:** Interfaces can now have concrete method implementations, enabling backward-compatible API evolution.

### Default Methods

```java
public interface Vehicle {
    String getBrand();                   // abstract

    default String describe() {          // concrete — can be overridden
        return "Vehicle: " + getBrand();
    }

    default void start() {
        System.out.println(getBrand() + " starting...");
    }
}

public class Car implements Vehicle {
    public String getBrand() { return "Toyota"; }

    @Override
    public String describe() {           // optional override
        return "Car: " + getBrand();
    }
}
```

### Static Methods

```java
public interface MathOperation {
    int operate(int a, int b);

    static MathOperation add()      { return (a, b) -> a + b; }
    static MathOperation multiply() { return (a, b) -> a * b; }
}

MathOperation add = MathOperation.add();
add.operate(3, 4); // 7
```

---

## 5. New Date/Time API

**What:** `java.time` package — immutable, thread-safe replacements for the old `java.util.Date` and `Calendar`.

### Core Classes

| Class | Description |
|-------|-------------|
| `LocalDate` | Date without time (e.g. 2024-01-15) |
| `LocalTime` | Time without date (e.g. 10:30:00) |
| `LocalDateTime` | Date + time, no timezone |
| `ZonedDateTime` | Date + time + timezone |

---

## 6. Collectors

**What:** Terminal stream operations that accumulate elements into a result container.

> Collectors are the packaging machine that takes stream elements and packs them into collections!

### Examples

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Bob");

// toList / toSet
List<String> list = names.stream().collect(Collectors.toList());
Set<String>  set  = names.stream().collect(Collectors.toSet());

// joining — with delimiter, prefix, suffix
String joined = names.stream().collect(Collectors.joining(", ", "[", "]"));
// "[Alice, Bob, Charlie, Bob]"
```

---

## 7. CompletableFuture — Chaining

**What:** Asynchronous programming without blocking. Chain async operations, handle errors, and combine multiple futures.

### Examples

```java
CompletableFuture<String> result = CompletableFuture
    .supplyAsync(() -> "hello")
    .thenApply(String::toUpperCase)       // transform result
    .thenApply(s -> s + " WORLD");

result.get(); // "HELLO WORLD"

// thenAccept — consume without returning
result.thenAccept(System.out::println);

// thenCompose — chain dependent futures (flatMap equivalent)
CompletableFuture<String> chained = CompletableFuture
    .supplyAsync(() -> "user-123")
    .thenCompose(id -> fetchUserAsync(id));
```
