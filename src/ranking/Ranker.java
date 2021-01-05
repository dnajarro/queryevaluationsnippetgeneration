package ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Ranker {

    public List<RankScore> rankQuery(Map<String, List<StatTuple>> indexTable, int collectionSize, List<String> query) {
        List<RankScore> ranking = new ArrayList<>();
        for (int i = 1; i < collectionSize + 1; i++) {
            double score = rankDoc(indexTable, collectionSize, query, i);
            RankScore rankScore = new RankScore(score, i);
            ranking.add(rankScore);
        }

        Collections.sort(ranking);
        Collections.reverse(ranking);

        return ranking;
    }

    private double rankDoc(Map<String, List<StatTuple>> indexTable, int collectionSize, List<String> query, int docNum) {
        // the sum of TF(w, d) x IDF(w) for all w in q
        double rank = 0;

        for (String keyword : query) {
            List<StatTuple> docAppearances = indexTable.get(keyword);
            if (docAppearances != null) {
                double idf = calcIDF(keyword, collectionSize, indexTable);
                for (StatTuple stat : docAppearances) {
                    if (stat.getDocNum() == docNum) {
                        double tf = calcTF(docNum, stat.getDocFreq(), indexTable);
                        rank += tf * idf;
                        break;
                    }
                }
            }
        }

        return rank;
    }

    private int getMostFrequentTokenByDoc(int docNum, Map<String, List<StatTuple>> indexTable) {
        int highestFrequency = 0;
        for (Map.Entry<String, List<StatTuple>> entry : indexTable.entrySet()) {
            List<StatTuple> statTuples = entry.getValue();
            for (StatTuple statTuple : statTuples) {
                if (statTuple.getDocNum() == docNum) {
                    if (statTuple.getDocFreq() > highestFrequency) {
                        highestFrequency = statTuple.getDocFreq();
                    }
                }
            }
        }

        return highestFrequency;
    }

    // divide the frequency of a token w in a document d by the frequency of the most frequent token in d
    // TF(w, d) = freq(w, d) / (max-l(freq(l, d)))
    private double calcTF(int docNum, int tokenFreq, Map<String, List<StatTuple>> indexTable) {
        int highestFreq = getMostFrequentTokenByDoc(docNum, indexTable);
        return (double)tokenFreq / highestFreq;
    }

    // IDF(w) = log2(N/n-w)
    // log base 2 of the (number of documents in the collection / number of documents in which w appears)
    private double calcIDF(String token, int collectionSize, Map<String, List<StatTuple>> indexTable) {
        return Math.log((double)collectionSize / getNumOfDocAppearances(indexTable.get(token))) / Math.log(2);
    }

    private int getNumOfDocAppearances(List<StatTuple> tokenStats) {
        return tokenStats.size();
    }
}
