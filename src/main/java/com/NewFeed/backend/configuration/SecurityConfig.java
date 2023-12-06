package com.NewFeed.backend.configuration;

import com.NewFeed.backend.security.JwtAuthenticationEntryPoint;
import com.NewFeed.backend.security.JwtAuthenticationFilter;
import com.NewFeed.backend.security.oauth2.CustomOAuth2UserService;
import com.NewFeed.backend.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.NewFeed.backend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.NewFeed.backend.security.oauth2.user.HttpCookieOAuth2AuthorizationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {


    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(crsf-> {
            try {
                crsf.disable().
                cors(cors ->cors.configurationSource(corsConfigurationSource())).
                authorizeHttpRequests(auth->auth.
                                requestMatchers("/home/**").
                                authenticated().
                                requestMatchers("/api/users/login").
                                permitAll().
                                requestMatchers("/api/users/signup").
                                permitAll().
                                requestMatchers("/api/users/register").
                                permitAll().
                                requestMatchers("/api/users/refresh").
                                permitAll().
                                anyRequest().
                                authenticated()
                        ).
                        oauth2Login(this::oauth2LoginConfig).
                        exceptionHandling(excep->excep.authenticationEntryPoint(jwtAuthenticationEntryPoint)).
                        sessionManagement(sesion->sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // Replace with your actual frontend URL
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return  source;
    }

    private void oauth2LoginConfig(OAuth2LoginConfigurer<HttpSecurity> oAuth2LoginConfigure){
        oAuth2LoginConfigure
                .authorizationEndpoint(config -> {
                     config
                        .baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository());
                })
                .redirectionEndpoint(config -> {
                    config.baseUri("/oauth2/callback/*");
                })
                .userInfoEndpoint(userInfoEndpointConfig -> {
                    userInfoEndpointConfig.userService(customOAuth2UserService);
                })
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);
    }

    @Bean
    public DaoAuthenticationProvider doDaoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

}

