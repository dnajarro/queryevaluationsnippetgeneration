public class ProbOfError {
    private String misspelledWord;
    private String correctWord;
    private float probability;

    public ProbOfError(String misspelledWord, String correctWord, float probability) {
        this.misspelledWord = misspelledWord;
        this.correctWord = correctWord;
        this.probability = probability;
    }

    public float getProbability() {
        return probability;
    }

    public String getCorrectWord() {
        return correctWord;
    }

    public String getMisspelledWord() {
        return misspelledWord;
    }

    public void setCorrectWord(String correctWord) {
        this.correctWord = correctWord;
    }

    public void setMisspelledWord(String misspelledWord) {
        this.misspelledWord = misspelledWord;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }
}
