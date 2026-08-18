package com.sdatfinals.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// quick fix so main isn't broken - UserService needs a PasswordEncoder bean
// and SecurityConfig doesn't have one yet. put it in its own file instead of
// touching SecurityConfig so your not fighting my changes when u gets to it.
// once your SecurityConfig makes its own PasswordEncoder bean, delete
// this whole file or spring will yell about two beans of the same type.

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
