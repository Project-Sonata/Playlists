package com.odeyalo.sonata.playlists.controller.support;

import com.odeyalo.sonata.playlists.dto.PartialPlaylistDetailsUpdateRequest;
import com.odeyalo.sonata.playlists.service.PartialPlaylistDetailsUpdateInfo;
import com.odeyalo.sonata.playlists.support.converter.PartialPlaylistDetailsUpdateInfoConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.annotation.AbstractMessageReaderArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;

import java.util.List;

@Component
public final class PartialPlaylistDetailsUpdateInfoMethodArgumentResolver extends AbstractMessageReaderArgumentResolver {
    private final PartialPlaylistDetailsUpdateInfoConverter infoConverter;

    public PartialPlaylistDetailsUpdateInfoMethodArgumentResolver(final List<HttpMessageReader<?>> readers,
                                                                  final PartialPlaylistDetailsUpdateInfoConverter infoConverter) {
        super(readers);
        this.infoConverter = infoConverter;
    }

    @Override
    public boolean supportsParameter(@NotNull final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(PartialPlaylistDetailsUpdateInfo.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {

        final Parameter bodyTypeParameter = resolveBodyTypeParameter();

        return readBody(MethodParameter.forParameter(bodyTypeParameter), parameter, true, bindingContext, exchange)
                .cast(PartialPlaylistDetailsUpdateRequest.class)
                .map(infoConverter::toPartialPlaylistDetailsUpdateInfo);
    }

    private Parameter resolveBodyTypeParameter() {
        //noinspection DataFlowIssue never be null because we always have method in this class
        return ReflectionUtils
                .findMethod(this.getClass(), "methodParameterDescriptorSupport", PartialPlaylistDetailsUpdateRequest.class)
                .getParameters()[0];
    }

    /**
     * Support method to resolve {@link java.lang.reflect.Parameter} to set in {@link org.springframework.core.MethodParameter}
     * DO NOT DELETE IT IN ANY CASE, ONLY IF YOU FOUND A BETTER SOLUTION
     *
     * @param body - body class to read content to
     */
    @SuppressWarnings("unused")
    private void methodParameterDescriptorSupport(PartialPlaylistDetailsUpdateRequest body) {
    }
}
