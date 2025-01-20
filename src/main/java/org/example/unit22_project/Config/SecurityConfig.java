package org.example.unit22_project.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(customizer -> customizer.disable());
        http.authorizeHttpRequests(request ->
                request.requestMatchers("/index/admin/**").permitAll()
                        .requestMatchers("/index/doctor/**").permitAll()
                        .requestMatchers("/index/user/**").permitAll()
                        .anyRequest().permitAll());

        http.formLogin(customizer ->
                customizer.disable());
        http.logout(customizer ->
                customizer.logoutUrl("/logout").permitAll());
        return http.build();
    }
}