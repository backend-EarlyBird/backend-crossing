package io.rapa.backendcrossing.config;


import io.rapa.backendcrossing.constant.EndPoints;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity
                .csrf(csrf->csrf.disable())
                .cors(cors->cors.disable())
                .formLogin(formLogin->formLogin.disable())
                .oauth2Login(Customizer.withDefaults())
                .httpBasic(httpBasic->httpBasic.disable())
                .sessionManagement(sessionConfig-> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                    auth -> auth

                            .requestMatchers("/login/**","/swagger-ui/**", "/v3/api-docs/**" ).permitAll()

                            .requestMatchers(HttpMethod.POST, EndPoints.POST_ANONYMOUS).anonymous()
                            .requestMatchers(HttpMethod.POST, EndPoints.POST_PERMITALL).permitAll()
                            .requestMatchers(HttpMethod.POST, EndPoints.POST_AUTHENTICATED).authenticated()

                            .requestMatchers(HttpMethod.GET, EndPoints.GET_ANONYMOUS).anonymous()
                            .requestMatchers(HttpMethod.GET, EndPoints.GET_PERMITALL).permitAll()
                            .requestMatchers(HttpMethod.GET, EndPoints.GET_AUTHENTICATED).authenticated()

                            .requestMatchers(HttpMethod.PATCH, EndPoints.PATCH_AUTHENTICATED).authenticated()

                            .requestMatchers(HttpMethod.DELETE, EndPoints.DELETE_AUTHENTICATED).authenticated()
                            .anyRequest()
                            .denyAll()
                )
                .build();
    }
}
