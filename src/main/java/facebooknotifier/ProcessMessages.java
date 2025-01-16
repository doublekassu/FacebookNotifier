package facebooknotifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class ProcessMessages {
    private ArrayList<String> openedPosts;
    private ArrayList<String> triggeredPosts;

    public ProcessMessages() {
        openedPosts = new ArrayList<>();
        triggeredPosts = new ArrayList<>();
    }
    

    public void processMessages(Gmail service) throws IOException {
        int counter = 0;

        ListMessagesResponse messagesResponse = service.users().messages().list("me")
                .setLabelIds(Collections.singletonList("Label_6175409980112628348")).execute();

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

                    checkPostList(postId);
                    checkKeywords(imgTxt, postId);
                    System.out.println("----------------------------------------------------------------------------------------");
                }
                counter++;
            }
            System.out.println("THESE POSTS HAVE BEEN ALREADY OPENED: " + openedPosts);
            System.out.println("\nTRIGGERED POSTS: " + triggeredPosts);
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

    public void checkKeywords(String imgTxt, String postId) {
        boolean containsKeyWord = false;

        //Check if the triggered post has already been added to the list
        for (String listElement : triggeredPosts) {
            if (listElement.contains(postId)) {
                return;
            }
        }
        
        //Check if imgtxt has Empire keywords 
        List<String> empireKeyWords = Arrays.asList("0.5", "0,5", "empire", "koli", "coin");
        for (int i=0; i<empireKeyWords.size(); i++) {
            if (imgTxt.contains(empireKeyWords.get(i))) {
                triggeredPosts.add(postId + ": EMPIRE");
                containsKeyWord = true;
                break;
            }
        }

        //Check if has Buff keywords
        if (checkStringForNumberBetween(imgTxt)) {
            triggeredPosts.add(postId + ": BUFF");
            containsKeyWord = true;
        }

        if (containsKeyWord) {
            openFacebookPost(postId);
        }
    }

    public boolean checkStringForNumberBetween(String imgTxt) {
        int minNumber = 80;
        int maxNumber = 94;

        //Regex that finds all numbers from msg
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(imgTxt);

        boolean checkNumbers = false;

        //Integrate all numbers
        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group());

            if (number >= minNumber && number <= maxNumber) {
                checkNumbers = true;
                break;
            }
        }
        return checkNumbers;
    }

    //Checks opened posts list and adds if new
    public void checkPostList(String postId) {
        if (!openedPosts.contains(postId)) {
            openedPosts.add(postId);
        }
    }
}
