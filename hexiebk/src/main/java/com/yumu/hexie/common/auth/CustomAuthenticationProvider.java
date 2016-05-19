package com.yumu.hexie.common.auth;

import java.util.List;

import javax.inject.Inject;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.yumu.hexie.model.backend.BackendUser;
import com.yumu.hexie.model.backend.BackendUserRepository;
import com.yumu.hexie.model.user.BackendMenu;
import com.yumu.hexie.model.user.BackendMenuRepository;

@Component(value = "customAuthenticationProvider")
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private BackendUserRepository backendUserRepository;
	@Inject
	private BackendMenuRepository backendMenuRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        BackendUser user = backendUserRepository.findByUsername(name);
        List<BackendUser> us = backendUserRepository.findAll();
        List<BackendMenu> l = backendMenuRepository.findAll();
        for(BackendMenu m : l ) {
        	System.out.println(m.getText());
        }
        if (user!=null&&passwordEncoder.matches(password, user.getPassword())){
        	UsernamePasswordAuthenticationToken a = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
            return a;
        }
        if(us.size()>0) {
        	name = authentication.getName();
        }
        //直接用token登录
//        final String verificationCode = redisRepository.get(Keys.mobileSmsKey(name));
//        if (StringUtil.isNotEmpty(verificationCode) && verificationCode.equalsIgnoreCase(password))
//            return new UsernamePasswordAuthenticationToken(user, password, null);
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
