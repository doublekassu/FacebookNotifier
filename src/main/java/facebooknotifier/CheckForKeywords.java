package facebooknotifier;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckForKeywords {

    private ArrayList<String> triggeredPosts;
    private TriggeredPostAlerter triggeredPostAlerter;

    public CheckForKeywords() {
        triggeredPosts = new ArrayList<>();
        try {
            triggeredPostAlerter = new TriggeredPostAlerter();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTriggeredPosts() {
        return triggeredPosts;
    }

    public void checkKeywords(String imgTxt, String postId) {
        boolean containsKeyWord = false;

        //Check if the triggered post has already been added to the list
        for (String listElement : triggeredPosts) {
            if (listElement.contains(postId)) {
                return;
            }
        }

        //Check if the skins are being sold in advance
        //Meaning: of the words: ennakko, lukko, huomenna, päivä, aukeaa
        List<String> sellingInAdvanceKeyWords = Arrays.asList("ennak", "luk", "huome", "pv", "auk");
        for (int i=0; i<sellingInAdvanceKeyWords.size(); i++) {
            if (imgTxt.contains(sellingInAdvanceKeyWords.get(i))) {
                triggeredPosts.add(postId + ": ENNAKKO");
                containsKeyWord = true;
                break;
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
}
