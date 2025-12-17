package com.tourflow.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class MetricsConfig implements WebMvcConfigurer {

    @Autowired
    private MeterRegistry meterRegistry;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "tourflow-backend");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                request.setAttribute("startTime", System.currentTimeMillis());
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                       Object handler, Exception ex) {
                long startTime = (Long) request.getAttribute("startTime");
                long duration = System.currentTimeMillis() - startTime;

                String uri = request.getRequestURI();
                String method = request.getMethod();
                int status = response.getStatus();

                Timer.Sample sample = Timer.start(meterRegistry);
                sample.stop(Timer.builder("http.server.requests")
                        .tag("method", method)
                        .tag("uri", uri)
                        .tag("status", String.valueOf(status))
                        .register(meterRegistry));
            }
        });
    }
}
