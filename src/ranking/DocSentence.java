package ranking;

public class DocSentence {
    private String sentence;
    private int lineNum;
    private int sentenceNum;

    public DocSentence(String sentence, int lineNum, int sentenceNum) {
        this.sentence = sentence;
        this.lineNum = lineNum;
        this.sentenceNum = sentenceNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getSentenceNum() {
        return sentenceNum;
    }

    public String getSentence() {
        return sentence;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public void setSentenceNum(int sentenceNum) {
        this.sentenceNum = sentenceNum;
    }

    public int getLength() {
        return sentence.split(" ").length;
    }
}
