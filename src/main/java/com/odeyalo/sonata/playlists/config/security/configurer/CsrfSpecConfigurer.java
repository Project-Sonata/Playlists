package com.odeyalo.sonata.playlists.config.security.configurer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.stereotype.Component;

/**
 * Used to configure the {@link ServerHttpSecurity.CsrfSpec}
 */
@Component
public class CsrfSpecConfigurer implements Customizer<ServerHttpSecurity.CsrfSpec> {

    @Override
    public void customize(ServerHttpSecurity.CsrfSpec csrfSpec) {
        csrfSpec.disable();
    }
}
