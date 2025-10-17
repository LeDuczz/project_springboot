package com.example.demo.exception;

import com.example.demo.base.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Exception ex = (Exception) request.getAttribute("exception");
        String message = (ex != null) ? ex.getMessage() : authException.getMessage();
        BaseResponse<?> body = new BaseResponse<>
                (HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized: " + message,
                        null);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(body);
        response.getWriter().write(json);
    }
}
