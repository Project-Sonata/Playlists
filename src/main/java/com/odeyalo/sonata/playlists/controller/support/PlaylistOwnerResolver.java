package com.odeyalo.sonata.playlists.controller.support;

import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Simple {@link HandlerMethodArgumentResolver} that resolves a {@link PlaylistOwner} from the current {@link AuthenticatedUser}
 */
@Component
public final class PlaylistOwnerResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(PlaylistOwner.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull MethodParameter parameter,
                                        @NotNull BindingContext bindingContext,
                                        @NotNull ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(AuthenticatedUser.class)
                .map(PlaylistOwnerResolver::resolveOwner);
    }

    @NotNull
    private static PlaylistOwner resolveOwner(AuthenticatedUser authenticatedUser) {
        return PlaylistOwner.builder()
                .id(authenticatedUser.getDetails().getId())
                .build();
    }
}
