# Spring AOP (Aspect-Oriented Programming) - Latest Tutorial

Aspect-Oriented Programming (AOP) helps you modularize cross-cutting concerns (like logging, security, transactions,
etc.) in your application.

This tutorial uses **Spring AOP with annotations** in a **Spring Boot** project.

## cross-cutting concerns

Cross-cutting concerns are parts of a program that affect multiple layers or components of your application, but they
are not part of the core business logic.

They “cut across” multiple functionalities, hence the name.

### 🔍 Simple Definition:

Cross-cutting concerns are shared concerns that are repeated in multiple places — they should ideally be separated from
business logic to make your code cleaner and easier to maintain.

### ✅ Common Examples of Cross-Cutting Concerns:

Concern What it does

- Logging -> Logs method calls, inputs, outputs, errors, etc.
- Security -> Checks user permissions, roles, authentication
- Transactions -> Begins, commits, or rolls back database transactions
- Error Handling -> Catches and processes exceptions globally
- Caching -> Caches method results to improve performance
- Monitoring / Metrics -> Measures execution time, counts calls, etc.
  Auditing Tracks who did what and when

---

## 1. Core Concepts

### What is AOP?

AOP allows you to separate **cross-cutting concerns** from your main business logic. These concerns affect multiple
parts of an application and are better handled separately.

### Key Terms

* **Aspect**: A class containing cross-cutting logic (like logging).
* **Advice**: The action taken by an aspect (before, after, around method).
* **Join Point**: A specific point in the application where an aspect can be applied (usually a method call).
* **Pointcut**: A predicate expression that matches join points (defines where advice should be applied).
* **Weaving**: Linking aspects with other application types.

---

## 2. Advice Types with Examples

Let's build an example: we have a `UserService` and we want to log actions before and after calling its methods.

### 2.1 Project Structure

```
com.example.demo
├── DemoApplication.java
├── aspect
│   └── LoggingAspect.java
├── service
│   └── UserService.java
```

### 2.2 Setup Dependencies (pom.xml)

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
</dependencies>
```

---

## 3. Example Code

### UserService.java

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void createUser(String name) {
        System.out.println("Creating user: " + name);
    }

    public String getUser() {
        System.out.println("Getting user");
        return "John";
    }

    public void errorMethod() {
        throw new RuntimeException("Something went wrong");
    }
}
```

---

## 4. LoggingAspect.java

### JoinPoint + Pointcut + Different Advices

```java
package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // POINTCUT
    @Pointcut("execution(* com.example.demo.service.UserService.*(..))")
    public void userServiceMethods() {
    }

    // BEFORE ADVICE
    @Before("userServiceMethods()")
    public void beforeAdvice(JoinPoint joinPoint) {
        System.out.println("[Before] Method: " + joinPoint.getSignature().getName());
    }

    // AFTER ADVICE
    @After("userServiceMethods()")
    public void afterAdvice(JoinPoint joinPoint) {
        System.out.println("[After] Method: " + joinPoint.getSignature().getName());
    }

    // AFTER RETURNING ADVICE
    @AfterReturning(pointcut = "userServiceMethods()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        System.out.println("[AfterReturning] Method: " + joinPoint.getSignature().getName() + ", Returned: " + result);
    }

    // AFTER THROWING ADVICE
    @AfterThrowing(pointcut = "userServiceMethods()", throwing = "ex")
    public void afterThrowingAdvice(JoinPoint joinPoint, Throwable ex) {
        System.out.println("[AfterThrowing] Method: " + joinPoint.getSignature().getName() + ", Exception: " + ex.getMessage());
    }

    // AROUND ADVICE
    @Around("userServiceMethods()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("[Around - Before] Method: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        System.out.println("[Around - After] Method: " + joinPoint.getSignature().getName());
        return result;
    }
}
```

---

## 5. Output Example

Calling the `getUser()` method:

```text
[Around - Before] Method: getUser
[Before] Method: getUser
Getting user
[After] Method: getUser
[AfterReturning] Method: getUser, Returned: John
[Around - After] Method: getUser
```

Calling the `errorMethod()`:

```text
[Around - Before] Method: errorMethod
[Before] Method: errorMethod
[After] Method: errorMethod
[AfterThrowing] Method: errorMethod, Exception: Something went wrong
```

## 6. Pointcut Designators (execution, within, this, etc.)

Spring AOP uses expressions to match join points. These expressions often begin with a **pointcut designator**, like
`execution`, `within`, etc.

### 📌 `execution` Designator

Matches method execution join points.

```java
@Around("execution(* com.example.demo.MyService.greet(..))")
```

This matches:

* Any return type (`*`)
* In `MyService`
* Named `greet`
* Any arguments (`(..)`)

### ✅ Common Designators

| Designator         | Description                                                           |
|--------------------|-----------------------------------------------------------------------|
| `execution(...)`   | Match method execution (most common)                                  |
| `within(...)`      | Match all join points within a certain type (class)                   |
| `this(...)`        | Match proxies implementing a certain type                             |
| `target(...)`      | Match target objects implementing a type                              |
| `args(...)`        | Match methods taking specific argument types                          |
| `@target(...)`     | Match classes annotated with a specific annotation                    |
| `@within(...)`     | Match types (classes) annotated with a specific annotation            |
| `@annotation(...)` | Match methods annotated with a specific annotation                    |
| `@args(...)`       | Match where method arguments are annotated with a specific annotation |

### 🧪 Example Combinations

```java
@Around("execution(* *(..)) && within(com.example.demo..*)")
```

Matches all methods inside `com.example.demo` package and sub-packages.

```java
@Before("@annotation(org.springframework.transaction.annotation.Transactional)")
```

Runs before any method annotated with `@Transactional`.

```java
@Around("execution(* *(..)) && args(java.lang.String)")
```

Matches any method that takes a single `String` argument.

---

## 7. Annotation-Based vs XML Configuration

### ✅ Annotation-Based AOP (Recommended for Spring Boot)

* Uses `@Aspect`, `@Before`, `@After`, etc.
* Supports custom annotations (`@Around("@annotation(...)`)\`)
* Easy to read and maintain

### 🧾 XML-Based AOP (Legacy style)

* Uses XML configuration in `applicationContext.xml`
* Useful in non-annotation projects

#### Example:

```xml

<aop:config>
    <aop:aspect ref="loggingAspect">
        <aop:pointcut id="logPointcut" expression="execution(* com.example.service.*.*(..))"/>
        <aop:before pointcut-ref="logPointcut" method="logBefore"/>
    </aop:aspect>
</aop:config>
```

```java
public class LoggingAspect {
    public void logBefore() {
        System.out.println("Logging before method");
    }
}
```

**Note**: XML config is rarely used today unless maintaining a legacy codebase.

---

## Summary

| Advice Type       | Description                                        |
|-------------------|----------------------------------------------------|
| `@Before`         | Runs before the method                             |
| `@After`          | Runs after the method (even if exception occurs)   |
| `@AfterReturning` | Runs after method returns successfully             |
| `@AfterThrowing`  | Runs if method throws an exception                 |
| `@Around`         | Wraps the method, allows custom before/after logic |

Use AOP to cleanly separate concerns like logging, monitoring, transactions, etc.

## @Around

@Around is the most powerful type of advice in Spring AOP. It wraps the method execution — you can run logic before,
after, and even prevent or alter method execution.

It uses ProceedingJoinPoint, which allows you to control whether the target method actually runs.

## 🧠 Typical Use Cases:

- Measuring method execution time
- Security checks (e.g., deny execution based on condition)
- Input validation
- Result modification
- Logging both input/output

## How @Around advice can run code before, after, prevent execution, or alter results.

We’ll use this simple service method as our target:

```java
public class MyService {
    public String greet(String name) {
        System.out.println("Inside greet(): Hello " + name);
        return "Hello " + name;
    }
}
```

`joinPoint.proceed()` is the main magic

### ✅ 1. Run Logic Before Method

You just write code before calling proceed().

```java

@Around("execution(* com.example.demo.MyService.greet(..))")
public Object beforeExample(ProceedingJoinPoint joinPoint) throws Throwable {
    System.out.println("[Before] About to call: " + joinPoint.getSignature().getName());
    return joinPoint.proceed(); // Call the actual method
}

```

```
[Before] About to call: greet
Inside greet(): Hello John
```

### ✅ 2. Run Logic After Method

Write code after proceed():

```java

@Around("execution(* com.example.demo.MyService.greet(..))")
public Object afterExample(ProceedingJoinPoint joinPoint) throws Throwable {
    Object result = joinPoint.proceed(); // Call the method
    System.out.println("[After] Method finished: " + joinPoint.getSignature().getName());
    return result;
}

```

```
Inside greet(): Hello John
[After] Method finished: greet
```

### ✅ 3. Prevent Method Execution

Just don't call proceed():

```java

@Around("execution(* com.example.demo.MyService.greet(..))")
public Object preventExecutionExample(ProceedingJoinPoint joinPoint) throws Throwable {
    System.out.println("[BLOCKED] Method call prevented: " + joinPoint.getSignature().getName());
    return "Blocked by AOP";
}
```

```
[BLOCKED] Method call prevented: greet
```

### ✅ 4. Alter/Modify Result

Change what the method returns:

```java

@Around("execution(* com.example.demo.MyService.greet(..))")
public Object modifyResultExample(ProceedingJoinPoint joinPoint) throws Throwable {
    Object originalResult = joinPoint.proceed();
    System.out.println("Original result: " + originalResult);
    return "[Modified] " + originalResult;
}
```

```
Inside greet(): Hello John
Original result: Hello John
```

```
[Modified] Hello John
```

### ✅ 5. Catch and Handle Exception

Wrap proceed() in a try-catch block:

```java

@Around("execution(* com.example.demo.MyService.greet(..))")
public Object exceptionHandlingExample(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
        return joinPoint.proceed();
    } catch (Throwable ex) {
        System.out.println("[Caught Exception] " + ex.getMessage());
        return "Fallback response due to error";
    }
}
```

## @Around Advice Summary Table

| Behavior         | Code Placement                      | `proceed()` Called? |
|------------------|-------------------------------------|---------------------|
| Before logic     | Before `proceed()`                  | ✅                   |
| After logic      | After `proceed()`                   | ✅                   |
| Skip execution   | Don't call `proceed()`              | ❌                   |
| Modify return    | Wrap result and return modified     | ✅                   |
| Handle exception | Surround `proceed()` with try-catch | ✅ / ❌               |
