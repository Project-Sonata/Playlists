package com.odeyalo.sonata.playlists.config.security.configurer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity.ExceptionHandlingSpec;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Used to configure the {@link ExceptionHandlingSpec}
 */
@Component
public class ExceptionHandlingSpecConfigurer implements Customizer<ExceptionHandlingSpec> {

    final ServerAccessDeniedHandler serverAccessDeniedHandler;
    final ServerAuthenticationEntryPoint serverAuthenticationEntryPoint;

    public ExceptionHandlingSpecConfigurer(ServerAccessDeniedHandler serverAccessDeniedHandler, ServerAuthenticationEntryPoint serverAuthenticationEntryPoint) {
        this.serverAccessDeniedHandler = serverAccessDeniedHandler;
        this.serverAuthenticationEntryPoint = serverAuthenticationEntryPoint;
    }

    @Override
    public void customize(ExceptionHandlingSpec exceptionHandlingSpec) {
        exceptionHandlingSpec
                .accessDeniedHandler(serverAccessDeniedHandler)
                .authenticationEntryPoint(serverAuthenticationEntryPoint);
    }
}
