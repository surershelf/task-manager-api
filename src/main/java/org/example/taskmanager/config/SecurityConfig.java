package org.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean // This annotation tells Spring to manage this method's result as a bean
    public PasswordEncoder passwordEncoder() {
        // We're telling Spring to use the BCrypt algorithm for password encoding
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita o CSRF
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll() // Permite TODAS as requisições para QUALQUER URL
                );

        return http.build();

    }
    }
