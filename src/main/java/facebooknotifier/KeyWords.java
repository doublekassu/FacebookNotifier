package facebooknotifier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;
import java.awt.Desktop;

public class KeyWords {

    private File keyWordFile = new File("./settings/keywords.txt");

    public void runMenu() throws IOException {
        while (true) {
            System.out.print("\n1. Set keywords\n2. Set a number between x and y as a keyword\n3. Print keywords\n0. Exit to main menu\n\nPlease select an operation by typing it and pressing ENTER: ");
            String menuChoice = MainApp.scanner.nextLine();

            if (menuChoice.equals("1")) {
                changeKeyWords();
            }
            else if (menuChoice.equals("3")) {
                printKeyWords();
            }
            else if (menuChoice.equals("0")) {
                break;
            }
            else {
                System.out.println("\nINVALID INPUT!\n");
            }
        }
    }

    private void printKeyWords() throws IOException {
        Scanner scanner = new Scanner(Paths.get("./keywords.txt"), StandardCharsets.UTF_8.name());
                String content = scanner.useDelimiter("\\A").next();
                scanner.close();
                System.out.println("\nCurrent keywords set: " + content);
    }

    private void changeKeyWords() {
        System.out.println("\nSet keywords that will send you a notification on Discord if they're included in a Facebook post! The keywords are categorized by lines. The first word of each line is the name of the category and it's not included in the scans! Please leave a whitespace between each keyword!\nExample of keyword category: category_name keyword1 keyword2");
        Desktop desktop = Desktop.getDesktop();
        if (keyWordFile.exists()) {
            try {
                desktop.open(keyWordFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
