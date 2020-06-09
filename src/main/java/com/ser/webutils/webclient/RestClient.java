package com.ser.webutils.webclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


@Slf4j
@Service
public class RestClient {

    /**
     * Method sending request in order to get a token response.
     *
     * @param url       URL where request will be sent
     * @param variables Variables needed to send through request
     * @param mediaType Type of request for example "application/x-www-form-urlencoded"
     * @return String Json in String format.
     */
    public Optional<String> sendTokenRequest(String url, List<NameValuePair> variables, String mediaType) {

        String notSupportedAnswer = "Not supported media type!";
        Optional<String> jsonResponse;

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);


        switch (mediaType) {

            case MediaType.APPLICATION_FORM_URLENCODED_VALUE:

                try {

                    variables.add(new BasicNameValuePair(HttpHeaders.CONTENT_TYPE, mediaType));
                    httpPost.setEntity(new UrlEncodedFormEntity(variables));
                    HttpResponse response = httpClient.execute(httpPost);

                    int responseStatusCode = response.getStatusLine().getStatusCode();
                    String responseContentType = response.getEntity().getContentType().getValue();

                    StringBuilder stringContainer = new StringBuilder();
                    Scanner byteStreamReader = new Scanner(response.getEntity().getContent());
                    String jsonAnswer = "application/json";

                    if (responseStatusCode != 200 && responseContentType.toLowerCase().contains(jsonAnswer)) {
                        while (byteStreamReader.hasNextLine()) {
                            stringContainer.append(byteStreamReader.nextLine());
                        }
                        log.error("Couldn't get a token, response: " + stringContainer.toString());
                        EntityUtils.consume(response.getEntity());
                        byteStreamReader.close();
                        return Optional.empty();

                    } else if (responseStatusCode == 200 && responseContentType.toLowerCase().contains(jsonAnswer)) {
                        while (byteStreamReader.hasNextLine()) {
                            stringContainer.append(byteStreamReader.nextLine());
                        }
                        EntityUtils.consume(response.getEntity());
                        byteStreamReader.close();
                        return Optional.of(stringContainer.toString());
                    }
                    log.error("Couldn't get a token. It seems than given URI is not for rest service, please check the URI: " + url);
                    EntityUtils.consume(response.getEntity());
                    byteStreamReader.close();

                } catch (IOException ex) {
                    log.error("Error while trying encode headers");
                    ex.printStackTrace();
                }

                return Optional.empty();

            case MediaType.APPLICATION_JSON_VALUE:
                log.error("Not supported media type send requested: " + mediaType);
                jsonResponse = Optional.empty();

                break;
            default:
                log.error("Not supported media type send requested: " + mediaType);
                jsonResponse = Optional.empty();
        }

        return jsonResponse;

    }

}
