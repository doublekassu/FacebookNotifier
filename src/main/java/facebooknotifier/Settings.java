package facebooknotifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;

public class Settings {

    public void settingsMenu(Gmail service) throws IOException {
        while (true) {
            System.out.print("\n1. Set Gmail Label\n2. Set Facebook name\n0. Return to main menu\n\nPlease select an operation by typing it and pressing ENTER: ");

            String settingsChoice = MainApp.scanner.nextLine();

            if (settingsChoice.equals("1")) {
                setGmailLabel(service);
            }
            else if (settingsChoice.equals("2")) {
                setFacebookName();
            }
            else if (settingsChoice.equals("0")) {
                break;
            }
            else {
                System.out.println("INVALID INPUT!");
            }
        }
    }

    private void setGmailLabel(Gmail service) throws IOException {

        ListLabelsResponse listLabelsResponse = service.users().labels().list("me").execute();
        List<Label> labels = listLabelsResponse.getLabels();
        
        for (Label label : labels) {
            System.out.println("Name: " + label.getName() + "   ID: " + label.getId());
        }

        System.out.print("\n\nAbove are all your Gmail folders. Please copy and paste the label of the folder you would like to scan in the program: ");
        String label = MainApp.scanner.nextLine();

        BufferedWriter writer = new BufferedWriter(new FileWriter(CreateSettings.folderLabelPath));
        writer.write(label);
        writer.close();

        System.out.println("\nLabel successfully set!");
    }

    private void setFacebookName() throws IOException {
        System.out.print("Input your Facebook account's first name: ");
        String name = MainApp.scanner.nextLine();

        BufferedWriter writer = new BufferedWriter(new FileWriter(CreateSettings.facebookNamePath));
        writer.write(name);
        writer.close();

        System.out.println("\nName successfully set!");
    }
}
