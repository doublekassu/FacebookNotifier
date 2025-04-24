package facebooknotifier;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.gmail.Gmail;

public class StartProgram {
    public static void startProgram(Gmail service) throws GeneralSecurityException, IOException, InterruptedException {
        ProcessMessages processMessages = new ProcessMessages();
            
        //Checking every 10 seconds for new Gmail messages
        
        while (true) {
            processMessages.processMessages(service);
            Thread.sleep(10000);
        }
    }
}
