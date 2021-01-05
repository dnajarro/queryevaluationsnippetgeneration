package shared;

public class SnippetSentence extends ResultSentence implements Comparable<SnippetSentence>{
    private float score;

    public SnippetSentence(float score, String sentence, int[] queryIndexes) {
        super(sentence, queryIndexes);
        this.score = score;
    }

    public SnippetSentence(String sentence, int[] queryIndexes) {
        super(sentence, queryIndexes);
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int compareTo(SnippetSentence o) {
        float val = o.score - this.score;
        int result = 0;
        if (val > -0.001 && val < 0.001) {
            result = 0;
        }
        if (val > 0) {
            result = 1;
        } else if (val < 0) {
            result = -1;
        }
        return result;
    }

}
