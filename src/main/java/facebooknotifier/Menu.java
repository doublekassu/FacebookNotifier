package facebooknotifier;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.gmail.Gmail;

public class Menu {
    public static void runMenu(Gmail service) throws GeneralSecurityException, IOException, InterruptedException {
        KeyWords keyWords = new KeyWords();
         while (true) {
            System.out.print("\n1. Start the program\n2. Change notification triggering keywords\n3. Settings\n0. Exit the program\n\nPlease select an operation by typing it and pressing ENTER: ");

            String menuChoice = MainApp.scanner.nextLine();
            if (menuChoice.equals("1")) {
                StartProgram.startProgram(service);
            }
            else if (menuChoice.equals("2")) {
                keyWords.runMenu();
                
            }
            else if (menuChoice.equals("3")) {
                Settings settings = new Settings();
                settings.settingsMenu(service);
            }
            else if (menuChoice.equals("0")) {
                System.exit(0);
            }
            else {
                System.out.println("\nINVALID INPUT!\n");
            }
         }
    }
}
