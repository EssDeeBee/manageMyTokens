package com.ser.webutils.webclient.rest;

import org.junit.Test;


public class RestClientTest {

    @Test
    public void getTokenFromCodeGoogle() {

//        //init
//        WebClient webClient = WebClient.create();
//        val map = new LinkedMultiValueMap<String, String>() {
//            {
//                add("grant_type", "refresh_token");
//                add("client_id", "1010277423834-br0pkjjh3r4tbmnrr78ge1mmbjgvm1ph.apps.googleusercontent.com");
//                add("client_secret", "LKTjxbJYgxqvC7_lgMpTjf2X");
//                add("refresh_token", "1/pUK19SGrfEcBNK_os0j7Mg2p9j11Eai3-Uw8tcZtDJI");
//            }
//        };
//
//        //act
//
//        Mono<String> response = webClient
//                .post()
//                .uri("https://www.googleapis.com/oauth2/v4/token")
//                .syncBody(map)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//                .exchange()
//                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class));
//
//
//        //assert
//        System.out.println(response.block());
//
//    }
//
//    @Test
//    public void getTokenFromCodeMyTarget() {
//
//        //init
//        WebClient webClient = WebClient.create();
//        val map = new LinkedMultiValueMap<String, String>() {
//            {
//                add("grant_type", "refresh_token");
//                add("client_id", "n5zvIiexebHHazsF");
//                add(
//                        "client_secret",
//                        "NoETjJTbsmAyE752NUakv2bpExZii88W2U1wddWPywayBcj2sjDVMr4fzFyvY87HxWtt7tJMoUNPY6K6aI2U3PjjROJRisTbzQMbfLiogsuN70631Bb3VoBxsIbUIroICpPCAJBfJ1aGijYianGKfzVuwLDn1esiTFntX5Gm0dWPA6XjYenpCXfrMrLbONcU8I2MEDTg5mGzPsQgpdMekjA5gLtHSfxJokhoN8"
//                );
//                add(
//                        "refresh_token",
//                        "1Hs3X1lUwK0GoDdBGZaVf0oHklez8GJFTLgK5QJqMShnZN8Ecmf2iYpdJ94Os71Oc5suGMT64FoQyW7P3bxeUkA1QG8a8q7SovlyGurrDzRzv6Ezm3zFCX4VhUdjDo98vpEmBIufoNnkYQiX0Ny92TLKBpvMBcyJDV71JzZWyI1aSLeTR4dyiTaLNGKbYk3BYUepLaK1RnxdMzgkGvanohj4PyfjuvEmuwqniRPXugUPZuuqXo2y4nbbiN5VM"
//                );
//            }
//        };
//
//
//        //act
//        Mono<String> response = webClient
//                .post()
//                .uri("https://target.my.com/api/v2/oauth2/token.json")
//                .syncBody(map)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//                .exchange()
//                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class));
//
//        //assert
//        System.out.println(response.block());

    }
}