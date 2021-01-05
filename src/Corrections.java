import java.util.ArrayList;
import java.util.List;

public class Corrections {
    private String originalWord;
    private String soundexCode;
    private List<String> corrections;

    public Corrections(String word, String code, List<String> corrections) {
        originalWord = word;
        soundexCode = code;
        this.corrections = corrections;
    }

    public Corrections() {
        corrections = new ArrayList<>();
    }

    public List<String> getCorrections() {
        return corrections;
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public String getSoundexCode() {
        return soundexCode;
    }

    public void setCorrections(List<String> corrections) {
        this.corrections = corrections;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

    public void setSoundexCode(String soundexCode) {
        this.soundexCode = soundexCode;
    }

    public boolean add(String word) {
        return corrections.add(word);
    }

    public boolean clear() {
        corrections.clear();
        return true;
    }
}
