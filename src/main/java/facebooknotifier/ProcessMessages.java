package facebooknotifier;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.Desktop;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class ProcessMessages {
    private ArrayList<String> openedPosts;
    public ProcessMessages() {
        openedPosts = new ArrayList<>();
    }
    

    public void processMessages(Gmail service) throws IOException {
        int counter = 0;
        List<String> keyWordList = Arrays.asList("0.5", "0,5", "empire", "koli", "coin", "8", "90", "91", "92", "93", "94");
        ArrayList<String> triggeredPosts = new ArrayList<>();

        ListMessagesResponse messagesResponse = service.users().messages().list("me")
                .setLabelIds(Collections.singletonList("INBOX"))  // Oletustunniste, voit vaihtaa tarpeen mukaan
                .setQ("category:personal")
                .execute();

        if (messagesResponse.getMessages() == null || messagesResponse.getMessages().isEmpty()) {
            System.out.println("Messages weren't found");
        } else {
            System.out.println("\nTRIGGERED POSTS: " + triggeredPosts);
            for (Message message : messagesResponse.getMessages()) {
                if (counter < 5) {

                    Message fullMessage = service.users().messages().get("me", message.getId()).setFormat("RAW").execute();
                    System.out.println("\nViesti ID: " + fullMessage.getId());
                    String rawData = fullMessage.getRaw();
                    String rawMessage = DecodeMessage.decodeRawMessage(rawData);
                    String imgTxt = rawMessage.substring(rawMessage.lastIndexOf("Hei Kasimir") + 13, rawMessage.lastIndexOf("=3D=3D=3D") - 121);
                    String postId = rawMessage.substring(rawMessage.lastIndexOf("Message-ID: <") + 13, rawMessage.lastIndexOf("Message-ID: <") + 29);
                    System.out.println(imgTxt);
                    /*for (int i=0; i<keyWordList.size(); i++) {
                        if (imgTxt.contains(keyWordList.get(i))) {
                            if (!triggeredPosts.contains(fullMessage.getId())) {
                                triggeredPosts.add(fullMessage.getId() + " keyword: " + keyWordList.get(i));
                                break;
                            }
                        }
                    }*/

                    checkPostList(postId);
                    System.out.println("----------------------------------------------------------------------------------------");
                }
                counter++;
            }
            System.out.println("THESE POSTS HAVE BEEN ALREADY OPENED: " + openedPosts);
        }
    }

    public static void openFacebookPost(String postId) {
        String facebookUrl = "https://facebook.com/groups/csgofinland/permalink/" + postId;
        String[] command = {"cmd.exe", "/c", "start", "msedge", facebookUrl};

        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //Checks opened posts list and adds if new
    public void checkPostList(String postId) {
        if (!openedPosts.contains(postId)) {
            openedPosts.add(postId);
            openFacebookPost(postId);
        }
    }
}
