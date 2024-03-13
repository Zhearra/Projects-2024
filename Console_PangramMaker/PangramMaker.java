import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

//By Zhearra Adil
public class PangramMaker {
public static List<String> dictionary = new ArrayList<>(); //exists to prevent modifications to dictionary array
    static {
        try {
            Scanner fileRead = new Scanner(new File("words.txt"));
            while (fileRead.hasNext()) {
                dictionary.add(fileRead.next());
            }
            fileRead.close();
        } catch (FileNotFoundException e) {

        }
    }
    public static void main(String[] args) {

        char[] alphabet = {' ', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String pangramInput; //Set to "" so it can be empty when first called.
        String pangram = ""; // Current Resulting Pangram
        int totalWords = 0;
        int totalLetters = 0;

        boolean pangramComplete = false;
        boolean badPangramAlphabet;
        boolean badPangramDictionary;

        System.out.println("Welcome to Zhearra's Pangram Maker!");
        //Main loop
        while (!pangramComplete) {
            List<String> dictionaryRemovable = new ArrayList<String>(dictionary); //A clone of the dictionary that can be manipulated throughout the program
            Scanner keyboard = new Scanner(System.in);

            //1. Display the pangram created so far. If this is the beginning of the program, it will be empty.
            System.out.println("\nYour pangram so far is:" + pangram + "\nEnter the next word (in all uppercase) or enter \"help\" for suggestions: ");
            pangramInput = keyboard.nextLine(); //2. Ask the user to enter a word in uppercase (or “help” for suggestions) and get the word from the user.

            if (pangramInput.contains("help")) {
                getHelp(dictionaryRemovable, alphabet, pangram);
            }
            else {
                /*The code to detect if the pangram is complete/valid at all is in an else statement,
                so it can be skipped if "help" is typed and checked again when the main loop continues*/

                /*3. If the user enters something other than "help"
                check whether it is in the dictionary. If so, add it to the pangram; if not, print an error message. */

                badPangramAlphabet = inAlphabet(alphabet, pangramInput); //Checks if characters are valid
                badPangramDictionary = inDictionary(dictionary, pangramInput); // Checks if pangram is in dictionary

                if (!pangramInput.contains("help")) {
                    if (!badPangramAlphabet) {
                        System.out.println("\nError: Use uppercase A-Z and spaces only.");
                    } else if (!badPangramDictionary) {
                        System.out.println("\nError: Word not in dictionary or contains invalid characters: Please use uppercase A-Z and spaces only.");
                    } else {
                        pangram = pangram + " " + pangramInput;
                    } //dont want this to run if help is typed or error, so i put it in an else statement instead of leaving it on its own
                }

                //Between here and end of while, checks if pangram is complete, if so, break, else, repeat

                //pangramChecker method:
                pangramComplete = pangramCheck(alphabet, pangram);

                if (pangramComplete) {
                    //Gathers total letters excluding spaces.
                    for (int i = 0; i < pangram.length(); i++) {
                        if (pangram.charAt(i) != ' ') {
                            totalLetters++; //if the current letter is not a space, then letter count is increased
                        }
                    }

                    for (int i = 0; i < pangram.length(); i++) {
                        if (pangram.charAt(i) == ' ') {
                            totalWords++; //if there is a space, there must be a word before it; pangram always starts with a space as well, so it accounts for last word
                        }
                    }
                    //the last word would not have a space, so increments to accommodate

                    System.out.println("Your pangram is completed!\n" + pangram.substring(1) + "\nTotal Words: " + totalWords + "\nTotal Letters: " + totalLetters);
                    //Display total words and total letters in it
                    //Substring is there to ignore the first space that the pangram would otherwise have.

                }
            }
        }

    }

    public static void getHelp(List<String> dictionaryRemovable, char[] alphabet, String fullPangram) { //Function that provides a list of 5 words which will aid the user in completing the pangram

        ArrayList<String> potentialWords = new ArrayList<>(); //Fill some of the requirements to complete the pangram
        ArrayList<String> topPotentialWords = new ArrayList<>(); //Fills all of the requirements to complete the pangram
        ArrayList<String> lettersLeft = LettersLeft(alphabet, fullPangram);
        int[] amountOfMatchingLetters = new int[dictionaryRemovable.size()];
        Random randomPotentialWord = new Random();

            for (int k = 0; k < dictionaryRemovable.size(); k++) { //Parses through whole dictionary via a modifiable clone of its array
                amountOfMatchingLetters[k] = 0;
                for (int l = 0; l < dictionaryRemovable.get(k).length(); l++) { //Holds word to see if valid as potential word

                    for (int m = 0; m < lettersLeft.size(); m++) { //Final inner loop compares

                        if (dictionaryRemovable.get(k).charAt(l) == lettersLeft.get(m).charAt(0)) {
                            amountOfMatchingLetters[k]++;
                            //Sorts an array, amount of matching letters, to have indexes parallel to dictionary removable.

                        }
                    }
                }
            }

            for (int n = 0; n < amountOfMatchingLetters.length; n++) { //This entire loop sorts an array, potential words, from most viable to least viable words
                int max = 0;

                for (int o = 1; o < dictionaryRemovable.size(); o++) {
                    if (amountOfMatchingLetters[o] > amountOfMatchingLetters[max]) {
                       max = o;
                       /*Max is a value which is stored in amountofmatchingletters which parallel to dictionaryremoveable
                       So, index 0 of aOML should represent the amount of letters of the string at index 0 of dictionary removeable
                        */

                    }
                }
                potentialWords.add(dictionaryRemovable.get(max));

                //The following two strings of code are written to avoid repeating viable words, their indexes are also aligned
                dictionaryRemovable.remove(max);
                amountOfMatchingLetters[max] = 0;
            }

            for (int p = 0; p < potentialWords.size(); p++){ //Holds a potential word
                /*4A. Find all the words in the dictionary containing the maximum number of distinct letters not already present in the pangram.*/
                int count = 0;

                for (int q = 0; q < potentialWords.get(p).length(); q++) { //Parses through a potential word

                    for (int r = 0; r < lettersLeft.size(); r++) { //Compares potential word to amount of letters unused

                        if (potentialWords.get(p).charAt(q) == lettersLeft.get(r).charAt(0)) {
                            count++; //Counts how many of the letters in each word could fulfil the pangram
                            lettersLeft.remove(r); //So letters arent counted twice

                        }
                    }
                }
                lettersLeft = LettersLeft(alphabet, fullPangram); //Reinitialized so next word can be checked for missing letters
                if (count >= lettersLeft.size()) {
                    topPotentialWords.add(potentialWords.get(p));
                    /*If has as many letters needed or more than the amount of letters left needed,
                    then it can be added to a smaller list of "top potential words" to aid the user*/
                }
            }


            /*If there are less than 5 words which contain all needed letters,
            the delta words are printed which contain some or none of the needed letters;
            Ex: If no words containing 7 needed letters are found words containing 6, 5, etc... are printed in random order*/

        int potentialWordsDelta = 5 - topPotentialWords.size() ;
        //Initialized at this point because the latest size of the most fulfilling words is at this point
        //4B. If there are at least five such words, print five of them (without repeats in a random order).
        if (topPotentialWords.size() != 0) {
            for (int r = 0; r < 5; r++) {
                int randomIndex = randomPotentialWord.nextInt(topPotentialWords.size());
                System.out.println(topPotentialWords.get(randomIndex));
            }
            if (topPotentialWords.size() < 5) {
                for (int r = 0; r < potentialWordsDelta; r++) {
                    int randomIndex = randomPotentialWord.nextInt(potentialWords.size());
                    System.out.println(potentialWords.get(randomIndex));
                }
            }
        }
        else { /*If there are not five such words, print all of them in a random order, then print random
                words from the dictionary (allowing repeats) until you have printed five words. */

            for (int r = 0; r < potentialWordsDelta; r++) {
                int randomIndex = randomPotentialWord.nextInt(potentialWords.size());
                System.out.println(potentialWords.get(randomIndex));
            }
        }
    }

    public static boolean inAlphabet(char[] alphabet, String pangram) { //Function checks if current pangram is valid via alphabet

        boolean inAlphabet = false;

        for (int i = 0; i < pangram.length(); i++) { //Holds value of index at i for pangram. So, the X in Xylophone matched against A,B,C..
            for (int j = 0; j < alphabet.length; j++) { //Holds value of index at j for alphabet
                if (pangram.charAt(i) == alphabet[j]) {
                    inAlphabet = true;
                }
            }

        }
        return inAlphabet; //done outside of loop, so if case is true doesnt stop before checking all words NOT an ELIF

    }

    public static boolean inDictionary(List<String> dictionary, String pangram) { //Function checks if current pangram is valid via dictionary

        for (int i = 0; i < dictionary.size(); i++) {
            if (dictionary.get(i).equals(pangram)) {
                return true;
            }
        }
        return false;

    }
    public static boolean pangramCheck(char[] alphabet, String pangram) { //This function checks "win" condition.

        if (LettersLeft(alphabet, pangram).size() == 0) { //If there are no letters left then...
        return true;
        }
        return false;
    }

    public static ArrayList<String> LettersLeft(char[] alphabet, String pangram) { //Function stores unused letters into an ArrayList

        ArrayList<String> notUsedLetters = new ArrayList<>();

            //Initializes an ArrayList to contain the contents of the alphabet array
            for (int a = 0; a < alphabet.length; a++) {
                notUsedLetters.add(String.valueOf(alphabet[a]));
            }

            /* Compares state of current pangram to the new "notUsedLetters" array,
             if the pangram contains letters in the new array, those letters are removed from the array */
        for (int i = 0; i < pangram.length(); i++) {

            for (int j = 0; j < notUsedLetters.size(); j++) {
                if (pangram.charAt(i) == notUsedLetters.get(j).charAt(0)) {
                    notUsedLetters.remove(j);

                }
            }

        }
            return notUsedLetters;
        }
}