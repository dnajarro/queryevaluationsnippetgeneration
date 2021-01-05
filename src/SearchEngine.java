import ranking.*;
import shared.ResultSentence;
import java.util.*;

public class SearchEngine {
    public SearchEngine() {}

    public List<ResultSentence> search(String query) {
        long start = System.currentTimeMillis();
        List<ResultSentence> resultSentences = new ArrayList<>();

        String[] queryTerms = query.split(" ");
        List<String> queryWords = new ArrayList<>();
        for (int i = 0; i < queryTerms.length; i++) {
            queryWords.add(queryTerms[i]);
        }

        SpellCorrector spellCorrector = new SpellCorrector("data/dictionary.txt");
        List<Corrections> corrections = spellCorrector.correct(queryWords);
        NoisyChannel noisyChannel = new NoisyChannel();
        List<String> spellCorrectedQuery = noisyChannel.calcProbability(corrections);
        corrections = removeUnneededCorrections(corrections, spellCorrectedQuery);

        resultSentences.addAll(createQueryCorrection(query, corrections, spellCorrectedQuery));

        TextProcessor textProcessor = new TextProcessor();
        int totalDocs = 322;
        Map<String, List<StatTuple>> indexTable = new HashMap<>();
        List<TokenStats> documentTable = new ArrayList<>();

        for (int i = 1; i < totalDocs + 1; i++) {
            String filename = "data/Doc (" + i + ").txt";
            documentTable = textProcessor.processDocument(filename, i);
            for (TokenStats tokenStats : documentTable) {
                if (!indexTable.containsKey(tokenStats.getToken())) {
                    indexTable.put(tokenStats.getToken(), tokenStats.getStatTuples());
                } else {
                    indexTable.get(tokenStats.getToken()).addAll(tokenStats.getStatTuples());
                }
            }
        }

        int queryNum = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spellCorrectedQuery.size(); i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(spellCorrectedQuery.get(i));
        }
        List<String> queries = new ArrayList<>();
        queries.add(sb.toString());
        for (String q : queries) {
            queryNum++;
            List<String> keywords = textProcessor.processQuery(q);
            Ranker ranker = new Ranker();
            List<RankScore> rankScores = ranker.rankQuery(indexTable, totalDocs, keywords);
            int numOfTopRankedDocs = 5;
            SnippetExtractor snippetExtractor = new SnippetExtractor();
            for (int i = 0; i < numOfTopRankedDocs; i++) {
                RankScore rankScore = rankScores.get(i);
                double score = rankScore.getScore();
                int docID = rankScore.getDocNum();
                int[] docIndexes = new int[2];
                docIndexes[0] = 0;
                docIndexes[1] = 1;
                resultSentences.add(new ResultSentence("Doc " + docID + ": ", docIndexes));
                resultSentences.addAll(snippetExtractor.extractSnippet(docID, spellCorrectedQuery));
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("runtime: " + (end - start) / 1000.0 + " s");
        return resultSentences;
    }

    private String toLowerCase(String word) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            sb.append(Character.toLowerCase(word.charAt(i)));
        }
        return sb.toString();
    }

    private List<Corrections> removeUnneededCorrections(List<Corrections> corrections, List<String> spellCorrectedQuery) {
        List<Corrections> neededCorrections = new ArrayList<>();
        for (int i = 0; i < corrections.size(); i++) {
            if (!toLowerCase(corrections.get(i).getOriginalWord()).equals(toLowerCase(spellCorrectedQuery.get(i)))) {
                neededCorrections.add(corrections.get(i));
            }
        }
        return neededCorrections;
    }

    private List<ResultSentence> createQueryCorrection(String query, List<Corrections> corrections, List<String> correctedQuery) {
        List<ResultSentence> result = new ArrayList<>();
        String originalQuery = "Original query: " + query + "\t";
        int[] originalQueryBoldIndexes = new int[2];
        originalQueryBoldIndexes[0] = 0;
        originalQueryBoldIndexes[1] = 1;
        result.add(new ResultSentence(originalQuery, originalQueryBoldIndexes));
        StringBuilder sb = new StringBuilder();
        sb.append("Corrected query: ");
        for (int i = 0; i < correctedQuery.size(); i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(correctedQuery.get(i));
        }
        int[] correctedQueryBoldIndexes = new int[2];
        correctedQueryBoldIndexes[0] = 0;
        correctedQueryBoldIndexes[1] = 1;
        result.add(new ResultSentence(sb.toString(), correctedQueryBoldIndexes));
        sb.setLength(0);
        sb.append("Soundex codes: ");
        for (int i = 0; i < corrections.size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            if (!corrections.get(i).getOriginalWord().equals(correctedQuery.get(i))) {
                sb.append(corrections.get(i).getSoundexCode());
            }
        }
        int[] soundexIndexes = new int[2 + corrections.size()];
        for (int i = 0; i < soundexIndexes.length; i++) {
            soundexIndexes[i] = i;
        }
        result.add(new ResultSentence(sb.toString(), soundexIndexes));
        sb.setLength(0);
        sb.append("Suggested corrections: ");
        for (int i = 0; i < corrections.size(); i++) {
            List<String> correctWords = corrections.get(i).getCorrections();
            for (int j = 0; j < correctWords.size(); j++) {
                if (j != 0) {
                    sb.append(", ");
                }

                sb.append(correctWords.get(j));
            }
        }
        int[] sugCorrectionsIndexes = new int[2 + corrections.size()];
        sugCorrectionsIndexes[0] = 0;
        sugCorrectionsIndexes[1] = 1;
        result.add(new ResultSentence(sb.toString(), sugCorrectionsIndexes));
        return result;
    }
}
