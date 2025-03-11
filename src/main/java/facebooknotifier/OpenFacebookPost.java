package facebooknotifier;

import java.io.IOException;

public class OpenFacebookPost {
    
    public static void openFacebookPost(String postLink) {
        String edgePath = "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe";
        try {
            Runtime.getRuntime().exec(new String[] {edgePath, postLink});

            Thread.sleep(7000);
            new ProcessBuilder("taskkill", "/F", "/IM", "msedge.exe").start();
        } catch (InterruptedException | IOException e) {
                e.printStackTrace();
        }
    }
}
