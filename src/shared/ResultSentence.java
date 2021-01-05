package shared;

public class ResultSentence {
    private String[] sentence;
    private int[] queryIndexes;

    public ResultSentence(String sentence, int[] queryIndexes) {
        this.sentence = sentence.split(" ");
        this.queryIndexes = queryIndexes;
    }

    public String[] getSentence() {
        return sentence;
    }

    public int[] getQueryIndexes() {
        return queryIndexes;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence.split(" ");
    }

    public void setQueryIndexes(int[] queryIndexes) {
        this.queryIndexes = queryIndexes;
    }

}
