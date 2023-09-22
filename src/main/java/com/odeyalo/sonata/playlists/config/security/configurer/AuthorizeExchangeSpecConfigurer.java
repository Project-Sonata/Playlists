package com.odeyalo.sonata.playlists.config.security.configurer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.stereotype.Component;

/**
 * Helper class to configure the {@link AuthorizeExchangeSpec}
 */
@Component
public class AuthorizeExchangeSpecConfigurer implements Customizer<AuthorizeExchangeSpec> {

    @Override
    public void customize(AuthorizeExchangeSpec authorizeExchangeSpec) {
        authorizeExchangeSpec.anyExchange().authenticated();
    }
}
