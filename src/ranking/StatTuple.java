package ranking;

public class StatTuple {
    private int docNum;
    private int docFreq;

    public StatTuple(int docNum, int docFreq) {
        this.docNum = docNum;
        this.docFreq = docFreq;
    }

    public int getDocNum() {
        return docNum;
    }

    public int getDocFreq() {
        return docFreq;
    }

    public void setDocNum(int docNum) {
        this.docNum = docNum;
    }

    public void setDocFreq(int docFreq) {
        this.docFreq = docFreq;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(docNum);
        sb.append(", ");
        sb.append(docFreq);
        sb.append(")");
        return sb.toString();
    }
}
