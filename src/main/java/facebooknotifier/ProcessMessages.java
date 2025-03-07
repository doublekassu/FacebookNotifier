package facebooknotifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.common.io.BaseEncoding;

public class ProcessMessages {
    private ArrayList<String> openedPosts;
    private ArrayList<String> triggeredPosts;
    private TriggeredPostAlerter triggeredPostAlerter;

    public ProcessMessages(TriggeredPostAlerter triggeredPostAlerter) {
        openedPosts = new ArrayList<>();
        triggeredPosts = new ArrayList<>();
        this.triggeredPostAlerter = triggeredPostAlerter;
    }
    

    public void processMessages(Gmail service) throws IOException {
        int counter = 0;
        
        ListMessagesResponse messagesResponse = service.users().messages().list("me")
                .setLabelIds(Collections.singletonList("Label_5017367702893122078")).execute();
        
        //Print all GMAIL labels' ids
        /*ListLabelsResponse listLabelsResponse = service.users().labels().list("me").execute();
        List<Label> labels = listLabelsResponse.getLabels();
        
        for (Label label : labels) {
            System.out.println("Name: " + label.getName() + "   ID: " + label.getId());
        }
        */

        if (messagesResponse.getMessages() == null || messagesResponse.getMessages().isEmpty()) {
            System.out.println("Messages weren't found");
        } else {
            System.out.println("\nTRIGGERED POSTS: " + triggeredPosts);
            for (Message message : messagesResponse.getMessages()) {
                if (counter < 1) {

                    Message fullMessage = service.users().messages().get("me", message.getId()).setFormat("RAW").execute();
                    System.out.println("\nViesti ID: " + fullMessage.getId());
                    String rawData = fullMessage.getRaw();
                    String rawMessage = DecodeMessage.decodeRawMessage(rawData);
                    String imgTxt = rawMessage.substring(rawMessage.lastIndexOf("Hei Kasimir") + 13, rawMessage.lastIndexOf("=3D=3D=3D") - 121);
                    String postId = rawMessage.substring(rawMessage.lastIndexOf("Message-ID: <") + 13, rawMessage.lastIndexOf("Message-ID: <") + 29);
                    String postLink = parseLink(rawMessage); 

                    //Switch image text to lower case characters only and decode with UTF-8
                    //imgTxt = imgTxt.toLowerCase();
                    
                    String hex = "E282AC";
                    byte[] bytes = BaseEncoding.base16().decode(hex.toUpperCase()); // Muuntaa hexin byte-taulukoksi
                    String decodedString = new String(bytes, StandardCharsets.UTF_8); // Muuntaa UTF-8-merkkijonoksi

                    System.out.println("Decoded String: " + decodedString); // Tulostaa: €
                    
                    checkPostList(postId, postLink);
                    checkKeywords(imgTxt, postId);
                    System.out.println("----------------------------------------------------------------------------------------");
                    
                }
                counter++;
            }
            System.out.println("THESE POSTS HAVE BEEN ALREADY OPENED: " + openedPosts);
            System.out.println("\nTRIGGERED POSTS: " + triggeredPosts);
        }
    }

    public String parseLink(String rawMessage) {
        String postLink = rawMessage.substring(rawMessage.lastIndexOf("A4 Facebookissa") + 17, rawMessage.lastIndexOf("&irms") + 9);
        String cleanPostLink = createCleanLink(postLink);
        return cleanPostLink;
    }

    public String createCleanLink (String postLink) {
        StringBuilder removeRowChangersLink = new StringBuilder(postLink);
        removeRowChangersLink.setCharAt(74, '\u0000');
        removeRowChangersLink.setCharAt(151, '\u0000');
        removeRowChangersLink.setCharAt(228, '\u0000');
        removeRowChangersLink.setCharAt(305, '\u0000');
        String postLinkNoRowChangers = removeRowChangersLink.toString();
        String remove3D = postLinkNoRowChangers.replaceAll("=3D", "=");
        String cleanPostLink = remove3D.replace("_text", "");
        cleanPostLink = cleanPostLink.replaceAll("[^a-zA-Z0-9-_.~:/?#@!$&'()*+,;%=]", "");
        return cleanPostLink;
    }

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

    public void checkKeywords(String imgTxt, String postId) {
        boolean containsKeyWord = false;

        //Check if the triggered post has already been added to the list
        for (String listElement : triggeredPosts) {
            if (listElement.contains(postId)) {
                return;
            }
        }
        
        //Check if imgtxt has Empire keywords 
        List<String> empireKeyWords = Arrays.asList("0.5", "0,5", "empire", "koli", "coin", "koin");
        for (int i=0; i<empireKeyWords.size(); i++) {
            if (imgTxt.contains(empireKeyWords.get(i))) {
                triggeredPosts.add(postId + ": EMPIRE");
                containsKeyWord = true;
                break;
            }
        }

        //Check if image text includes any integer + c for example 300c
        String integerAndC = "\\d+";
        Pattern pattern = Pattern.compile(integerAndC + "c");
        Matcher matcher = pattern.matcher(imgTxt);

        if (matcher.find() && containsKeyWord == false) {
            triggeredPosts.add(postId + ": EMPIRE");
            containsKeyWord = true;
        }

        //Check if imgtxt has Buff keywords
        if (checkStringForNumberBetween(imgTxt) && containsKeyWord == false) {
            triggeredPosts.add(postId + ": BUFF");
            containsKeyWord = true;
        }

        if (containsKeyWord) {
            triggeredPostAlerter.newPostAlertDiscord("1330963084965056616", imgTxt, postId);
        }
    }

    public boolean checkStringForNumberBetween(String imgTxt) {
        long minNumber = 70;
        long maxNumber = 94;

        //Regex that finds all numbers from msg
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(imgTxt);

        boolean checkNumbers = false;

        //Integrate all numbers
        while (matcher.find()) {
            long number = Long.parseLong(matcher.group());

            if (number >= minNumber && number <= maxNumber) {
                checkNumbers = true;
                break;
            }
        }
        return checkNumbers;
    }

    //Checks opened posts list and adds if new
    public void checkPostList(String postId, String postLink) {
        if (!openedPosts.contains(postId)) {
            openedPosts.add(postId);
            openFacebookPost(postLink);
        }
    }
}
