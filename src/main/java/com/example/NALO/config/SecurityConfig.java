package com.example.NALO.config;


import com.example.NALO.jwt.JwtAuthenticationTokenFilter;
import com.example.NALO.jwt.JwtTokenProvider;
import com.example.NALO.redis.RedisService;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private JwtTokenProvider jwtTokenProvider;
    private RedisService redisService;


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults());

        http.httpBasic(AbstractHttpConfigurer::disable);

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/user/normal/register", "/user/normal/login").permitAll()
                .anyRequest().permitAll()
        );

        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));

        http.addFilterBefore(new JwtAuthenticationTokenFilter(jwtTokenProvider, redisService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
