# ☕ Java Notes — Quick Reference

---

## 📌 Table of Contents
1. [Keywords: final, finally, finalize](#keywords)
2. [WORA — Write Once Run Anywhere](#wora)
3. [Java Program Life Cycle](#lifecycle)
4. [Exceptions](#exceptions)
5. [Static Keyword](#static)
6. [String, StringBuilder, StringBuffer](#strings)
7. [Java 8 Features](#java8)
8. [Streams & Parallel Streams](#streams)
9. [Threads](#threads)
10. [HashMap vs ConcurrentHashMap](#hashmap)
11. [HashMap Internal Working](#hashmap-internal)

---

## 1. Keywords: final, finally, finalize <a name="keywords"></a>

| Keyword | Description |
|---------|-------------|
| `final` | Declares constants, prevents method overriding, or prevents inheritance. Can only be assigned once. |
| `finally` | Block in exception handling that **always executes** after try-catch, regardless of exception. |
| `finalize` | *(Deprecated)* Method in `Object` class called by garbage collector before reclaiming memory. |

> Note: `Public` → `public` (Java is case-sensitive)

---

## 2. WORA — Write Once Run Anywhere <a name="wora"></a>

Java code compiled into **bytecode** can run on any platform (Windows, Mac, Linux, etc.) that has a **JVM (Java Virtual Machine)**, without needing recompilation.

---

## 3. Java Program Life Cycle <a name="lifecycle"></a>

```
EDIT (.java)  →  COMPILE (javac → .class)  →  LOAD (ClassLoader)
     →  VERIFY (Bytecode Verifier)  →  EXECUTE (JIT/Interpreter)
     →  RUN (Output)  →  GARBAGE COLLECTOR (auto cleanup)
```

| Step | Tool / Component | What Happens |
|------|-----------------|--------------|
| Edit | Text Editor | Write `Hello.java` source code |
| Compile | `javac` | Produces `Hello.class` (bytecode) |
| Load | ClassLoader | Loads bytecode into JVM |
| Verify | Bytecode Verifier | Checks for errors/security |
| Execute | JIT / Interpreter | Converts bytecode → machine code |
| Run | JVM | Output printed to console |

---

## 4. Exceptions <a name="exceptions"></a>

> Exceptions are **runtime errors** that disrupt normal program flow (invalid input, file not found, division by zero, etc.)

### Hierarchy

```
Throwable
├── Error (Unrecoverable)
│     └── OutOfMemoryError, StackOverflowError, VirtualMachineError
└── Exception (Recoverable)
      ├── Checked Exception (Compile-time)
      │     └── IOException, SQLException, ClassNotFoundException
      └── Unchecked Exception (Runtime)
            └── NullPointerException, ArithmeticException, ArrayIndexOutOfBoundsException
```

### Types at a Glance

| Type | When Checked | Mandatory Handling | Examples |
|------|-------------|-------------------|----------|
| **Checked** | Compile-time | ✅ Yes (`try-catch` or `throws`) | `IOException`, `SQLException` |
| **Unchecked** | Runtime | ❌ No | `NullPointerException`, `ArithmeticException` |
| **Error** | Runtime | ❌ Should NOT catch | `OutOfMemoryError`, `StackOverflowError` |

---

## 5. Static Keyword <a name="static"></a>

> `static` means the member belongs to the **class itself**, not to individual objects.

### Behavior

| Type | Behavior |
|------|----------|
| Static Variable | One copy shared by **all** objects |
| Static Method | Call without object: `ClassName.method()` |
| Static Block | Runs **once** when class first loads |

### Overloading vs Hiding

- ✅ Static methods **CAN be overloaded** (same name, different parameters)
- ❌ Static methods **CANNOT be overridden** — only **hidden**

```
Parent ref = new Child();
ref.staticMethod()    →  Parent's method  (resolved at COMPILE-TIME via reference type)
ref.instanceMethod()  →  Child's method   (resolved at RUNTIME via object type)
```

### Quick Comparison

| Feature | Instance Method | Static Method |
|---------|----------------|---------------|
| Can be overridden? | YES | NO (only hidden) |
| Can be overloaded? | YES | YES |
| Binding time | Runtime | Compile-time |
| Polymorphism | Yes (dynamic) | No (static) |

---

## 6. String, StringBuilder, StringBuffer <a name="strings"></a>

### Key Differences

| Feature | String | StringBuilder | StringBuffer |
|---------|--------|---------------|--------------|
| Mutability | Immutable | Mutable | Mutable |
| Thread-safe | ✅ YES | ❌ NO | ✅ YES |
| Performance | Slow | Fastest | Medium |
| Synchronized | NO | NO | YES |
| Introduced | Java 1.0 | Java 1.5 | Java 1.0 |
| Best for | Fixed values | Single thread | Multi-thread |

### How String Immutability Works

```java
String s = "Hello";
s.concat(" World");       // New object created but NOT assigned
System.out.println(s);    // Hello (unchanged!)

s = s.concat(" World");   // Now reassigned
System.out.println(s);    // Hello World
```

### When to Use What

- **String** → constant/fixed values
- **StringBuilder** → single-thread string manipulation (fastest)
- **StringBuffer** → multi-thread string manipulation (thread-safe)

### Why is String Immutable?

| Reason | Explanation |
|--------|-------------|
| Security | Used in class loading, network connections |
| Thread Safety | Automatic synchronization |
| String Pool | Caching for performance |
| HashCode caching | Fast hashing for `HashMap` keys |

---

## 7. Java 8 Features <a name="java8"></a>

| Feature | Short Explanation |
|---------|-------------------|
| Lambda Expressions | Write functions as code: `(x, y) -> x + y` |
| Stream API | Process collections with filters & maps |
| Functional Interfaces | Interface with single method (`@FunctionalInterface`) |
| Method References | Shorthand: `System.out::println` |
| Optional Class | Avoid null pointer exceptions |
| Default Methods | Interfaces can have method body |
| Static Methods in Interface | Utility methods in interface |
| New Date/Time API | `LocalDate`, `LocalTime` (immutable) |
| Metaspace | Replaced PermGen (memory management change) |

### Memory Change: PermGen → Metaspace

| | Before Java 8 (PermGen) | Java 8+ (Metaspace) |
|--|------------------------|---------------------|
| Location | Inside Heap | Outside Heap (Native Memory) |
| Size | Fixed (64 MB default) | Auto-growing |
| OOM Errors | Frequent | Rare |
| JVM Flag | `-XX:MaxPermSize` | `-XX:MaxMetaspaceSize` |

---

## 8. Streams & Parallel Streams <a name="streams"></a>

| Feature | Stream | ParallelStream |
|---------|--------|----------------|
| Processing | Single thread | Multiple threads |
| Order maintained? | ✅ YES | ❌ NO |
| Performance (large data) | Slower | Faster |
| Performance (small data) | Better | Overhead |
| Method | `.stream()` | `.parallelStream()` |

### Default Thread Count for ParallelStream

```java
// Uses ForkJoinPool.commonPool()
// Threads = number of CPU cores
Runtime.getRuntime().availableProcessors()
// 4-core CPU → 4 threads | 8-core CPU → 8 threads
```

### When to Use Parallel Streams

| ✅ USE ParallelStream when | ❌ DON'T USE when |
|---------------------------|-----------------|
| Data size > 10,000 items | Small data sets |
| Order doesn't matter | Order is important |
| Heavy complex operations | Simple operations (e.g., print) |
| Multi-core CPU available | Non-thread-safe collections |

```java
list.stream().forEach(n -> System.out.print(n + " "));          // 1 2 3 4 5 (ordered)
list.parallelStream().forEach(n -> System.out.print(n + " "));  // random order
```

---

## 9. Threads <a name="threads"></a>

> A **Thread** is a lightweight sub-process — the smallest unit of execution. Allows multiple tasks to run simultaneously.

### Ways to Create a Thread

| Way | Method | Syntax |
|-----|--------|--------|
| 1 | Extend `Thread` class | `class MyThread extends Thread { public void run() {} }` |
| 2 | Implement `Runnable` | `class MyTask implements Runnable { public void run() {} }` |
| 3 | Lambda (Java 8+) | `Runnable r = () -> {};` |
| 4 | Executor Framework | `Executors.newFixedThreadPool(5);` |

### Thread Class vs Runnable Interface

| Feature | Thread Class | Runnable Interface |
|---------|-------------|-------------------|
| Inheritance | extends Thread | implements Runnable |
| Multiple inheritance | ❌ NO | ✅ YES |
| Code reuse | Difficult | Easy |
| Memory usage | More | Less |
| Recommended for | Simple cases | Production code |

### Thread Lifecycle States

| State | Description |
|-------|-------------|
| `NEW` | Thread created but not started |
| `RUNNABLE` | Running or ready to run |
| `BLOCKED` | Waiting for monitor lock |
| `WAITING` | Waiting indefinitely for another thread |
| `TIMED_WAITING` | Waiting for specified time |
| `TERMINATED` | Execution completed |

---

## 10. HashMap vs ConcurrentHashMap <a name="hashmap"></a>

| Feature | HashMap | ConcurrentHashMap |
|---------|---------|-------------------|
| Thread-safe | ❌ NO | ✅ YES |
| Null key | ✅ 1 allowed | ❌ Not allowed |
| Null values | ✅ Multiple | ❌ Not allowed |
| Performance | Fastest (single thread) | Slower (but safe) |
| Iterator | Fail-fast | Weakly consistent |
| Synchronization | None | Lock striping (16 segments default) |

### When to Use Which

| Use HashMap when | Use ConcurrentHashMap when |
|-----------------|---------------------------|
| Single thread environment | Multiple threads accessing same map |
| Performance is critical | Thread-safety is required |
| Null keys/values needed | Concurrent reads and writes needed |

> **One-liner:** `HashMap` = Non-thread-safe, allows null, best for single thread.  
> `ConcurrentHashMap` = Thread-safe, no null, best for multi-thread with lock striping.

---

## 11. HashMap Internal Working <a name="hashmap-internal"></a>

### How `put(key, value)` Works

```
put(key, value)
  → hashCode() called on key
  → index = hashCode % array_size
  → Go to that bucket
  → If empty → store new Node
  → If occupied (collision) → compare using equals()
      → equals() true  → replace value
      → equals() false → add to LinkedList / Tree
  → If size > threshold (75%) → resize (double capacity)
```

### Collision Handling

- Java 7 and below → **LinkedList** in each bucket
- Java 8+ → **LinkedList** converts to **Balanced Tree** when list size > 8 and array size > 64

| Structure | Search Time |
|-----------|------------|
| LinkedList | O(n) |
| Balanced Tree | O(log n) |

### Resizing

- Default capacity: **16**
- Load factor: **0.75**
- Resize triggers when: `size > capacity × 0.75`
- On resize: capacity **doubles**, all entries rehashed

```
Capacity = 16, Threshold = 12
size hits 13 → RESIZE → new capacity = 32
```

### One-Liner Summary

> HashMap uses an **array of Node buckets**. `hashCode()` finds the bucket. `equals()` finds the exact match. Collisions handled by **chaining** (LinkedList → Tree in Java 8+). Resizes at **75% capacity**.

---

## 🔁 Code Snippets

### Reverse a String

```java
String str = "devesh";
char[] arr = str.toCharArray();
String reverse = "";
for (int i = arr.length - 1; i >= 0; i--) {
    reverse += arr[i];
}
System.out.print(reverse); // hsevEd
```

### All Ways to Create Threads

```java
// Way 1: Extend Thread
class Way1 extends Thread {
    public void run() { System.out.println("Way 1"); }
}
new Way1().start();

// Way 2: Implement Runnable
class Way2 implements Runnable {
    public void run() { System.out.println("Way 2"); }
}
new Thread(new Way2()).start();

// Way 3: Lambda
new Thread(() -> System.out.println("Way 3")).start();

// Way 4: Executor Framework
ExecutorService executor = Executors.newFixedThreadPool(2);
executor.execute(() -> System.out.println("Way 4"));
executor.shutdown();
```

---

*Happy coding! ☕*