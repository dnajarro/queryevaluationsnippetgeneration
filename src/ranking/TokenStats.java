package ranking;

import java.util.ArrayList;
import java.util.List;

public class TokenStats implements Comparable<TokenStats> {
    private String token;
    private List<StatTuple> stats;

    public TokenStats(String token, int docNum, int docFreq) {
        this.token = token;
        this.stats = new ArrayList<StatTuple>();
        stats.add(new StatTuple(docNum, docFreq));
    }

    public TokenStats(String token, List<StatTuple> stats) {
        this.token = token;
        this.stats = stats;
    }

    public String getToken() {
        return token;
    }

    public int getDocNum(int num) {
        return stats.get(num).getDocNum();
    }

    public int getDocFreq(int num) {
        return stats.get(num).getDocFreq();
    }

    public void setDocFreq(int num, int docFreq) {
        this.stats.get(num).setDocFreq(docFreq);
    }

    public void setDocNum(int num, int docNum) {
        this.stats.get(num).setDocNum(docNum);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<StatTuple> getStatTuples() {
        return stats;
    }

    @Override
    public int compareTo(TokenStats o) {
        return token.compareTo(o.token);
    }
}
