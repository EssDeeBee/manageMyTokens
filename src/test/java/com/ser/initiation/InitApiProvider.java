package com.ser.initiation;

import com.ser.model.entity.*;
import com.ser.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.Date;

@Service
public class InitApiProvider {
//    @Autowired
//    com.ser.rest.InitApiProvider initApiProvider;
//
//    @RequestMapping(value = "/data_init", method = RequestMethod.GET)
//    public boolean initiateData(){
//        initApiProvider.createGoogleApiProvider();
//        initApiProvider.createMyTargetApiProvider();
//        initApiProvider.createYandexApiProvider();
//        return true;
//    }

    private OauthApiProviderRepository oauthApiProviderRepository;
    private OauthAccountRepository oauthAccountRepository;
    private OauthClientRepository oauthClientRepository;
    private OauthClientTokenRepository oauthClientTokenRepository;
    private OauthAccountTokenRepository oauthAccountTokenRepository;

    public void createGoogleApiProvider() {
        OauthApiProvider oauthApiProviderGoogle = new OauthApiProvider();
        OauthAccount oauthAccountGoogle = new OauthAccount();

        oauthApiProviderGoogle.setProviderName("Google");
        oauthApiProviderGoogle.setProviderCodeUrl("https://accounts.google.com/o/oauth2/auth");
        oauthApiProviderGoogle.setProviderTokenFromCodeUrl("https://www.googleapis.com/oauth2/v4/token");
        oauthApiProviderGoogle.setDefaultClientId("1010277423834-br0pkjjh3r4tbmnrr78ge1mmbjgvm1ph.apps.googleusercontent.com");
        oauthApiProviderGoogle.setDefaultClientSecret("LKTjxbJYgxqvC7_lgMpTjf2X");

        oauthAccountGoogle.setStatus("");
        oauthAccountGoogle.setAccountName("one.statistics@gmail.com");
        oauthAccountGoogle.setClientId("1010277423834-br0pkjjh3r4tbmnrr78ge1mmbjgvm1ph.apps.googleusercontent.com");
        oauthAccountGoogle.setClientSecret("LKTjxbJYgxqvC7_lgMpTjf2X");
        oauthAccountGoogle.setResponseType("code");
        oauthAccountGoogle.setScope("https://www.googleapis.com/auth/analytics");
        oauthAccountGoogle.setState("");

        oauthApiProviderRepository.save(oauthApiProviderGoogle);

        oauthAccountGoogle.setOauthApiProvider(oauthApiProviderGoogle);
        oauthAccountRepository.save(oauthAccountGoogle);

    }

    public void createYandexApiProvider() {
        OauthApiProvider oauthApiProviderYandex = new OauthApiProvider();
        OauthAccount oauthAccountYandex = new OauthAccount();

        oauthApiProviderYandex.setProviderName("Yandex");
        oauthApiProviderYandex.setProviderCodeUrl("https://oauth.yandex.ru/authorize");
        oauthApiProviderYandex.setProviderTokenFromCodeUrl("https://oauth.yandex.ru/token");
        oauthApiProviderYandex.setDefaultClientId("bf2f88e9a92f4ee1ae5bf6c1afd82fff");
        oauthApiProviderYandex.setDefaultClientSecret("ed159946bae348859e8fb5629f976d10");

        oauthAccountYandex.setStatus("");
        oauthAccountYandex.setAccountName("HavasDigitalHyundai@yandex.ru");
        oauthAccountYandex.setClientId("bf2f88e9a92f4ee1ae5bf6c1afd82fff");
        oauthAccountYandex.setClientSecret("ed159946bae348859e8fb5629f976d10");
        oauthAccountYandex.setResponseType("code");
        oauthAccountYandex.setState("");

        oauthApiProviderRepository.save(oauthApiProviderYandex);

        oauthAccountYandex.setOauthApiProvider(oauthApiProviderYandex);
        oauthAccountRepository.save(oauthAccountYandex);

    }

    public void createMyTargetApiProvider() {
        OauthApiProvider oauthApiProviderMyTarget = new OauthApiProvider();
        OauthAccount oauthAccountMyTarget = new OauthAccount();
        OauthClient oauthClient = new OauthClient();
        OauthClientToken oauthClientToken = new OauthClientToken();
        OauthAccountToken oauthAccountToken = new OauthAccountToken();

        oauthApiProviderMyTarget.setProviderName("MyTarget");
        oauthApiProviderMyTarget.setProviderCodeUrl("https://target.my.com/api/v2/oauth2/token.json");
        oauthApiProviderMyTarget.setProviderTokenFromCodeUrl("https://target.my.com/api/v2/oauth2/token.json");
        oauthApiProviderMyTarget.setDefaultClientId("n5zvIiexebHHazsF");
        oauthApiProviderMyTarget.setDefaultClientSecret(
                "NoETjJTbsmAyE752NUakv2bpExZii88W2U1wddWPywayBcj2sjDVMr4fzFyvY87HxWtt7tJMoUNPY6K6aI2U3PjjROJRisTbzQMbfLiogsuN70631Bb3VoBxsIbUIroICpPCAJBfJ1aGijYianGKfzVuwLDn1esiTFntX5Gm0dWPA6XjYenpCXfrMrLbONcU8I2MEDTg5mGzPsQgpdMekjA5gLtHSfxJokhoN8");

        oauthAccountMyTarget.setStatus("");
        oauthAccountMyTarget.setAccountName("ttm_algorithm_trading@mail.ru");
        oauthAccountMyTarget.setClientId("n5zvIiexebHHazsF");
        oauthAccountMyTarget.setClientSecret(
                "NoETjJTbsmAyE752NUakv2bpExZii88W2U1wddWPywayBcj2sjDVMr4fzFyvY87HxWtt7tJMoUNPY6K6aI2U3PjjROJRisTbzQMbfLiogsuN70631Bb3VoBxsIbUIroICpPCAJBfJ1aGijYianGKfzVuwLDn1esiTFntX5Gm0dWPA6XjYenpCXfrMrLbONcU8I2MEDTg5mGzPsQgpdMekjA5gLtHSfxJokhoN8");
        oauthAccountMyTarget.setResponseType("code");
        oauthAccountMyTarget.setState("");

        oauthClient.setClientMailId("f2dc1dd518@agency_client");
        oauthClient.setClientName("ExxonMobil_ADV_Programmatic_CPC");


        oauthClientToken.setAccessToken(
                "t0mmrNlxgOT6ahyx9QsbI5KGNvpdVhaSB42bbvOJNmWWdj0YZz4z2SyGHM3QExVPXqnCWEOKY0Z1xkDjJFS7x1UcEj8JGshLApl4pvW85HNomG6QW5WUnAeQlqAw66MrfOReQGrLowUZ9zqknanx4c1G2S5AaetinSRqwQ2Mf6QDaA1W7eCtY7lDDy4QnG1zhFZb7jwjwUiWG6WzwZyGjRPaqGD");
        oauthClientToken.setRefreshToken(
                "wa9DX8qf2dPVQNqgixYK7awICDdozQaVI3ZrWyL2Q8jlxVMSYZLWlVtt1Xa3ihKrfbAqkffOXTPVR0cE7zmCJSDovIzIwIQYRxFNLrDVMBPiSFfebCnXXXHf688Dg8CZY8rdbzkZrEvKckumuvwx7GaU2XQAZeZJGez1myD3I0SkmAba0fzrMKAqmUklrYngbQocuPBbYitWdA278o4OVLvRxJlLXqIsI77EA5BdaP92QeWjBrSwnNx76T2xp");
        oauthClientToken.setTokenExpiresDate(new Timestamp(new Date().getTime()));
        oauthClientToken.setTokenExpiresIn(1L);

        oauthAccountToken.setAccessToken(
                "JS6YxQuR4yqnOWGxlJptoHGCBvdxiBsWTN8cOHLc06xEUvA95jvMCx9jJbv56IOanUQycIsRVhpGQE1nyNjIfh49jgmPnloaD5wffg9l9eE7MYCVdYkA59thWGWy8cWr4e3buRyPRaa8odxcuGRIrHM0oJdM9LkJQIQJOi3Ygh354Fx9QTFYkQ6JfkSqNtPSfsOpFFA7TB4HhdDSJZ5eL1RUFJVijtAkeCUC52IqxnKvF5PAQal");
        oauthAccountToken.setRefreshToken(
                "o6mwooG5Uh6n8ivbRSC78NK1t4rbXCF9YojaMw9sPhoviiMw6kwfjo5g6XJkY7iabm0OynbOuows39LvR2hidXde974Mglbm62oFQoZTjIBmxTTKV99sskyAAZGpOrbFWTMlXuo0ZeuVSu01bohO9ONEqoTcr4q4KZqMPPFxNyU6EoAbrqHNh04cZZvMo1fnOMG5pJt8wHV3LLoTgtj4Zo2ms1lVWDfyD");
        oauthAccountToken.setTokenExpiresDate(new Timestamp(new Date().getTime()));
        oauthAccountToken.setTokenExpiresIn(1L);


        oauthApiProviderRepository.save(oauthApiProviderMyTarget);

        oauthAccountMyTarget.setOauthApiProvider(oauthApiProviderMyTarget);
        oauthAccountRepository.save(oauthAccountMyTarget);

        oauthClient.setOauthAccount(oauthAccountMyTarget);
        oauthClientRepository.save(oauthClient);

        oauthAccountToken.setOauthAccount(oauthAccountMyTarget);
        oauthAccountTokenRepository.save(oauthAccountToken);

        oauthClientToken.setOauthClient(oauthClient);
        oauthClientTokenRepository.save(oauthClientToken);


    }

    public void createVkontakteApiProvider() {
        OauthApiProvider oauthApiProviderGoogle = new OauthApiProvider();
        OauthAccount oauthAccountGoogle = new OauthAccount();

        oauthApiProviderGoogle.setProviderName("VKontakte");
        oauthApiProviderGoogle.setProviderCodeUrl("https://oauth.vk.com/authorize");
        oauthApiProviderGoogle.setProviderTokenFromCodeUrl("https://oauth.vk.com/access_token");
        oauthApiProviderGoogle.setDefaultClientId("6181259");
        oauthApiProviderGoogle.setDefaultClientSecret("4ttlzVNR9yB7L25CAH47");

        oauthAccountGoogle.setStatus("");
        oauthAccountGoogle.setAccountName("79684801943");
        oauthAccountGoogle.setClientId("6181259");
        oauthAccountGoogle.setClientSecret("4ttlzVNR9yB7L25CAH47");
        oauthAccountGoogle.setResponseType("code");
        //    oauthAccountGoogle.setScope("https://www.googleapis.com/auth/analytics");
        oauthAccountGoogle.setState("");

        oauthApiProviderRepository.save(oauthApiProviderGoogle);

        oauthAccountGoogle.setOauthApiProvider(oauthApiProviderGoogle);
        oauthAccountRepository.save(oauthAccountGoogle);

    }


    @Autowired
    public InitApiProvider(
            OauthApiProviderRepository oauthApiProviderRepository,
            OauthAccountRepository oauthAccountRepository,
            OauthClientRepository oauthClientRepository,
            OauthClientTokenRepository oauthClientTokenRepository,
            OauthAccountTokenRepository oauthAccountTokenRepository
    ) {

        this.oauthApiProviderRepository = oauthApiProviderRepository;
        this.oauthAccountRepository = oauthAccountRepository;
        this.oauthClientRepository = oauthClientRepository;
        this.oauthClientTokenRepository = oauthClientTokenRepository;
        this.oauthAccountTokenRepository = oauthAccountTokenRepository;
    }
}
