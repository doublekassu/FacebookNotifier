package facebooknotifier;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

public class CheckForKeywords {
    private ArrayList<String> triggeredPosts;
    private TriggeredPostAlerter triggeredPostAlerter;

    private ArrayList<String> keyWordsTxtList;
    private ArrayList<Float> keyNumbersList;
    private HashMap<String, List<String>> keyWordCategoryMap = new HashMap<>();
    private HashMap<String, List<Float>> keyNumberCategoryMap = new HashMap<>();

    public CheckForKeywords()  {
        triggeredPosts = new ArrayList<>();
        keyWordsTxtList = new ArrayList<>();
        keyNumbersList = new ArrayList<>();
        keyWordLineToList();
        keyNumberLineTolist();

        try {
            triggeredPostAlerter = new TriggeredPostAlerter();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void keyWordLineToList() {

        try {
            String categoryKey = null;

            Scanner fileScanner = new Scanner(new File("./settings/keywords.txt"));
            
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
                        keyWordsTxtList.add(value);
                    }
                    lineIterator++;
                }

                //A copy is taken of the row's arraylist and put into a hashmap with the category name as the key
                List<String> keyWordTxtListCopy = new ArrayList<>(keyWordsTxtList);
                keyWordCategoryMap.put(categoryKey, keyWordTxtListCopy);
                
                //Original arraylist is cleared for the next row
                keyWordsTxtList.clear();

                //RowsScanner can be opened and closed on every iteration. (System.in scanners can only be closed once during execution)
                rowScanner.close();
            }
            
            fileScanner.close();
            System.out.println(keyWordCategoryMap);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    public void keyNumberLineTolist() {
        String categoryKey = null;

        Scanner fileScanner;
        try {
            fileScanner = new Scanner(new File("./settings/keynumbers.txt"));
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
                        keyNumbersList.add(Float.parseFloat(value));
                    }
                    lineIterator++;
                }
                //A copy is taken of the row's arraylist and put into a hashmap with the category name as the key
                List<Float> keyNumberListCopy = new ArrayList<>(keyNumbersList);
                keyNumberCategoryMap.put(categoryKey, keyNumberListCopy);
                
                //Original arraylist is cleared for the next row
                keyNumbersList.clear();
    
                //RowsScanner can be opened and closed on every iteration. (System.in scanners can only be closed once during execution)
                rowScanner.close();
    
            }
    
            System.out.println("Keynumbers: " + keyNumberCategoryMap);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTriggeredPosts() {
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
                    triggeredPosts.add(postId);
                    containsKeyWord = true;
                    categoryName = key;
                    System.out.println("KEYWORD (HASHMAPISSA) LÖYTYI POSTAUKSESTA");
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
            triggeredPostAlerter.newPostAlertDiscord("1330963084965056616", imgTxt, postId, postLink, categoryName);
        }
    }

    public boolean checkStringForNumberBetween(String imgTxt) {
        return true;
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
