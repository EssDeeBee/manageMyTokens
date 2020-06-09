package com.ser.service.property;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

public final class PropertiesService {

    private static final String COMMON_PROPERTIES_PATH = "config/config.properties";
    private static final String MAIL_PROPERTIES_PATH = "config/mail.properties";
    private static final String HOST_ADDRESS = "host_address";
    private static final String EXTERNAL_HOST_ADDRESS = "yandex.ru";
    private static final String DEFAULT_HOST_ADDRESS = "localhost";
    private static final String MAIL_SMTP_ADDRESS = "mail_smtp_address";
    private static final String MAIL_SMTP_PORT = "mail_smtp_port";
    private static final String MAIL_TRANSPORT_STRATEGY = "mail_transport_strategy";
    private static final String MAIL_RECEIVERS_LIST = "mail_receivers_list";
    private static final String MAIL_ACCOUNT_EMAIL = "mail_account_email";
    private static final String MAIL_SMTP_LOGIN = "mail_smtp_login";
    private static final String MAIL_SMTP_PASSWORD = "mail_smtp_password";


    private static final Logger logger = LoggerFactory.getLogger(PropertiesService.class);

    private static Properties getCommonProperties() {
        Properties commonProperties = new Properties();

        try (FileReader commonPropertiesFileReader = new FileReader(COMMON_PROPERTIES_PATH)) {
            commonProperties.load(commonPropertiesFileReader);
        } catch (IOException ex) {
            logger.error("Could't find config.properties file, check if path exists:\"" + COMMON_PROPERTIES_PATH + "\"");
            ex.printStackTrace();
        }
        return commonProperties;
    }

    private static Properties getMailProperties() {
        Properties mailProperties = new Properties();

        try (FileReader mailPropertiesFileReader = new FileReader(MAIL_PROPERTIES_PATH)) {
            mailProperties.load(mailPropertiesFileReader);
        } catch (IOException ex) {
            logger.error("Could't find mail.properties file, check if path exists:\"" + MAIL_PROPERTIES_PATH + "\"");
            ex.printStackTrace();
        }
        return mailProperties;
    }

    public static class CommonProperties {

        private static final String accessCode = "1988";

        public static String getHostAddress() {
            String hostAddress = getCommonProperties().getProperty(HOST_ADDRESS);
            String ipAddress;
            if (hostAddress != null && !hostAddress.isEmpty()) {
                return hostAddress;
            }
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(EXTERNAL_HOST_ADDRESS, 80));
                ipAddress = "https://" + socket.getLocalAddress().getHostAddress();
                if (ipAddress.equals("0.0.0.0"))
                    return DEFAULT_HOST_ADDRESS;

            } catch (IOException ex) {
                return DEFAULT_HOST_ADDRESS;
            }
            logger.info("Host address property is empty, automatic IP Address detection applied, IP: " + ipAddress + " returned as host");
            return ipAddress;
        }

        public static String getAccessCode() {
            return accessCode;
        }

        private CommonProperties() {
        }
    }

    public static class MailProperties {
        public static String getSmtpAddress() {
            return getMailProperties().getProperty(MAIL_SMTP_ADDRESS);
        }

        public static int getSmtpPort() {
            return Integer.decode(getMailProperties().getProperty(MAIL_SMTP_PORT));
        }

        public static String getTransportStrategy() {
            return getMailProperties().getProperty(MAIL_TRANSPORT_STRATEGY).toUpperCase();
        }

        public static String getReceivers() {
            String replaceableSymbols = "(\\s+|,+)";
            return getMailProperties().getProperty(MAIL_RECEIVERS_LIST).replaceAll(replaceableSymbols, ";");
        }

        public static AccountEmail getAccountEmail() {
            return new AccountEmail(getMailProperties().getProperty(MAIL_ACCOUNT_EMAIL));
        }

        public static String getEmailLogin() {
            return getMailProperties().getProperty(MAIL_SMTP_LOGIN);
        }

        public static String getMailPassword() {
            return getMailProperties().getProperty(MAIL_SMTP_PASSWORD);
        }

        static public class AccountEmail {
            public String fullEmail;

            public String getAccountName() {
                if (fullEmail != null)
                    return fullEmail.substring(0, fullEmail.indexOf('@'));
                return null;
            }

            public String getDomainName() {
                if (fullEmail != null)
                    return fullEmail.substring(fullEmail.indexOf('@') + 1);
                return null;
            }

            private AccountEmail(String fullEmail) {
                this.fullEmail = fullEmail;
            }
        }

        private MailProperties() {
        }
    }

    private PropertiesService() {
    }
}
