package com.example.software.security;

import com.example.software.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    private static final String LOGIN_URL = "/login";
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests().requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll();
        super.configure(http);
        http.oauth2Login ().loginPage (LOGIN_URL).permitAll ()
                .and ()
                .formLogin ().loginPage (LOGIN_URL).permitAll ();
        setLoginView(http, LoginView.class);
    }

}
