package facebooknotifier;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.gmail.Gmail;
public class MainApp {
    private static final String APPLICATION_NAME = "Skin notifier";

    public static void main(String... args) throws IOException, GeneralSecurityException, InterruptedException {
        ProcessMessages processMessages = new ProcessMessages();
        final com.google.api.client.http.HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GetCredentials.getJSON_FACTORY() , GetCredentials.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        System.out.println("\n HERE STARTS NEW MESSAGES");
        // Jatkuva odottaminen uusille viesteille
        while (true) {
            processMessages.processMessages(service);;  // Käsitellään saapuneet viestit
            Thread.sleep(10000);  // Odotetaan 10 sekuntia ennen seuraavaa tarkistusta
        }
    }
}