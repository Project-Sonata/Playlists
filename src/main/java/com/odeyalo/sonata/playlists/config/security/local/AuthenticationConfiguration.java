package com.odeyalo.sonata.playlists.config.security.local;

import com.odeyalo.suite.security.auth.TokenAuthenticationManager;
import com.odeyalo.suite.security.auth.token.AccessTokenMetadata;
import com.odeyalo.suite.security.auth.token.ReactiveAccessTokenValidator;
import com.odeyalo.suite.security.auth.token.ValidatedAccessToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Configuration
@Profile("local")
public class AuthenticationConfiguration {

    @Bean
    @Primary
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new TokenAuthenticationManager(
                new LocalDevelopmentAccessTokenValidator()
        );
    }

    private static class LocalDevelopmentAccessTokenValidator implements ReactiveAccessTokenValidator {
        private final Map<String, ValidatedAccessToken> tokensCache = Map.of(
                "token1", ValidatedAccessToken.valid(
                        AccessTokenMetadata.of("123", new String[]{"read", "write"},
                                Instant.now().getEpochSecond(),
                                Instant.now().plusSeconds(600).getEpochSecond()
                        )),
                "token1_2", ValidatedAccessToken.valid(
                        AccessTokenMetadata.of("123", new String[]{"read", "write", "playlist"},
                                Instant.now().getEpochSecond(),
                                Instant.now().plusSeconds(600).getEpochSecond()
                        )),
                "token2", ValidatedAccessToken.valid(
                        AccessTokenMetadata.of("miku", new String[]{"read", "write", "playlist"},
                                Instant.now().getEpochSecond(),
                                Instant.now().plusSeconds(600).getEpochSecond()
                        ))
        );

        private final Logger logger = LoggerFactory.getLogger(LocalDevelopmentAccessTokenValidator.class);

        public LocalDevelopmentAccessTokenValidator() {
            logger.info("Using local 'DEV' mode, cache of available access tokens to use. Token that is not exist in cache will cause HTTP 401 UNAUTHORIZED status");

            tokensCache.forEach((key, value) -> {
                final AccessTokenMetadata tokenMetadata = value.getToken();
                logger.info("Generated access token: '{}' for user 'ID({})' with following scopes: '({})', expire after: {} seconds", key, tokenMetadata.getUserId(), tokenMetadata.getScopes(), tokenMetadata.getExpiresIn() - Instant.now().getEpochSecond());
            });

        }

        @Override
        @NotNull
        public Mono<ValidatedAccessToken> validateToken(@NotNull final String tokenValue) {

            final ValidatedAccessToken validatedAccessToken = tokensCache.getOrDefault(tokenValue, ValidatedAccessToken.invalid());

            return Mono.just(validatedAccessToken);
        }
    }
}
