package com.ser.service.links;

import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import com.ser.model.entity.OauthAccount;
import com.ser.service.property.PropertiesService;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class LinkSender {

    /**
     * Opens link via default browser if supported.
     *
     * @param uriLink String , this link will opened in a default browser.
     */
    @Deprecated
    public static void openLinkInBrowser(String uriLink) {
        if (Desktop.isDesktopSupported())
            try {
                Desktop.getDesktop().browse(new URI(uriLink));
            } catch (URISyntaxException | IOException ex) {
                log.error("Given URI cannot be open : \"" + uriLink + "\" ");
                ex.printStackTrace();
            }
        else log.error("Browser is not supported");
    }


    /**
     * Sends given link via email using smtp properties from inform.properties file.
     *
     * @param uriLink String ,this link will be send  via email.
     * @return void.
     */
    private static void sendLinkViaEmail(String uriLink, String accountName) {

        log.info("Building email message...");
        String smtpHost = PropertiesService.MailProperties.getSmtpAddress();
        int smtpPort = PropertiesService.MailProperties.getSmtpPort();
        String userName = PropertiesService.MailProperties.getEmailLogin();

        String password = PropertiesService.MailProperties.getMailPassword();
        String transportStrategy = PropertiesService.MailProperties.getTransportStrategy();

        Mailer mailer = MailerBuilder
                .withSMTPServer(smtpHost, smtpPort, userName, password)
                .withTransportStrategy(TransportStrategy.valueOf(transportStrategy))
                .buildMailer();

        String emailReceivers = PropertiesService.MailProperties.getReceivers();
        String fromEmailAddress = PropertiesService.MailProperties.getAccountEmail().fullEmail;

        log.info("Creating SMTP connection...");

        Email email = EmailBuilder.startingBlank()
                .to(emailReceivers)
                .withSubject("Token " + accountName + " expired!")
                .from("Token service", fromEmailAddress)
                .withHTMLText("Token for the accountName: " + accountName + " has expired </br></br> <h3>Click the link below to refresh the token:</h3> <a href= \"" + uriLink + "\">Refresh the token</a>")
                .buildEmail();

        log.info("Sending email...");
        mailer.sendMail(email);

        log.info("Email sent. Requested refresh web for the accountName: " + accountName + ". Email receivers: " + emailReceivers);
    }

    /**
     * Creates and sends the token link by email.
     *
     * @param oauthAccount The accountName for which code will be requested.
     */
    public void sendCodeRequestForAccount(OauthAccount oauthAccount) {

        //TODO change accountName login to request login
        log.info("Sending email");
        LinkSender.sendLinkViaEmail(buildTokenLink(oauthAccount), oauthAccount.getAccountName());
    }

    /**
     * Creates a token link and reruns it as a result.
     *
     * @param oauthAccount The accountName for which code will be requested.
     */
    public String buildTokenLink(OauthAccount oauthAccount) {

        String receiverControllerURI = "/oauth/code/receiver";
        String redirectURI = PropertiesService
                .CommonProperties
                .getHostAddress()
                + receiverControllerURI;

        log.info("Building uri using the given accountName: " + oauthAccount);
        String tokenRequestLink = LinkBuilder.getNewTokenRequestLink(oauthAccount, redirectURI);

        log.info("URI to get a web is: " + tokenRequestLink);
        return tokenRequestLink;
    }
}




