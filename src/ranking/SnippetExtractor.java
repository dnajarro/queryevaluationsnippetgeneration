package ranking;

import shared.ResultSentence;
import shared.SnippetSentence;

import java.io.*;
import java.util.*;

public class SnippetExtractor {
    private StopWords stopWords;

    public SnippetExtractor() {
        stopWords = new StopWords();
    }

    public List<SnippetSentence> extractSnippet(int docID, List<String> query) {
        List<DocSentence> sentences = new ArrayList<>();
        String snippet = "";
        List<SnippetSentence> resultSentences = new ArrayList<>();
        try {
            File file = new File("data/Doc (" + docID + ").txt");
            Scanner scanner = new Scanner(file);
            int totalLines = 0;
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                totalLines++;
            }

            sentences = extractSentences(file, totalLines);
            for (DocSentence s : sentences) {
                int totalQueryTermsScore = countTotalQueryTerms(s.getSentence(), query);
                int uniqueQueryTermsScore = countUniqueQueryTerms(s.getSentence(), query);
                int contiguousScore = countLongestContiguousQueryWords(s.getSentence(), query);
                float densityMeasure = calcSignificanceFactor(s.getSentence(), sentences);
                float locationScore = calcSentenceLocation(s.getSentenceNum(), sentences.size());
                int quotationScore = calcQuotationRating(s.getSentence());
                int contiguousNumScore = countTotalContiguous(s.getSentence(), query);

                int headingScore = 0;
                int atStartScore = 0;
                if (isHeading(s.getSentenceNum())) {
                    headingScore = 1;
                }
                if (isAtStartOfDoc(s.getLineNum())) {
                    atStartScore = 1;
                }
                float score = totalQueryTermsScore + uniqueQueryTermsScore + contiguousScore + densityMeasure +
                        headingScore + atStartScore + locationScore + quotationScore + contiguousNumScore;
                resultSentences.add(new SnippetSentence(score, s.getSentence(), getQueryTermsArray(s.getSentence(), query)));
            }
        } catch (IOException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }

        Collections.sort(resultSentences);
        List<SnippetSentence> bestSnippets = new ArrayList<>();
        int i = 0;
        while (i < 2 && i < resultSentences.size()) {
            bestSnippets.add(resultSentences.get(i));
            i++;
        }
        return bestSnippets;
    }

    private float calcSentenceLocation(int curSentenceNum, int totalSentences) {
        return (1.0f - (curSentenceNum * 1.0f / totalSentences)) * (totalSentences * 1.0f / curSentenceNum);
    }

    private int calcQuotationRating(String sentence) {
        if (sentence.contains("'")) {
            int first = sentence.indexOf("'");
            int second = sentence.indexOf("'", first + 1);
            if (second != -1) {
                return 0;
            }
            return 1;
        } else if (sentence.contains("\"")) {
            int first = sentence.indexOf("\"");
            int second = sentence.indexOf("\"", first + 1);
            if (second != -1) {
                return 0;
            }
            return 1;
        }
        return 1;
    }

    public int countTotalContiguous(String sentence, List<String> query) {
        Set<String> queryTerms = new HashSet<>();
        String[] terms = sentenceToLowerCase(sentence);
        for (int x = 0; x < query.size(); x++) {
            queryTerms.add(toLowerCase(query.get(x)));
        }
        queryTerms.addAll(query);
        int count = 0;
        int totalContiguous = 0;
        int i;
        int j = 0;
        while (j < terms.length) {
            i = j;
            boolean firstSigFound = false;
            boolean isRunEnded = false;
            while (i < terms.length && !isRunEnded) {
                if (queryTerms.contains(terms[i])) {
                    firstSigFound = true;
                } else {
                    if (firstSigFound) {
                        isRunEnded = true;
                        count++;
                        j = i + 1;
                    }
                }
                i++;
            }
            j++;
        }
        return totalContiguous;
    }

    public List<DocSentence> extractSentences(File file, int totalLines) throws IOException {
        List<DocSentence> sentences = new ArrayList<>();
        int curLine = 0;
        Scanner scanner = new Scanner(file);
        StringBuilder sb = new StringBuilder();
        int sentenceNum = 0;
        while (scanner.hasNextLine()) {
            curLine++;
            String line = scanner.nextLine();
            boolean allSentencesOnLineFound = false;
            boolean isSentenceEnded = false;
            int endIndex;
            int startIndex = 0;
            while (!allSentencesOnLineFound) {
                endIndex = findEndPunctuation(line, startIndex);
                if (endIndex != -1) {
                    if (!line.substring(startIndex, endIndex).isEmpty()) {
                        sb.append(line, startIndex, endIndex);
                        sentenceNum++;
                        sentences.add(new DocSentence(sb.toString(), curLine, sentenceNum));
                        sb.setLength(0);
                        startIndex = endIndex + 1;
                        if (startIndex > line.length()) {
                            allSentencesOnLineFound = true;
                        }
                    } else {
                        allSentencesOnLineFound = true;
                    }
                } else {
                    if (!line.isEmpty()) {
                        sb.append(line, startIndex, line.length());
                    }
                    allSentencesOnLineFound = true;
                }
            }
        }
        scanner.close();
        return sentences;
    }

    private String removeStartEndQuotations(String word) {
        StringBuilder sb = new StringBuilder();
        if (word != null) {
            if (!word.isEmpty()) {
                for (int i = 0; i < word.length(); i++) {
                    if (i == 0 || i == word.length() - 1) {
                        char c = word.charAt(i);
                        if (c != '\'' && c != '\"' && c != '(' && c != ')' && c != '{' && c != '}'
                                && c != '[' && c != ']' && c != '-') {
                            sb.append(c);
                        }
                    } else {
                        sb.append(word.charAt(i));
                    }
                }
            }
        }
        return sb.toString();
    }

    private String removeEndingPunctuation(String word) {
        StringBuilder sb = new StringBuilder();
        if (word != null) {
            if (!word.isEmpty()) {
                for (int i = 0; i < word.length(); i++) {
                    if (i == word.length() - 1) {
                        char c = word.charAt(i);
                        if (c != '.' && c != '!' && c != '?' && c != ',' && c != ';' && c != ':') {
                            sb.append(c);
                        }
                    } else {
                        sb.append(word.charAt(i));
                    }
                }
            }
        }
        return sb.toString();
    }

    private int findEndPunctuation(String s, int startIndex) {
        if (startIndex < s.length()) {
            if (s.contains(".") || s.contains("!") || s.contains("?")) {
                int period = s.indexOf(".", startIndex);
                if (period == -1) {
                    period = Integer.MAX_VALUE;
                } else if (s.indexOf(".'", startIndex) == s.indexOf(".", startIndex)
                        || (s.indexOf(".\"", startIndex) == s.indexOf(".", startIndex))) {
                    period += 1;
                } else if (s.indexOf("p.m.") == s.indexOf(".") - 1 || s.indexOf("a.m.") == s.indexOf(".") - 1) {
                    period += 2;
                }
                int exclamation = s.indexOf("!", startIndex);
                if (exclamation == -1) {
                    exclamation = Integer.MAX_VALUE;
                } else if (s.indexOf("!'", startIndex) == s.indexOf("!", startIndex)
                        || (s.indexOf(".\"", startIndex) == s.indexOf("!", startIndex))) {
                    exclamation += 1;
                }
                int question = s.indexOf("?", startIndex);
                if (question == -1) {
                    question = Integer.MAX_VALUE;
                } else if (s.indexOf("?'", startIndex) == s.indexOf("?", startIndex)
                        || s.indexOf("?\"", startIndex) == s.indexOf("?", startIndex)) {
                    question += 1;
                }
                int index = Math.min(Math.min(period, exclamation), question);
                if (index == Integer.MAX_VALUE)
                    return -1;
                return index + 1;
            }
        }
        return -1;
    }


    // calculate f-sub-d,w, the frequency of word w in document d
    public float calcLowerBoundFreq(String word, int numOfSentences) {
        float significanceFactor = 0.0f;
        if (!stopWords.contains(word)) {
            significanceFactor = 7.0f - 0.1f * (25 - numOfSentences);
            if (numOfSentences >= 25 && numOfSentences <= 40) {
                significanceFactor = 7;
            }
            if (numOfSentences < 25) {
                significanceFactor = 7.0f - 0.1f * (25 - numOfSentences);
            }
        }

        return significanceFactor;
    }

    // assuming sentence has punctuation removed
    public List<Integer> identifySignificantWords(String[] sentence, List<DocSentence> sentences) {
        List<Integer> significantWords = new ArrayList<>();
        for (int i = 0; i < sentence.length; i++) {
            if (!stopWords.contains(sentence[i])) {
                if (calcFreq(sentence[i], sentences) >= calcLowerBoundFreq(sentence[i], sentences.size())) {
                    significantWords.add(i);
                }
            }
        }
        return significantWords;
    }

    public float calcFreq(String word, List<DocSentence> sentences) {
        int count = 0;
        if (!word.isEmpty()) {
            for (int i = 0; i < sentences.size(); i++) {
                String sentence = sentences.get(i).getSentence();
                int j = 0;
                while (j != -1) {
                    if (sentence.indexOf(word, j) != -1) {
                        count++;
                        j = sentence.indexOf(word, j) + word.length();
                    } else {
                        j = sentence.indexOf(word, j);
                    }
                }
            }
        }
        return count;
    }

    public Span findMaxSpan(String[] sentence, List<Integer> significantWords) {
        // take smallest and biggest index from list of integers
        // if there are <= 4 non-significant words between them, return sentence from smallest index to biggest index
        // else choose between second or second-to-last index, whichever has the smallest difference from corresponding smallest/biggest index
        // count non-significant words again
        // repeat while smallest < biggest and non-significant words > 4
        boolean maxSpanFound = false;
        StringBuilder sb = new StringBuilder();
        int startIndex = 0;
        int endIndex = significantWords.size() - 1;
        for (int i = 0; i < sentence.length; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(sentence[i]);
        }
        Span span = new Span(sb.toString(), startIndex, endIndex, significantWords);

        while (!maxSpanFound && startIndex < endIndex) {
            int smallestPos = significantWords.get(startIndex);
            int biggestPos = significantWords.get(endIndex);
            int count = 0;
            for (int i = smallestPos; i < biggestPos + 1; i++) {
                if (!significantWords.contains(i)) {
                    count++;
                }
            }

            if (count <= 4) {
                sb.setLength(0);
                for (int j = smallestPos; j < biggestPos + 1; j++) {
                    if (j != smallestPos) {
                        sb.append(" ");
                    }
                    sb.append(sentence[j]);
                }
                span.setStartIndex(smallestPos);
                span.setEndIndex(biggestPos);
                span.setSpan(sb.toString());
                span.setSignificantWords(significantWords);
                maxSpanFound = true;
            }

            int startDiff = significantWords.get(startIndex + 1) - significantWords.get(startIndex);
            int endDiff = significantWords.get(endIndex) - significantWords.get(endIndex - 1);
            if (startDiff < endDiff) {
                startIndex += 1;
            } if (endDiff <= startDiff) {
                endIndex -= 1;
            }
        }

        return span;
    }

    // find maximum span of words, bracketed by significant words, with at most 4 non-significant words between them
    public float calcSignificanceFactor(String sentence, List<DocSentence> sentences) {
        String[] terms = sentenceToLowerCase(sentence);
        List<Integer> significantWordsPos = identifySignificantWords(terms, sentences);
        Span maxSpan = findMaxSpan(terms, significantWordsPos);
        return (maxSpan.getNumOfSignificantWords() * 1.0f * maxSpan.getNumOfSignificantWords()) / maxSpan.getLength();
    }

    private String toLowerCase(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(Character.toLowerCase(s.charAt(i)));
        }
        return sb.toString();
    }

    private String[] sentenceToLowerCase(String sentence) {
        String[] terms = sentence.split(" ");
        for (int i = 0; i < terms.length; i++) {
            terms[i] = toLowerCase(terms[i]);
        }
        return terms;
    }

    public int countLongestContiguousQueryWords(String sentence, List<String> query) {
        Set<String> queryTerms = new HashSet<>();
        String[] terms = sentenceToLowerCase(sentence);
        for (int x = 0; x < query.size(); x++) {
            queryTerms.add(toLowerCase(query.get(x)));
        }
        queryTerms.addAll(query);
        int count = 0;
        int longestRun = 0;
        int i;
        for (int j = 0; j < terms.length; j++) {
            i = j;
            count = 0;
            boolean firstSigFound = false;
            boolean isRunEnded = false;
            while (i < terms.length && !isRunEnded) {
                if (queryTerms.contains(terms[i])) {
                    count++;
                    firstSigFound = true;
                } else {
                    if (firstSigFound) {
                        isRunEnded = true;
                        if (count > longestRun) {
                            longestRun = count;
                        }
                    }
                }
                i++;
            }
        }
        return longestRun;
    }

    public int countUniqueQueryTerms(String sentence, List<String> query) {
        Set<String> queryTerms = new HashSet<>();
        String[] terms = sentenceToLowerCase(sentence);
        for (int x = 0; x < query.size(); x++) {
            queryTerms.add(toLowerCase(query.get(x)));
        }
        int startSize = queryTerms.size();

        for (int i = 0; i < terms.length; i++) {
            if (queryTerms.contains(terms[i])) {
                queryTerms.remove(terms[i]);
            }
        }
        return startSize - queryTerms.size();
    }

    public int countTotalQueryTerms(String sentence, List<String> query) {
        String[] words = sentence.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = toLowerCase(removeStartEndQuotations(removeEndingPunctuation(words[i])));
        }
        int count = 0;
        for (int i = 0; i < words.length; i++) {
            if (query.contains(words[i])) {
                count++;
            }
        }
        return count;
    }

    public boolean isAtStartOfDoc(int lineNum) {
        return lineNum > 0 && lineNum < 3;
    }

    public boolean isHeading(int sentenceNum) {
        return sentenceNum == 1;
    }

    public int[] getQueryTermsArray(String sentence, List<String> query) {
        List<Integer> qTerms = new ArrayList<>();
        String[] terms = sentence.split(" ");
        for (int i = 0; i < terms.length; i++) {
            if (query.contains(toLowerCase(terms[i]))) {
                qTerms.add(i);
            }
        }
        int[] termIndexes = new int[qTerms.size()];
        for (int j = 0; j < termIndexes.length; j++) {
            termIndexes[j] = qTerms.get(j);
        }

        return termIndexes;
    }
}
