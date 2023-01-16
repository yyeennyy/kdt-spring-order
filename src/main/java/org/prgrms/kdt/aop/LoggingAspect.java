package org.prgrms.kdt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Aspect Oriented Programming 실습!

// Aspect의 구성 :
// 1. Advice
//     Aspect는 여러 Advice(제공할 부가기능)를 담고 있다.
//     Advice(부가기능)에는 JoinPoint(적용할 위치)를 지정해주어야 한다.
// 2. PointCut
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    //    @Pointcut("execution(public * org.prgrms.kdt..*.*(..))")
    //    public void servicePublicMethodPointcut(){};

    // Around라는 Advice를 사용할 것이다.
    @Around("org.prgrms.kdt.aop.CommonPointcut.repositoryInsertMethodPointcut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Before method called. {}", joinPoint.getSignature().toString());
        var result = joinPoint.proceed();
        log.info("After method called with result => {}", result);
        return result;
    }
}
