package com.odeyalo.sonata.playlists.config.security;

import com.odeyalo.suite.security.annotation.EnableSuiteSecurity;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;


@Configuration
@EnableSuiteSecurity
@EnableWebFluxSecurity
@Builder
public class SecurityConfiguration {
    @Autowired
    Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> authorizeExchangeSpecCustomizer;
    @Autowired
    Customizer<ServerHttpSecurity.ExceptionHandlingSpec> exceptionHandlingSpecCustomizer;
    @Autowired
    Customizer<ServerHttpSecurity.CorsSpec> corsSpecCustomizer;
    @Autowired
    Customizer<ServerHttpSecurity.CsrfSpec> csrfSpecCustomizer;
    @Autowired
    AuthenticationWebFilter authenticationManagerFilter;

    @Bean
    public SecurityWebFilterChain httpSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .authorizeExchange(authorizeExchangeSpecCustomizer)
                .cors(corsSpecCustomizer)
                .csrf(csrfSpecCustomizer)
                .exceptionHandling(exceptionHandlingSpecCustomizer)
                .addFilterAt(authenticationManagerFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

}
