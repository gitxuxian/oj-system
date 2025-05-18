package com.xu.xuoj.aspect;

import com.xu.xuoj.judge.codesandbox.model.ExecuteCodeRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 使用Sring的AOP
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * 日志记录方法调用的环绕通知
     *
     * @param joinPoint 切面连接点，包含方法的执行信息
     * @return 方法执行结果
     * @throws Throwable 如果方法执行过程中出现异常，则抛出该异常
     */
    @Around("execution(* com.xu.xuoj.judge.codesandbox.CodeSandBox.excuteCode(com.xu.xuoj.judge.codesandbox.model.ExecuteCodeRequest))")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();
        ExecuteCodeRequest request = (ExecuteCodeRequest) args[0];
        log.info("代码沙箱的请求信息 {}", request.toString());
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("代码沙箱的相应信息 {},耗时{}", signature.getReturnType(), startTime - endTime);
        return result;
    }
}
