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

        //Checking every 10 seconds for new messages
        while (true) {
            processMessages.processMessages(service);
            Thread.sleep(10000);
        }
    }
}