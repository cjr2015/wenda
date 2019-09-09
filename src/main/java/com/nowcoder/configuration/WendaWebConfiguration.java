package com.nowcoder.configuration;

import com.nowcoder.interceptor.LoginrequriedInterceptor;
import com.nowcoder.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Component
public class WendaWebConfiguration extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter {

    @Autowired
    PassportInterceptor passportInterceptor;
    @Autowired
    LoginrequriedInterceptor loginrequriedInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
         registry.addInterceptor(passportInterceptor).excludePathPatterns("/error");
        registry.addInterceptor(loginrequriedInterceptor).addPathPatterns("/user/*");

        super.addInterceptors(registry);
    }
}
