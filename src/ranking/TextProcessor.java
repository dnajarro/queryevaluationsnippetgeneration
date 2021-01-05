package ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextProcessor {
    public List<TokenStats> processDocument(String filename, int docNum) {

        List<String> tokens = tokenizeDocument(filename);
        tokens = removeStopWords(tokens);
        tokens = stemTokens(tokens);
        Collections.sort(tokens);
        List<TokenStats> indices = extractStats(tokens, docNum);
        return indices;
    }

    public List<String> processQuery(String query) {
        List<String> tokens = tokenizeQuery(query);
        tokens = removeStopWords(tokens);
        tokens = stemTokens(tokens);
        return tokens;
    }

    private List<String> tokenizeDocument(String filename) {
        Tokenizer tokenizer = new Tokenizer();
        return tokenizer.tokenizeDocument(filename);
    }

    private List<String> tokenizeQuery(String query) {
        Tokenizer tokenizer = new Tokenizer();
        return tokenizer.tokenizeQuery(query);
    }

    private List<String> removeStopWords(List<String> tokens) {
        StopWords stopWords = new StopWords();
        List<String> validTokens = new ArrayList<>();
        for (String tok : tokens) {
            if (!stopWords.contains(tok)) {
                validTokens.add(tok);
            }
        }
        return validTokens;
    }

    private List<String> stemTokens(List<String> tokens) {
        PorterStemmer porterStemmer = new PorterStemmer();
        List<String> stems = new ArrayList<>();
        for (String tok : tokens) {
            String stem = porterStemmer.stem(tok.trim());
            if (!stem.equals("Invalid term") && !stem.equals("No term entered")) {
                stems.add(stem);
            }
        }
        return stems;
    }

    private List<TokenStats> extractStats(List<String> tokens, int docNum) {
        List<TokenStats> indices = new ArrayList<>();
        String curToken = "";
        int count = 0;
        for (String tok : tokens) {
            if (curToken.equals("")) {
                curToken = tok;
                count++;
            } else {
                if (curToken.equals(tok)) {
                    count++;
                } else {
                    indices.add(new TokenStats(curToken, docNum, count));
                    curToken = tok;
                    count = 1;
                }
            }
        }
        if (!curToken.equals("")) {
            indices.add(new TokenStats(curToken, docNum, count));
        }

        return indices;
    }
}
