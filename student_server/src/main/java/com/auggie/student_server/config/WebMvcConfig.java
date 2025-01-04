package com.auggie.student_server.config;

import com.auggie.student_server.security.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/studentList") // 仅拦截 studentList 请求
                .excludePathPatterns("/student/login", "/teacher/login", "/error", "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**");
    }
}