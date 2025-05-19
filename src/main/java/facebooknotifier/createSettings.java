package facebooknotifier;

import java.io.File;
import java.io.IOException;

public class createSettings {
    public static void createSettingsFiles() throws IOException {
        File settingsFolder = new File("./settings");
        File facebookNameFile = new File("./settings/facebook_name.txt");
        File folderLabelFile = new File("./settings/folder_label.txt");
        File keyNumbersFile = new File("./settings/keynumbers.txt");
        File keyWordsFile = new File("./settings/keywords.txt");


        if (!settingsFolder.exists() || !settingsFolder.isDirectory()) {
            settingsFolder.mkdirs();
        }
        if (!facebookNameFile.exists()) {
            facebookNameFile.createNewFile();
        }
        if (!folderLabelFile.exists()) {
            folderLabelFile.createNewFile();
        }
        if (!keyNumbersFile.exists()) {
            keyNumbersFile.createNewFile();
        }
        if (!keyWordsFile.exists()) {
            keyWordsFile.createNewFile();
        }
    }
}
