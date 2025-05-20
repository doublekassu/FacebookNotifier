package facebooknotifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Scanner;

import org.apache.commons.codec.net.QuotedPrintableCodec;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class ProcessMessages {
    private ArrayDeque<String> openedPosts;
    private CheckForKeywords checkForKeywords = new CheckForKeywords();
    private String folderLabel;
    private String facebookName;

    public ProcessMessages() throws IOException {
        openedPosts = new ArrayDeque<>();
        folderLabel = getFolderLabel();
        facebookName = getFacebookName();
    }

    private String getFolderLabel() throws IOException {
        Scanner labelScanner = new Scanner(Paths.get(CreateSettings.folderLabelPath), StandardCharsets.UTF_8.name());
        String folderLabel = labelScanner.useDelimiter("\\A").next();
        labelScanner.close();

        return folderLabel;
    }

    private String getFacebookName() throws IOException {
        Scanner facebookNameScanner = new Scanner(Paths.get(CreateSettings.facebookNamePath), StandardCharsets.UTF_8.name());
        String facebookName = facebookNameScanner.useDelimiter("\\A").next();
        facebookNameScanner.close();

        return facebookName;
    }
    
    public void processMessages(Gmail service) throws IOException {
        int counter = 0;
        
        ListMessagesResponse messagesResponse = service.users().messages().list("me")
                .setLabelIds(Collections.singletonList(folderLabel)).execute();
        
        if (messagesResponse.getMessages() == null || messagesResponse.getMessages().isEmpty()) {
            System.out.println("Messages weren't found");
        } else {
            for (Message message : messagesResponse.getMessages()) {
                if (counter < 5) {

                    Message fullMessage = service.users().messages().get("me", message.getId()).setFormat("RAW").execute();
                    String rawData = fullMessage.getRaw();
                    String rawMessage = DecodeMessage.decodeRawMessage(rawData);
                    String imgTxt = rawMessage.substring(rawMessage.lastIndexOf("Hei ") + facebookName.length() + 5, rawMessage.lastIndexOf("=3D=3D=3D") - 121);
                    String postId = rawMessage.substring(rawMessage.lastIndexOf("Message-ID: <") + 13, rawMessage.lastIndexOf("Message-ID: <") + 29);
                    String postLink = parseLink(rawMessage); 
                    System.out.println("\nFacebook post ID: " + postId);

                    //Decode imgtxt with UTF-8 and change it to lower case characters
                    try {
                        QuotedPrintableCodec codec = new QuotedPrintableCodec(StandardCharsets.UTF_8.name());
                        imgTxt = codec.decode(imgTxt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    imgTxt = imgTxt.toLowerCase();
                    
                    checkPostList(postId, postLink);
                    checkForKeywords.checkKeywords(imgTxt, postId, postLink);

                    System.out.println("----------------------------------------------------------------------------------------");
                    
                }
                counter++;
            }
            System.out.println("THESE POSTS HAVE BEEN ALREADY OPENED: " + openedPosts);
            System.out.println("\nTRIGGERED POSTS: " + checkForKeywords.getTriggeredPosts());
        }
    }

    public String parseLink(String rawMessage) {
        String postLink = rawMessage.substring(rawMessage.lastIndexOf("A4 Facebookissa") + 17, rawMessage.indexOf("=3D=3D", rawMessage.lastIndexOf("A4 Facebookissa") + 17));
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

    //Checks opened posts list and adds if new. Remove oldest post when post list is > 15.
    public void checkPostList(String postId, String postLink) {
        if (!openedPosts.contains(postId)) {
            openedPosts.add(postId);
            OpenFacebookPost.openFacebookPost(postLink);
            if (openedPosts.size() > 15) {
                openedPosts.pollFirst();
            }
        }
    }
}
