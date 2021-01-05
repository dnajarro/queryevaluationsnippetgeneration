package ranking;

import java.util.List;

public class Span {
    private String span;
    private int startIndex;
    private int endIndex;
    private int[] significantWords;

    public Span(String span, int startIndex, int endIndex, List<Integer> significantWords) {
        this.span = span;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.significantWords = new int[significantWords.size()];
        int j = 0;
        for (int i = startIndex; i < endIndex + 1; i++) {
            this.significantWords[j] = significantWords.get(i);
            j++;
        }
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public String getSpan() {
        return span;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public void setSpan(String span) {
        this.span = span;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getLength() {
        return span.split(" ").length;
    }

    public int[] getSignificantWords() {
        return significantWords;
    }

    public void setSignificantWords(List<Integer> significantWords) {
        int size = 0;
        for (int i = startIndex; i < endIndex + 1; i++) {
            if (significantWords.contains(i)) {
                size++;
            }
        }

        this.significantWords = new int[size];
        int j = 0;
        int curIndex = significantWords.indexOf(startIndex);
        while (j < size) {
            this.significantWords[j] = significantWords.get(curIndex);
            curIndex++;
            j++;
        }
    }

    public int getNumOfSignificantWords() {
        return significantWords.length;
    }
}
