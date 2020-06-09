package com.ser.webclientutils.services;

import lombok.Getter;
import lombok.val;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.ser.webclientutils.valueobjects.WebClientHandledException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResponseProcessor {

    @Getter
    private Map<Function<WebClientHandledException, Boolean>, BiFunction<WebClientHandledException, Mono<WebClient.RequestBodyUriSpec>, Mono<Void>>> processExceptionHandlers = new HashMap<>();

    public Mono<String> processExceptions(WebClientHandledException exception) {
        val handlers = processExceptionHandlers
                .entrySet()
                .stream()
                .filter(h -> h.getKey().apply(exception))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (handlers.size() > 0) {
            val request = exception.getRequest();
            Mono<Void> handlersChain = Mono.empty();
            for (val handler : handlers) {
                handlersChain = handlersChain.then(handler.apply(exception, request));
            }
            return handlersChain
                    .thenReturn(
                            request.flatMap(WebClient.RequestHeadersSpec::exchange).flatMap(it -> {
                                return it.bodyToMono(String.class);
                            }))
                    .flatMap(it -> {
                        return it;
                    });
        } else {
            return Mono.error(exception);
        }
    }
}