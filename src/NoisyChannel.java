import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NoisyChannel {
    private final int NUMOFDOCS = 322;
    public List<String> calcProbability(List<Corrections> possibleCorrections) {
        Map<String, List<List<String>>> logEntries = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/query_log.txt"));
            String line = "";
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                if (lineNum != 1) {
                    if (line != null) {
                        String[] lineWords = line.split("[ \t]");
                        String id = lineWords[0];
                        List<String> query = new ArrayList<>();
                        for (int i = 1; i < lineWords.length; i++) {
                            query.add(lineWords[i]);
                        }
                        if (logEntries.containsKey(id)) {
                            List<List<String>> queries = logEntries.get(id);
                            queries.add(query);
                            logEntries.put(id, queries);
                        } else {
                            List<List<String>> queries = new ArrayList<>();
                            queries.add(query);
                            logEntries.put(id, queries);
                        }
                    }
                }
                lineNum++;
            }
        } catch (IOException e) {
            System.out.println("Could not read file");
        }

        Map<String, List<ProbOfError>> queryErrorProbs = calcProbOfError(possibleCorrections, logEntries);

        List<String> queryWords = new ArrayList<>();
        for (int i = 0; i < possibleCorrections.size(); i++) {
            queryWords.add(possibleCorrections.get(i).getOriginalWord());
        }

        List<String> allReplacementWords = new ArrayList<>();
        for (Corrections c : possibleCorrections) {
            allReplacementWords.addAll(c.getCorrections());
        }

        Map<String, Float> proportionsOfOccurrences = calcProportionOfOccurrence(allReplacementWords);

        List<String> bestWords = new ArrayList<>();
        for (String word : queryWords) {
            List<ProbOfError> possReplacements = queryErrorProbs.get(word);
            float maxProbability = Float.MIN_NORMAL;
            String correctWord = word;
            for (ProbOfError replacementWord : possReplacements) {
                if (!toLowerCase(replacementWord.getCorrectWord()).equals(word)) {
                    float probability = replacementWord.getProbability() * proportionsOfOccurrences.get(replacementWord.getCorrectWord());
                    if (probability > maxProbability) {
                        maxProbability = probability;
                        correctWord = replacementWord.getCorrectWord();
                    }
                } else {
                    maxProbability = 1.0f;
                    correctWord = word;
                }
            }
            correctWord = maintainCapitalization(word, correctWord);
            bestWords.add(correctWord);
        }
        return bestWords;
    }

    public String maintainCapitalization(String word, String correctWord) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < correctWord.length(); i++) {
            if (i < word.length()) {
                if (Character.isUpperCase(word.charAt(i))) {
                    char c = correctWord.charAt(i);
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(correctWord.charAt(i));
                }
            } else {
                sb.append(correctWord.charAt(i));
            }
        }
        return sb.toString();
    }

    private String toLowerCase(String word) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            sb.append(word.charAt(i));
        }
        return sb.toString();
    }

    public Map<String, List<ProbOfError>> calcProbOfError(List<Corrections> possibleCorrections,
                                                          Map<String, List<List<String>>> logEntries) {
        Map<String, List<ProbOfError>> querySpellingCorrections = new HashMap<>();
        for (Corrections corrections : possibleCorrections) {
            List<ProbOfError> probOfErrors = new ArrayList<>();
            for (String correctWord : corrections.getCorrections()) {
                List<PossRespelling> relevantSessions = new ArrayList<>();
                for (Map.Entry<String, List<List<String>>> entry : logEntries.entrySet()) {
                    List<List<String>> queries = entry.getValue();
                    boolean sessionAdded = false;
                    if (queries.size() > 1) {
                        for (int i = 0; i < queries.size(); i++) {
                            for (int j = 0; j < queries.get(i).size(); j++) {
                                if (correctWord.equals(queries.get(i).get(j).toLowerCase())) {
                                    relevantSessions.add(new PossRespelling(j, entry.getKey()));
                                    sessionAdded = true;
                                }
                                if (sessionAdded) {
                                    break;
                                }
                            }
                            if (sessionAdded) {
                                break;
                            }
                        }
                    }
                }

                int eCount = 0;
                int totCount = 0;
                for (PossRespelling p : relevantSessions) {
//                    boolean misspellingFound = false;
                    boolean replacementFound = false;
                    List<List<String>> sessionQueries = logEntries.get(p.getSessionId());
                    for (List<String> query : sessionQueries) {
                        if (!correctWord.equals(query.get(p.getPosition()))) {
                            if (query.get(p.getPosition()).equals(corrections.getOriginalWord().toLowerCase())) {
                                eCount++;
//                                misspellingFound = true;
                            }
                            replacementFound = true;
                        }
                    }
                    if (replacementFound) {
                        totCount++;
                    }
                }

                if (totCount > 0) {
                    ProbOfError prob = new ProbOfError(corrections.getOriginalWord(), correctWord,
                            (float) (eCount * 1.0 / totCount));
                    probOfErrors.add(prob);
                } else {
                    probOfErrors.add(new ProbOfError(corrections.getOriginalWord(), correctWord, 0.0f));
                }
            }
            querySpellingCorrections.put(corrections.getOriginalWord(), probOfErrors);
        }

        return querySpellingCorrections;
    }

    public Map<String, Float> calcProportionOfOccurrence(List<String> queryWords) {
        Map<String, Float> proportionsOfOccurrence = new HashMap<>();
        StopWords stopWords = new StopWords();
        for (int i = 0; i < queryWords.size(); i++) {
            int totalCount = 0;
            int wordCount = 0;
            for (int j = 0; j < NUMOFDOCS; j++) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader("data/Doc (" + (j + 1) + ").txt"));
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        if (line != null) {

                            String[] docWords = line.split(" ");
                            for (int k = 0; k < docWords.length; k++) {
                                if (!stopWords.contains(docWords[k])) {
                                    for (int l = 0; l < docWords[k].length(); l++) {
                                        if (Character.isDigit(docWords[k].charAt(l))) {
                                            break;
                                        }
                                        if (l == docWords[k].length() - 1) {
                                            totalCount++;
                                        }
                                    }
                                    if (docWords[k].toLowerCase().equals(queryWords.get(i).toLowerCase())) {
                                        wordCount++;
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Could not read file");
                }
            }
            proportionsOfOccurrence.put(queryWords.get(i), (float)(wordCount * 1.0 / totalCount));
        }
        return proportionsOfOccurrence;
    }
}
