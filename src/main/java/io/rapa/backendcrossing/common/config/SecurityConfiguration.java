package io.rapa.backendcrossing.common.config;


import io.rapa.backendcrossing.common.constants.EndPoints;
import io.rapa.backendcrossing.common.eventhandler.OauthSuccessHandler;
import io.rapa.backendcrossing.common.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtFilter jwtFilter;
    private final OauthSuccessHandler oauthSuccessHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity
                .csrf(csrf->csrf.disable())
                .cors(cors->cors.disable())
                .formLogin(formLogin->formLogin.disable())
                .oauth2Login(
                        oauth->oauth.successHandler(oauthSuccessHandler)
                )
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
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
