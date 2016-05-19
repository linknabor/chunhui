package com.yumu.hexie.common.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.backend.web.dto.BaseResult;
import com.yumu.hexie.model.backend.BackendUser;

@Component(value = "restAuthSuccessHandler")
public class RestAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Inject
    private ObjectMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        PrintWriter writer = response.getWriter();
        
        mapper.writeValue(writer, BaseResult.successResult((BackendUser) authentication.getPrincipal()));
        writer.flush();
    }
}
