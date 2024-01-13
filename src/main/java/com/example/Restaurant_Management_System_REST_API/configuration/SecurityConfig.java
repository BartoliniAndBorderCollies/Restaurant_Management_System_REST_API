package com.example.Restaurant_Management_System_REST_API.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, MvcRequestMatcher.Builder mvc)
            throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) //TODO: consult this part with my mentor
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(mvc.pattern("/api/admin/**")) //TODO: update path with appropriate mapping
                        .hasRole("ADMIN")
                        .requestMatchers(mvc.pattern("/api/owner/**")) //TODO: update path with appropriate mapping
                        .hasRole("OWNER")
                        .requestMatchers(mvc.pattern("/api/manager/**")) //TODO: update path with appropriate mapping
                        .hasRole("MANAGER")
                        .requestMatchers(mvc.pattern("/api/staff/**")) //TODO: update path with appropriate mapping
                        .hasRole("STAFF")
                        .anyRequest()
                        .permitAll())
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}
