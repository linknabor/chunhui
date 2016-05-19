package com.yumu.hexie.common.config;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.yumu.hexie.common.auth.CustomAuthenticationProvider;
import com.yumu.hexie.common.auth.RestAuthFailureHandler;
import com.yumu.hexie.common.auth.RestAuthSuccessHandler;
import com.yumu.hexie.common.auth.UnauthorizedAuthenticationEntryPoint;

@EnableGlobalMethodSecurity(securedEnabled=true)
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Inject
    private RestAuthSuccessHandler restAuthSuccessHandler;
    @Inject
    private RestAuthFailureHandler restAuthFailureHandler;
    @Inject
    private CustomAuthenticationProvider customAuthenticationProvider;
    
    @Inject
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth//.userDetailsService(customUserDetailService).and()
        .authenticationProvider(customAuthenticationProvider);
    	// 设置内存用户角色
        //auth.inMemoryAuthentication()
        //	.withUser("user").password("123456").roles("USER")
        //	.and()
        //	.withUser("admin").password("654321").roles("USER", "ADMIN");
    }

    @Override
    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


//    @Inject
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder());
//    }
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.csrf().disable()
        	.headers().disable()
        		//.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                //.and()
        		.authorizeRequests().antMatchers("/wechat/backend/login").permitAll()
                .and()
                .authorizeRequests().antMatchers("/wechat/backend/**").authenticated()
                .and().formLogin().loginPage("/wechat/backend/login").permitAll()
                .successHandler(restAuthSuccessHandler)
                .failureHandler(restAuthFailureHandler)
                .and().logout().logoutUrl("/wechat/backend/logout").permitAll().logoutSuccessUrl("/backend/login.html")
                .and().exceptionHandling().authenticationEntryPoint(new UnauthorizedAuthenticationEntryPoint());
    }
}
