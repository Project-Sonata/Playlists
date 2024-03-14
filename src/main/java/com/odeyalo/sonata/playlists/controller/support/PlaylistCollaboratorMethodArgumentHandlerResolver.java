package com.odeyalo.sonata.playlists.controller.support;

import com.odeyalo.sonata.playlists.controller.PlaylistController;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
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

@Component
public final class PlaylistCollaboratorMethodArgumentHandlerResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(PlaylistCollaborator.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull MethodParameter parameter,
                                        @NotNull BindingContext bindingContext,
                                        @NotNull ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(AuthenticatedUser.class)
                .map(it -> createPlaylistCollaborator(it));
    }

    private static PlaylistCollaborator createPlaylistCollaborator(AuthenticatedUser user) {
        return PlaylistCollaborator.builder()
                .id(user.getDetails().getId())
                .displayName("mock")
                .type(EntityType.USER)
                .contextUri("sonata:user:" + user.getDetails().getId())
                .build();
    }
}
