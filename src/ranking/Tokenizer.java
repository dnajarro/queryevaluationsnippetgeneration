package ranking;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Tokenizer {

    private Set<String> dictionary;
    public Tokenizer() {
        dictionary = new HashSet<>();
        try {
            File file = new File("data/dictionary.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String word = scanner.next();
                dictionary.add(word);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
    }

    public List<String> tokenizeDocument(String filename) {
        List<String> docTokens = new ArrayList<>();
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String word = scanner.next();
                String token = tokenizeWord(word);
                if (!token.isEmpty()) {
                    docTokens.add(token);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }


        return docTokens;
    }

    public List<String> tokenizeQuery(String query) {
        List<String> queryTokens = new ArrayList<>();
        Scanner scanner = new Scanner(query);
        while (scanner.hasNext()) {
            String word = scanner.next();
            String token = tokenizeWord(word);
            if (!token.isEmpty()) {
                queryTokens.add(token);
            }
        }
        scanner.close();

        return queryTokens;
    }


    private String tokenizeWord(String word) {
        word = word.toLowerCase();
        String[] subwords = word.replaceAll("\\p{Punct}", " ").split("\\s");
        boolean canConcat = false;
        List<String> validSubTokens = new ArrayList<>();

        String resultToken;
        if (subwords.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < subwords.length; i++) {
                sb.append(subwords[i]);
            }

            resultToken = sb.toString();

            if (!dictionary.contains(resultToken)) {
                return "";
            }
        } else if (subwords.length == 1) {
            resultToken = subwords[0];
        } else {
            return "";
        }

        return resultToken;
    }

    private boolean isInDictionary(String word) {
        if (!dictionary.contains(word)) {
            return false;
        }
        return true;
    }

}
