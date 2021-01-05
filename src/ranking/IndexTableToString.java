package ranking;

import java.util.List;

public class IndexTableToString {
    public String toString(List<TokenStats> indexTable) {
        StringBuilder sb = new StringBuilder();
        for (TokenStats tokenStats : indexTable) {
            sb.append(tokenStats.getToken());
            sb.append(" ");
            sb.append(tokenStats.getStatTuples().toString());
            sb.append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }


}
