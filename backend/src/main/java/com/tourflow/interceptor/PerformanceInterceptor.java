package com.tourflow.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PerformanceInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                           Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        logger.info("Request URL: {}, Method: {}, Execution Time: {} ms",
                request.getRequestURL(),
                request.getMethod(),
                executeTime);

        // Loguer les requêtes lentes (plus de 1000ms)
        if (executeTime > 1000) {
            logger.warn("Slow Request Detected: URL: {}, Method: {}, Execution Time: {} ms",
                    request.getRequestURL(),
                    request.getMethod(),
                    executeTime);
        }
    }
}
