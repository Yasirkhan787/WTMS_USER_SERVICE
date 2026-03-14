package com.yasirkhan.user.configurations;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

//    @Value("${app.security.internal-secret}")
//    private String internalSecret;

    @Override
    public void apply(RequestTemplate template) {
        // Get the current request coming into the User-Service
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // Extract the Authorization header (the Bearer token)
            String authHeader =
                    request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader != null) {
                // Relay the token to the Auth-Service
                template.header(HttpHeaders.AUTHORIZATION, authHeader);
            }
        }

//        // 4. Always add the Gateway Secret for internal trust
//        template.header("X-Gateway-Secret", internalSecret);
    }
}