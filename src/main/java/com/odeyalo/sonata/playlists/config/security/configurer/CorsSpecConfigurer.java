package com.odeyalo.sonata.playlists.config.security.configurer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.stereotype.Component;

/**
 * Used to configure the {@link CorsSpec}
 */
@Component
public class CorsSpecConfigurer implements Customizer<CorsSpec> {

    @Override
    public void customize(CorsSpec corsSpec) {
        corsSpec.disable();
    }
}
