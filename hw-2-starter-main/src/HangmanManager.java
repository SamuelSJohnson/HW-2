import java.util.*;

/**
 *  Primary class for "Evil Hangman"
 * Contains the primary logic for a game of hangman that cheats by always leaving the player with the widest possible
 * set of answers when the guess a letter.
 * @Author Christopher Nugent, Mikyung Han for EGR221 Solutions
 * @Version Jan 2017
 */

/**
 * worked on with Nathan, Jacob, and Adam
 */
public class HangmanManager {

    private Set<String> answerSpace;
    private SortedSet<Character> guessedCharacters;
    private int guessesRemaining;
    private String pattern;

    /*
     * Constructor
     * Initializes the HangmanManager from dictionary with words of length length. Allows a maximum of max guesses.
     * Throws IllegalArgument exceptions if length is less than one or if max is less than zero.
     * Parameters: dictionary of words to use, length of words to select, number of allowed incorrect guesses
     */
    public HangmanManager(Collection<String> dictionary, int length, int max) {
        if(length < 1) {
            throw new IllegalArgumentException("Length must be 1 or more.");
        } else if (max < 0) {
            throw new IllegalArgumentException("Maximum incorrect guesses must be more than 0.");
        }

        answerSpace = new TreeSet<>();
        for(String word:dictionary) {
            if (word.length() == length) {
                answerSpace.add(word);
            }
        }

        guessedCharacters = new TreeSet<>();
        guessesRemaining = max;

        pattern = "-";
        for (int i = 1; i < length; i++) {
            pattern += " -";
        }
    }

    public Set<String> words() {
        return new TreeSet<>(answerSpace);
    }
    public int guessesLeft() {
        return guessesRemaining;
    }
    public SortedSet<Character> guesses() {
        return new TreeSet<>(guessedCharacters);
    }

    public String pattern() {
        if (answerSpace.isEmpty())
            throw new IllegalStateException("answer space should not be empty");
        return pattern;
    }

    /*
     * Allows the user to guess a letter of the word and updates the state of HangmanManager accordingly.
     * If there are no possible answers remaining, throws an IllegalStateException (no words were found).
     * If there are not enough guessedCharacters remaining, throws an IllegalStateException.
     * If the character has already been guessed, throws an IllegalArgumentException.
     * Parameter: Letter being guessed
     * Returns: Number of occurrences  of that letter in the pattern
     */
    public int record(char guess) {
        if(answerSpace.size() == 0) {
            throw new IllegalStateException("The set of possible answers is empty.");
        } else if (guessesLeft() < 1) {
            throw new IllegalStateException("No guesses remaining.");
        } else if (guessedCharacters.contains(guess)) {
            throw new IllegalArgumentException(guess + " has already been guessed.");
        }

        guessedCharacters.add(guess);

        Map<String, Set<String>> answerMap = buildMap(guess);
        int numOccur = updateAnswerInfo(answerMap, guess);

        if (numOccur == 0) guessesRemaining--;
        return numOccur;
    }

    /**
     * Helper method for buildMap
     * This method builds the new pattern of the word given the word and the guess and the current pattern
     * The pattern will be the key for the map
     * (e.g., say the current pattern is "_ _ _ l _"
     * @param word (e.g. "apple")
     * @param guess (e.g. "a")
     * @return returns the pattern of the word (e.g. "a _ _ _ l _") based on current pattern and guess
     */
    private String buildKey(String word, char guess) {
        StringBuilder sb = new StringBuilder(pattern);
        for(int i = 0; i < word.length(); i++) {
            if(word.charAt(i) == guess) {
                sb.setCharAt(i * 2, guess);
            }
        }
        return sb.toString();
    }

    /*
     * Helper method for record()
     * Generates a map of index combinations to sets of word that match those index combinations.
     * Parameters: character being guessed
     * Returns: map of index combinations to word sets
     */
    private Map<String, Set<String>> buildMap(char guess) {
        Map<String, Set<String>> answerMap = new TreeMap<>();
        for(String word:answerSpace) {
            String key = buildKey(word, guess);
            Set<String> value;
            if(!answerMap.containsKey(key)) {
                value = new TreeSet<>();
            } else {
                value = answerMap.get(key);
            }
            value.add(word);
            answerMap.put(key, value);
        }
        return answerMap;
    }

    /*
     * Helper method for record()
     * Parameters: map to search, char for the current guess
     * post: updates answerSpace, pattern to show to user,
     * @return the number of occurrences of guess in the new pattern
     */
    private int updateAnswerInfo(Map<String, Set<String>> answerMap, char guess) {
        String bestKey = findBestKey(answerMap);
        answerSpace = answerMap.get(bestKey);
        int numOccurrences = calcDiff(pattern, bestKey);
        pattern = bestKey;
        return numOccurrences;
    }

    /**
     * Static helper method for calculating the number of different characters
     * @param str1
     * @param str2
     * @return returns the number of different characters in str1 and str2
     */
    private static int calcDiff(String str1, String str2){
        int numDiff = 0;
        for(int i = 0; i < str1.length(); i++){
            if(str1.charAt(i) != str2.charAt(i))
                numDiff++;
        }
        return numDiff;
    }

    /*
     * Helper method for updateAnswerInfo()
     * Returns the key of the largest Set in a map
     * Parameters: map to search
     * Returns: key which refers to that set
     */
    private String findBestKey(Map<String, Set<String>> answerMap){
        Set<String> keySet = answerMap.keySet();
        String bestKey = keySet.iterator().next();
        for(String key:keySet) {
            if(answerMap.get(key).size() > answerMap.get(bestKey).size()) {
                bestKey = key;
            }
        }
        return bestKey;
    }
}