package facebooknotifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class ProcessMessages {
    private ArrayList<String> openedPosts;
    private CheckForKeywords checkForKeywords = new CheckForKeywords();

    public ProcessMessages() {
        openedPosts = new ArrayList<>();
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
            for (Message message : messagesResponse.getMessages()) {
                if (counter < 2) {

                    Message fullMessage = service.users().messages().get("me", message.getId()).setFormat("RAW").execute();
                    System.out.println("\nViesti ID: " + fullMessage.getId());
                    String rawData = fullMessage.getRaw();
                    String rawMessage = DecodeMessage.decodeRawMessage(rawData);
                    String imgTxt = rawMessage.substring(rawMessage.lastIndexOf("Hei Kasimir") + 13, rawMessage.lastIndexOf("=3D=3D=3D") - 121);
                    String postId = rawMessage.substring(rawMessage.lastIndexOf("Message-ID: <") + 13, rawMessage.lastIndexOf("Message-ID: <") + 29);
                    String postLink = parseLink(rawMessage); 

                    //Decode imgtxt with UTF-8 and change it to lower case characters
                    try {
                        QuotedPrintableCodec codec = new QuotedPrintableCodec(StandardCharsets.UTF_8.name());
                        imgTxt = codec.decode(imgTxt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    imgTxt = imgTxt.toLowerCase();
                    
                    checkPostList(postId, postLink);
                    checkForKeywords.checkKeywords(imgTxt, postId);
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

    //Checks opened posts list and adds if new
    public void checkPostList(String postId, String postLink) {
        if (!openedPosts.contains(postId)) {
            openedPosts.add(postId);
            OpenFacebookPost.openFacebookPost(postLink);
        }
    }
}
