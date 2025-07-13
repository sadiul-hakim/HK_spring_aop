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