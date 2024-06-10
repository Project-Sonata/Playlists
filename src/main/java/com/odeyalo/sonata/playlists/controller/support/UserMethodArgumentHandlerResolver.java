package com.odeyalo.sonata.playlists.controller.support;

import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.User;
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
public final class UserMethodArgumentHandlerResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(User.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull MethodParameter parameter,
                                        @NotNull BindingContext bindingContext,
                                        @NotNull ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(AuthenticatedUser.class)
                .map(it -> createUser(it));
    }

    private static User createUser(AuthenticatedUser user) {
        return User.builder()
                .id(user.getDetails().getId())
                .displayName(user.getDetails().getId())
                .type(EntityType.USER)
                .contextUri("sonata:user:" + user.getDetails().getId())
                .build();
    }
}
