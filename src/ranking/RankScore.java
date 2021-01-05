package ranking;

public class RankScore implements Comparable<RankScore> {
    private double score;
    private int docNum;

    public RankScore(double score, int docNum) {
        this.score = score;
        this.docNum = docNum;
    }

    public int getDocNum() {
        return docNum;
    }

    public double getScore() {
        return score;
    }

    public void setDocNum(int docNum) {
        this.docNum = docNum;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public int compareTo(RankScore o) {
        double val = this.score - o.score;
        if (val < 0.0001 && val >= 0.0) {
            return 0;
        } else if (val > -0.0001 && val <= 0.0) {
            return 0;
        }
        else if (val < 0) {
            return (int)Math.floor(val);
        }

        return (int)Math.ceil(val);
    }
}
