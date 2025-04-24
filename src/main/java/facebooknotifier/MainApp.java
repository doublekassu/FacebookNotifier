package facebooknotifier;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.gmail.Gmail;

public class MainApp {
    public static void main(String... args) throws IOException, GeneralSecurityException, InterruptedException {
        //Gmail API setup
        final com.google.api.client.http.HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GetCredentials.getJSON_FACTORY() , GetCredentials.getCredentials(HTTP_TRANSPORT))
                .setApplicationName("FacebookNotifier")
                .build();
        
        Menu.runMenu(service);
    }

    //Initialize the program's Scanner
    public static final Scanner scanner = new Scanner(System.in); 
}