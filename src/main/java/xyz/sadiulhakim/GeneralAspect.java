package xyz.sadiulhakim;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GeneralAspect {

    // We can make a PointCut and use it in multiple places. It takes Designators.
    // Or we can use Designators directly in Advice
    // Any method of LearnAopApplication class
    @Pointcut("execution(* xyz.sadiulhakim.LearnAopApplication.*(..))")
    public void publicApis() {
    }

    @Before("publicApis()")
    public void beforePublicApis(JoinPoint point) {
        System.out.println("[Before Calling] " + point.getSignature().getName());
    }

    @After("publicApis()")
    public void afterPublicApis(JoinPoint point) {
        System.out.println("[After Calling] " + point.getSignature().getName());
    }

    @AfterReturning(pointcut = "publicApis()", returning = "result")
    public void afterReturningPublicApis(JoinPoint point, Object result) {
        System.out.println("[After Returning] " + point.getSignature().getName());
        System.out.println(result);
    }

    @AfterThrowing(pointcut = "publicApis()", throwing = "ex")
    public void afterThrowingPublicApis(JoinPoint point, Throwable ex) {
        System.out.println("[After Throwing] " + point.getSignature().getName());
        System.out.println(ex.getMessage());
    }

    @Around("publicApis()")
    public Object aroundPublicApis(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = point.proceed();
        long end = System.currentTimeMillis();
        System.out.println(point.getSignature().getName() + " took " + (end - start) / 1000.0 + " secs");
        return result;
    }
}
