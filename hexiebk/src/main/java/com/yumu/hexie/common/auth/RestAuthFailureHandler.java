package com.yumu.hexie.common.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.backend.web.dto.BaseResult;

@Component(value = "restAuthFailureHandler")
public class RestAuthFailureHandler implements AuthenticationFailureHandler {
    @Inject
    private ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, BaseResult.fail("用户登录失败"));
        writer.flush();
    }
}
