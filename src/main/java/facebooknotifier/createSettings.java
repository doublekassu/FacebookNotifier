package facebooknotifier;

import java.io.File;
import java.io.IOException;

public class CreateSettings {

    //Settings files' paths for ease of use in methods
    public static final String facebookNamePath = "./settings/facebook_name.txt";
    public static final String folderLabelPath = "./settings/folder_label.txt";
    public static final String keyNumbersPath = "./settings/keynumbers.txt";
    public static final String keyWordsPath = "./settings/keywords.txt";

    public static void createSettingsFiles() throws IOException {
        File settingsFolder = new File("./settings");
        File facebookNameFile = new File(facebookNamePath);
        File folderLabelFile = new File(folderLabelPath);
        File keyNumbersFile = new File(keyNumbersPath);
        File keyWordsFile = new File(keyWordsPath);


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
