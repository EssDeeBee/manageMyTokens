package com.ser.webclientutils.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import com.ser.webclientutils.valueobjects.WebClientHandledException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.Callable;

import static lombok.Lombok.sneakyThrow;

@Slf4j
public class BaseRestClient {

    public BaseRestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.setWebClient(WebClient
                .builder()
                .filter(logFilter())
                .baseUrl(baseUrl)
                .build());
    }

    @Getter
    private String baseUrl;

    private ObjectMapper jsonObjectMapper = new ObjectMapper();


    private ExchangeFilterFunction logFilter() {
        return (ClientRequest clientRequest, ExchangeFunction exchangeFunction) -> {

            Mono<ClientResponse> response;

            log.debug(
                    MessageFormat.format("\nProcessing getResponse:\n" +
                                    "method= {0}\n" +
                                    "path= {1}\n" +
                                    "body=[ {2} ]"
                            , clientRequest.method()
                            , clientRequest.url()
                            , clientRequest.body()
                    )
            );
            clientRequest.headers()
                    .forEach((name, values) -> {
                        values.forEach(value -> {
                            log.trace(" $name = $value");
                        });
                    });

            response = exchangeFunction.exchange(clientRequest);

            response = response.doOnEach(r -> {
                val li = r.get();
                if (li == null)
                    return;

                log.debug(li.statusCode().toString());
            });

            return response;
        };
    }

    @Getter
    @Setter
    private WebClient webClient;

    public ResponseProcessor responseProcessor = new ResponseProcessor();

    public <T> Mono<T> getRequest(Callable<T> supplier) {
        return Mono.fromCallable(supplier);
    }

    public Mono<String> getResponseString(Mono<WebClient.RequestBodyUriSpec> request) {
        Mono<String> result = request
                .flatMap(it -> it.exchange())
                .flatMap(r -> r.bodyToMono(String.class)
                        .map(it -> {
                            Mono<String> bodyResult;

                            // Выбрасываем исключение, если есть его обработчики
                            val processor = this.responseProcessor;
                            val possibleException = new WebClientHandledException(
                                    request,
                                    r,
                                    it
                            );

                            if (processor != null && processor.getProcessExceptionHandlers()
                                    .keySet()
                                    .stream()
                                    .anyMatch(handlerCondition -> {
                                        return handlerCondition.apply(possibleException);
                                    })) {
                                sneakyThrow(possibleException);
                            }

                            bodyResult = Mono.just(it);

                            return bodyResult;
                        }))
                .flatMap(it -> it)
                .onErrorResume(it -> {
                    Mono<String> errorProcessedResult = null;
                    val processor = this.responseProcessor;
                    if (processor != null && (it instanceof WebClientHandledException)) {
                        errorProcessedResult = this.responseProcessor.processExceptions((WebClientHandledException) it);
                    } else {
                        sneakyThrow(it);
                    }
                    return errorProcessedResult;
                });

        return result;
    }

    public <T> Mono<T> getResponseTyped(Mono<WebClient.RequestBodyUriSpec> request, Class<T> elementClass) {
        val responseMono = this.getResponseString(request);

        return responseMono.map(it -> {
            T result = null;
            if (elementClass.isAssignableFrom(it.getClass())) {
                result = (T) it;
            } else {
                JsonNode jsonNode = null;
                try {
                    jsonNode = jsonObjectMapper.readTree(it);
                } catch (IOException e) {
                    e.printStackTrace();
                    sneakyThrow(e);
                }
                if (elementClass.isAssignableFrom(jsonNode.getClass())) {
                    result = (T) jsonNode;
                } else {
                    try {
                        result = jsonObjectMapper.readValue(it, elementClass);
                    } catch (IOException e) {
                        e.printStackTrace();
                        sneakyThrow(e);
                    }
                }
            }
            return result;
        });
    }
}