package facebooknotifier;

import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

public class CheckForKeywords {
    private ArrayDeque<String> triggeredPosts;
    private TriggeredPostAlerter triggeredPostAlerter;

    private ArrayList<String> keyWordsTxtList;
    private ArrayList<Float> keyNumbersList;
    private HashMap<String, List<String>> keyWordCategoryMap = new HashMap<>();
    private HashMap<String, List<Float>> keyNumberCategoryMap = new HashMap<>();

    public CheckForKeywords()  {
        triggeredPosts = new ArrayDeque<>();
        System.out.println("WHEN EMPTY, ARRAYDEQUE'S SIZE IS: " + triggeredPosts.size());
        keyWordsTxtList = new ArrayList<>();
        keyNumbersList = new ArrayList<>();
        lineValuesToList("./settings/keywords.txt");
        lineValuesToList("./settings/keynumbers.txt");

        try {
            triggeredPostAlerter = new TriggeredPostAlerter();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void lineValuesToList(String filePath) {
        String categoryKey = null;

        Scanner fileScanner;
        try {
            fileScanner = new Scanner(new File(filePath));
            while (fileScanner.hasNextLine()) {
                int lineIterator = 0;
                String row = fileScanner.nextLine();
                Scanner rowScanner = new Scanner(row);  
                String value;
    
                while (rowScanner.hasNext()) {
                    value = rowScanner.next();
                    if (lineIterator == 0) {
                        categoryKey = value;
                    }
                    else {
                        if (filePath.contains("keynumbers")) {
                            keyNumbersList.add(Float.parseFloat(value));
                        }
                        else if (filePath.contains("keywords")) {
                            keyWordsTxtList.add(value);
                        }
                    }
                    lineIterator++;
                }

                if (filePath.contains("keynumbers")) {
                     //A copy is taken of the row's arraylist and put into a hashmap with the category name as the key
                    List<Float> keyNumberListCopy = new ArrayList<>(keyNumbersList);
                    keyNumberCategoryMap.put(categoryKey, keyNumberListCopy);
                    
                    //Original arraylist is cleared for the next row
                    keyNumbersList.clear();
                }
                else if (filePath.contains("keywords")) {
                    List<String> keyWordTxtListCopy = new ArrayList<>(keyWordsTxtList);
                    keyWordCategoryMap.put(categoryKey, keyWordTxtListCopy);
                    
                    keyWordsTxtList.clear();
                }

                //RowScanner can be opened and closed on every iteration. (System.in scanners can only be closed once during execution)
                rowScanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayDeque<String> getTriggeredPosts() {
        return triggeredPosts;
    }

    public void checkKeywords(String imgTxt, String postId, String postLink) {
        boolean containsKeyWord = false;
        String categoryName = "";
        float minNumber = 0;
        float maxNumber = 0;

        //Check if the triggered post has already been added to the list
        for (String listElement : triggeredPosts) {
            if (listElement.contains(postId)) {
                return;
            }
        }

        for (String key : keyWordCategoryMap.keySet()) {
            List<String> keyWordList = keyWordCategoryMap.get(key);
            for (String keyWord : keyWordList) {
                if (imgTxt.contains(keyWord)) {
                    containsKeyWord = true;
                    categoryName = key;
                    break;
                }
            }
        }

        for (String key : keyNumberCategoryMap.keySet()) {
            categoryName = key;
            List<Float> keyNumberList = keyNumberCategoryMap.get(key);
            for (int i = 0; i < keyNumberList.size(); i++) {
                if (i == 0) {
                    minNumber = keyNumberList.get(i);
                }
                else if (i == 1) {
                    maxNumber = keyNumberList.get(i);
                }
            }
        }

        //Regex that finds all numbers from msg
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(imgTxt);

        boolean containsKeyNumber = false;

        //Integrate all numbers
        while (matcher.find()) {
            long number = Long.parseLong(matcher.group());

            if (number >= minNumber && number <= maxNumber) {
                containsKeyNumber = true;
                break;
            }
        }

        if (containsKeyWord || containsKeyNumber) {
            triggeredPostsAdd(triggeredPosts, postId);
            triggeredPostAlerter.newPostAlertDiscord("1330963084965056616", imgTxt, postId, postLink, categoryName);
        }
    }

    //Separate method for adding triggered posts to remove the oldest one when the size is x
    private void triggeredPostsAdd(ArrayDeque<String> triggeredPosts, String postId) {
        if (triggeredPosts.size() > 4) {
            System.out.println("The size of triggeredPosts is more than 4. Removing the first value.");
            triggeredPosts.pollFirst();
        }
        triggeredPosts.add(postId); 
    }
}
