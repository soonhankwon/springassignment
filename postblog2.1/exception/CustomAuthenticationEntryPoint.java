package com.assignment.postblog.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    //Filter에서 토큰 관련 예외가 발생했을 경우, 이를 Servlet 단계로 보내줘서 예외를 처리해주기 위함
    //예외가 발생했을떄, Controller에 있는 해당 URI로 전달해주게 된다.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
//        response.sendRedirect("/exception/entrypoint");
    }
}
