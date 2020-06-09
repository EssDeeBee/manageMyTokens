package com.ser.service.links;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


@Slf4j
public class LinkSenderTest {


    @Test
    public void sendCodeRequestForAccount() {

//        String accountName = "test";
//
//        log.info("Building email message...");
//        String smtpHost = PropertiesService.MailProperties.getSmtpAddress();
//        int smtpPort = PropertiesService.MailProperties.getSmtpPort();
//        String userName = PropertiesService.MailProperties.getAccountEmail().getAccountName();
//
//        String password = PropertiesService.MailProperties.getMailAccountPassword();
//        String transportStrategy = PropertiesService.MailProperties.getTransportStrategy();
//
//        Mailer mailer = MailerBuilder
//                .withSMTPServer(smtpHost, smtpPort, userName, password)
//                .withTransportStrategy(TransportStrategy.valueOf(transportStrategy))
//                .buildMailer();
//
//        String emailReceivers = PropertiesService.MailProperties.getReceivers();
//        String fromEmailAddress = PropertiesService.MailProperties.getAccountEmail().fullEmail;
//
//        log.info("Creating SMTP connection...");
//
//        Email email = EmailBuilder.startingBlank()
//                .to(emailReceivers)
//                .withSubject("Token " + accountName + " expired!")
//                .from("Token Service", fromEmailAddress)
//                .withHTMLText("Token for the accountName: " + accountName + " has expired </br></br> <h3>Click the link below to refresh the token:</h3> <a href= >Refresh the token</a>")
//                .buildEmail();
//
//        log.info("Sending email..." + email);
//        mailer.sendMail(email);
//
//        log.info("Email sent. Requested refresh web for the accountName: " + accountName + ". Email receivers: " + emailReceivers);
    }

    //    LinkSender.sendLinkViaEmail("any link","Sergey.Dzeboev@advlab.io");
}
