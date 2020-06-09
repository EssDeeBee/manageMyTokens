package com.ser.webutils.webclient.rest;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@Deprecated
public class RestRequestCopy {

    /**
     * Method sending request in order to get a token response.
     *
     * @param url       URL where request will be sent
     * @param variables Variables needed to send through request
     * @param mediaType Type of request for example "application/x-www-form-urlencoded"
     * @return String Json in String format.
     */
    @SneakyThrows
    public Mono<String> getTokenFromCode(String url, LinkedMultiValueMap<String, String> variables, String mediaType) {
        final String notSupportedAnswer = "Not supported media type!";


        Mono<String> jsonResponse;
        WebClient webClient = WebClient.create(url);

        switch (mediaType) {

            case MediaType.APPLICATION_FORM_URLENCODED_VALUE:

                log.info("Sending post request using URL: "
                        + url
                        + " media type: "
                        + mediaType
                );

                jsonResponse =
                        webClient
                                .post()
                                .syncBody(variables)
                                .header(HttpHeaders.CONTENT_TYPE, mediaType)
                                .exchange()
                                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class));

                log.info("Received response is: " + jsonResponse);

                break;


            case MediaType.APPLICATION_JSON_VALUE:
                jsonResponse = Mono.empty();

                break;
            default:
                jsonResponse = Mono.empty();
        }

        return jsonResponse;

    }

}
