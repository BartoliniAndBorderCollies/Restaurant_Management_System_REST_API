package com.example.Restaurant_Management_System_REST_API.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, MvcRequestMatcher.Builder mvc)
            throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(mvc.pattern("/api/admin/customer/add")) //TODO: update path with appropriate mapping
                        .authenticated()

                        //Menu records secured paths
                        .requestMatchers(mvc.pattern("/api/menu/record/add"), mvc.pattern("/api/menu/record/update/**"),
                                 mvc.pattern("/api/menu/record/delete/**"))
                        .hasAnyRole("OWNER", "MANAGER")
                        .requestMatchers(mvc.pattern("/api/menu/record/find/**"), mvc.pattern("/api/menu/record/findAll"))
                        .hasAnyRole("OWNER", "MANAGER", "STAFF")

                        //Customer secured paths
                        .requestMatchers(mvc.pattern("/api/customer/add"), mvc.pattern("/api/customer/update/**"),
                                mvc.pattern("/api/customer/delete/**"))
                        .hasAnyRole("OWNER", "MANAGER")
                        .requestMatchers(mvc.pattern("/api/customer/find/**"), mvc.pattern("/api/customer/findAll"))
                        .hasAnyRole("OWNER", "MANAGER", "STAFF")

                        //The rest
                        .anyRequest()
                        .permitAll())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
