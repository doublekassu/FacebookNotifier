package facebooknotifier;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class CheckForKeywords {

    //Lisää keywords.txt olevat stringit listaan ja checkKeywords funktiossa tarkastetaan listaa läpi

    private ArrayList<String> triggeredPosts;
    private TriggeredPostAlerter triggeredPostAlerter;

    private ArrayList<String> keyWordsTxtList;

    public CheckForKeywords()  {
        triggeredPosts = new ArrayList<>();
        keyWordsTxtList = new ArrayList<>();
        keyWordTxtToList();

        try {
            triggeredPostAlerter = new TriggeredPostAlerter();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void keyWordTxtToList()  {
        Scanner keyWordScanner;
        try {
            keyWordScanner = new Scanner(new File("./settings/keywords.txt"));
            while (keyWordScanner.hasNext()) {
                keyWordsTxtList.add(keyWordScanner.next());
            }
            keyWordScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTriggeredPosts() {
        return triggeredPosts;
    }

    public void checkKeywords(String imgTxt, String postId, String postLink) {
        boolean containsKeyWord = false;

        //Check if the triggered post has already been added to the list
        for (String listElement : triggeredPosts) {
            if (listElement.contains(postId)) {
                return;
            }
        }

        for (int i=0; i<keyWordsTxtList.size(); i++) {
            if (imgTxt.contains(keyWordsTxtList.get(i))) {
                triggeredPosts.add(postId);
                containsKeyWord = true;
                break;
            }
        }

        //Check if the post's text includes numbers between the set keynumbers
        /*if (checkStringForNumberBetween(imgTxt) && containsKeyWord == false) {
            containsKeyWord = true;
        }*/

        if (containsKeyWord) {
            triggeredPostAlerter.newPostAlertDiscord("1330963084965056616", imgTxt, postId, postLink);
        }
    }

    //Check if the post's text includes numbers between the set keynumbers
    /*public boolean checkStringForNumberBetween(String imgTxt) {
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
    }*/
}
