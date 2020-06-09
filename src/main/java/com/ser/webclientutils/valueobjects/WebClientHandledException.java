package com.ser.webclientutils.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class WebClientHandledException extends Throwable {
    private Mono<WebClient.RequestBodyUriSpec> request;
    private ClientResponse response;
    private String responseBodyAsString;
}