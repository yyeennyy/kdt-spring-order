package org.prgrms.kdt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Aspect Oriented Programming 실습!

// Aspect의 구성 :
// 1. Advice
//     Aspect는 여러 Advice(제공할 부가기능)를 담고 있다.
//     Advice(부가기능)에는 JoinPoint(적용할 위치)를 지정해주어야 한다.
// 2. PointCut
@Aspect
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    public void log(ProceedingJoinPoint joinPoint) {
        joinPoint.proceed();
    }
}
