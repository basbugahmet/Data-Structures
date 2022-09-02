import java.io.File;
import java.util.*;
import java.util.Scanner;

public class HashedDictionaryTest {

    public static void main(String[] args) throws Exception {

        String DELIMITERS = "[-+=" +
                " " +        //space
                "\r\n " +    //carriage return line fit
                "1234567890" + //numbers
                "’'\"" +       // apostrophe
                "(){}<>\\[\\]" + // brackets
                ":" +        // colon
                "," +        // comma
                "‒–—―" +     // dashes
                "…" +        // ellipsis
                "!" +        // exclamation mark
                "." +        // full stop/period
                "«»" +       // guillemets
                "-‐" +       // hyphen
                "?" +        // question mark
                "‘’“”" +     // quotation marks
                ";" +        // semicolon
                "/" +        // slash/stroke
                "⁄" +        // solidus
                "␠" +        // space?
                "·" +        // interpunct
                "&" +        // ampersand
                "@" +        // at sign
                "*" +        // asterisk
                "\\" +       // backslash
                "•" +        // bullet
                "^" +        // caret
                "¤¢$€£¥₩₪" + // currency
                "†‡" +       // dagger
                "°" +        // degree
                "¡" +        // inverted exclamation point
                "¿" +        // inverted question mark
                "¬" +        // negation
                "#" +        // number sign (hashtag)
                "№" +        // numero sign ()
                "%‰‱" +      // percent and related signs
                "¶" +        // pilcrow
                "′" +        // prime
                "§" +        // section sign
                "~" +        // tilde/swung dash
                "¨" +        // umlaut/diaeresis
                "_" +        // underscore/understrike
                "|¦" +       // vertical/pipe/broken bar
                "⁂" +        // asterism
                "☞" +        // index/fist
                "∴" +        // therefore sign
                "‽" +        // interrobang
                "※" +          // reference mark
                "]";


        HashedDictionary<String, LinkedList<TableEntry<String, String>>> hashtable;

        Scanner scanner = new Scanner(System.in);

        System.out.println("1-Linear Probing");
        System.out.println("2-Double Hashing");
        System.out.println("Which hash method you would like to use:");
        int typeOfHashing = scanner.nextInt();


        System.out.println("1-SSF");
        System.out.println("2-PAF");
        System.out.println("Which hash function you would like to use:");
        int hashFunctionType = scanner.nextInt();

        System.out.println("1-Load Factor %50");
        System.out.println("2-Load Factor %80");
        System.out.println("Which load factor you would like to use:");
        int loadFactorInput = scanner.nextInt();



        hashtable = new HashedDictionary<>(97, typeOfHashing, hashFunctionType,loadFactorInput); //creating hashtable



        File mainFolder = new File("src\\bbc"); //main folder
        File[] files = mainFolder.listFiles();
        long startOfIndexing = System.nanoTime(); //for measuring indexing time
        for (File eachFolder : files) {
            readAndAdd(eachFolder.getPath(), hashtable, DELIMITERS);
            System.out.println("The folder " + eachFolder.getName() + " was read and added");
        }
        long finishOfIndexing = System.nanoTime(); //for measuring indexing time

        System.out.println("Indexing time:" + (finishOfIndexing-startOfIndexing));
        System.out.println("Collision Number: " + hashtable.collisionCountFunc());


        System.out.println("1-Search a word");
        System.out.println("2-Search in txt");
        System.out.println("3-Displaying search time without outputs");
        System.out.println("What would you would like to do:");

        int optionInput = 0;

        try{
            optionInput = scanner.nextInt();
        }catch(InputMismatchException e){
            System.out.println("Invalid Selection");
            System.out.println(e.toString());
        }

        if(optionInput == 1){
            searchAWord(hashtable);
        }else if(optionInput == 2){
            searchAccordingToTxt("src\\search.txt",hashtable);
        }else if(optionInput == 3){
            searchAccordingToTxtNoOutput("src\\search.txt",hashtable);
        }

        scanner.nextLine();


    }


    //This function allows us to see how many words are in which txt files.
    public static void searchAWord(HashedDictionary<String, LinkedList<TableEntry<String, String>>> hashtable) {

        while (true) {

            Scanner scanner = new Scanner(System.in);

            System.out.print("Please enter the word you would like to search: ");

            String word = scanner.nextLine();

            if (Objects.equals(word, "q")) {
                break;
            }


            if (hashtable.contains(word)) {
                System.out.println(hashtable.search(word).size() + " documents found");
                for (int i = 0; i < hashtable.search(word).size(); i++) {
                    String[] splitedPathArray = (hashtable.search(word).get(i).getKey()).split("\\\\");
                    System.out.println(hashtable.search(word).get(i).getValue() + "-" + splitedPathArray[splitedPathArray.length - 2] + "\\" + splitedPathArray[splitedPathArray.length - 1]);
                }
            } else {
                System.out.println("Not Found!");
            }


        }
    }


    //This function is quite similar to the above. It searches the words in the txt file in the given path and shows us their values.
    public static void searchAccordingToTxt(String path, HashedDictionary<String, LinkedList<TableEntry<String, String>>> hashtable) throws Exception {
        File searchKeys = new File(path);
        String[] searchKeysArray = reader(searchKeys);

        for (String word : searchKeysArray) {

            System.out.println("Searched key now: " + word);

            if (Objects.equals(word, "q")) {
                break;
            }
            if (hashtable.contains(word)) {

                System.out.println(hashtable.search(word).size() + " documents found");
                for (int i = 0; i < hashtable.search(word).size(); i++) {
                    String[] splitedPathArray = (hashtable.search(word).get(i).getKey()).split("\\\\");
                    System.out.println(hashtable.search(word).get(i).getValue() + "-" + splitedPathArray[splitedPathArray.length - 2] + "\\" + splitedPathArray[splitedPathArray.length - 1]);
                }
            } else {
                System.out.println("Not Found!");
            }
            System.out.println();
        }

    }

    //This function was written to measure time performance.
    // While reading the search.txt above it was problematic to measure the time due to the outputs, so this function is used.
    public static void searchAccordingToTxtNoOutput(String path, HashedDictionary<String, LinkedList<TableEntry<String, String>>> hashtable) throws Exception {
        File searchKeys = new File(path);
        String[] searchKeysArray = reader(searchKeys);

        long minSearchTime = 1000000000;
        long maxSearchTime = 0;

        long startForAllWords =System.nanoTime();
        for (String word : searchKeysArray) {

            long startForEachWord =System.nanoTime();
            if (hashtable.contains(word)) {
                long finishForEachWord =System.nanoTime();
                long time = (finishForEachWord-startForEachWord);
                if(time<minSearchTime){
                    minSearchTime = time;
                }if(time>maxSearchTime){
                    maxSearchTime = time;
                }
            }
        }
        long finishForAllWords =System.nanoTime();
        long averageSearchTime = ((finishForAllWords-startForAllWords) / searchKeysArray.length);
        System.out.println("Minimum Search Time: " + minSearchTime);
        System.out.println("Maximum Search Time: " + maxSearchTime);
        System.out.println("Average Search Time: " + averageSearchTime);
    }


    //This method calls the reader methods, finds the word frequency in each txt and adds them to the hash table.
    // While doing this, it made use of the Dictionary and TableEntry classes.
    public static void readAndAdd(String path, HashedDictionary<String, LinkedList<TableEntry<String, String>>> hashtable, String DELIMITERS) throws Exception {

        File f = new File(path);
        File[] files = f.listFiles();

        File fileStopWords = new File("src\\stop_words_en.txt");

        String[] stopWordsArray = reader(fileStopWords); //calling reader function

        assert files != null;
        for (File eachTxt : files) {

            String name = eachTxt.getPath();

            String[] splitted = reader(eachTxt, DELIMITERS, stopWordsArray); //calling another reader function

            Dictionary<String, Integer> eachWord = new Dictionary<>(); //to keep number frequency for each word

            for (int i = 0; i < splitted.length; i++) {
                LinkedList<TableEntry<String, String>> llist = new LinkedList<>();
                if (!eachWord.contains(splitted[i])) { //if not added before
                    eachWord.put(splitted[i], 1); //first adding
                } else {
                    int newValue = eachWord.get(splitted[i]) + 1; //if exists before then increase the number
                    eachWord.put(splitted[i], newValue);
                }

                TableEntry<String, String> willBeAdded = new TableEntry<>(name, String.valueOf(eachWord.get(splitted[i])));
                //frequency of this word in old array is 1 less
                TableEntry<String, String> oldArray = new TableEntry<>(name, String.valueOf(eachWord.get(splitted[i]) - 1));

                if (hashtable.get(splitted[i]) == null) { //splitted[i] = current word , first adding to the hashtable
                    llist.add(willBeAdded); //firstly adding to linkedlist
                    hashtable.put(splitted[i], llist); //after that adding to the hashtable
                } else {//if exists before
                    if (containsArray(oldArray, willBeAdded)) { //if word is in same txt
                        hashtable.get(splitted[i]).getLast().setValue(willBeAdded.getValue());
                    } else {
                        hashtable.get(splitted[i]).add(willBeAdded); //if word exists but in other txt
                    }

                }

            }
        }
    }

    //This method allows us to read files according to delimiters and removes stopwords from the list.
    public static String[] reader(File eachTxt, String DELIMITERS, String[] stopWords) throws Exception {
        File file = new File(String.valueOf(eachTxt));

        Scanner scan = new Scanner(file);
        String fileContent = "";
        while (scan.hasNextLine()) {
            String nextLine = scan.nextLine();
            if (!Objects.equals(nextLine, "")) {
                fileContent = fileContent.concat(nextLine + "\n");
            }
        }

        String lowerCaseVersion = fileContent.toLowerCase(Locale.ENGLISH); //to make letters lowercase
        String[] splited = lowerCaseVersion.split(DELIMITERS); //splitting according to delimiters

        ArrayList<String> wordList = new ArrayList<>();
        wordList.addAll(Arrays.asList(splited));

        ArrayList<String> stopWordList = new ArrayList<>();
        stopWordList.addAll(Arrays.asList(stopWords));
        stopWordList.add("");
        wordList.removeAll(stopWordList); //process of removing stop words from the list

        String[] returnedList = wordList.toArray(new String[wordList.size()]);

        return returnedList;
    }


    //This method reads txt that containing stopwords.
    public static String[] reader(File stopWords) throws Exception {
        File file = new File(String.valueOf(stopWords));
        Scanner scan = new Scanner(file);
        String fileContent = "";
        while (scan.hasNextLine()) {
            String nextLine = scan.nextLine();
            if (!Objects.equals(nextLine, "")) {
                fileContent = fileContent.concat(nextLine + "\n");
            }
        }
        String lowerCaseVersion = fileContent.toLowerCase(Locale.ENGLISH); //to make letters lowercase
        String[] splited = lowerCaseVersion.split("\n");
        return splited;
    }


    //While this method counts the frequency of the same words in the same txt,
    //it makes comparisons by looking at the key values, that is, the path values here.
    public static boolean containsArray(TableEntry<String, String> t1, TableEntry<String, String> t2) {
        return (Objects.equals(t1.getKey(), t2.getKey()) && (!Objects.equals(t1.getValue(), "0")));
    }

}


//This class is used when keeping the txt name and frequency in the linkedlist
class TableEntry<S, T> {

    private S key;
    private T value;
    private HashedDictionary.TableEntry.States state; // Flags whether this entry is in the hash table

    enum States {CURRENT, REMOVED} // Possible values of state

    TableEntry(S searchKey, T dataValue) {
        key = searchKey;
        value = dataValue;
        state = HashedDictionary.TableEntry.States.CURRENT;
    } // end constructor

    public S getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }


    private boolean isIn() {
        return state == HashedDictionary.TableEntry.States.CURRENT;
    }


    public boolean isRemoved() {
        return state == HashedDictionary.TableEntry.States.REMOVED;
    }


    private void setToRemoved() {
        key = null;
        value = null;
        state = HashedDictionary.TableEntry.States.REMOVED;
    }


}





