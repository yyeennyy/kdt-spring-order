package org.prgrms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;



class CalculatorImpl implements Calculator {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}

interface Calculator {
    int add(int a, int b);
}

// 핸들러가 호출이 되면서 부가기능이 돌아가는 것임
class LoggingInvocationHandler implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(LoggingInvocationHandler.class); // 로거 이렇게 썻다는거 다시 기억!
    private final Object target;
    public LoggingInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("{} executed in {}", method.getName(), target.getClass().getCanonicalName());
        return method.invoke(target, args);
    }
}

// JdkProxy를 통해 Proxy객체를 다이나믹하게 만드는 방법

public class JdkProxyTest {
    private static final Logger log = LoggerFactory.getLogger(JdkProxyTest.class); // 로거 이렇게 썻다는거 다시 기억!

    public static void main(String[] args) {
        // 저 클래스의 ClassLoader를 준다는 말이 무슨말일까?
        var calculator = new CalculatorImpl();
        // 아무튼 Proxy Instance 생성함
        Calculator proxyInstance = (Calculator) Proxy.newProxyInstance(LoggingInvocationHandler.class.getClassLoader(),
                new Class[]{Calculator.class},
                new LoggingInvocationHandler(calculator));

        var result = proxyInstance.add(1, 2);
        log.info("Add -> {}", result);
    }
}
