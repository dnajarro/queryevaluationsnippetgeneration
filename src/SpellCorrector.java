import java.io.*;
import java.util.*;

public class SpellCorrector {
    private Map<String, List<String>> soundexLibrary;

    private Soundex soundex;

    public SpellCorrector(String filename) {
        soundex = new Soundex();
        soundexLibrary = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            int num = 0;
            String line;
            while ((line = br.readLine()) != null) {
                num++;
                storeSoundex(line, convertToSoundex(line));
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Failed to read line");
        }
    }

    public List<Corrections> correct(List<String> queryWords) {
        List<Corrections> possibleCorrections = new ArrayList<>();
        for (String s : queryWords) {
            String soundexCode = convertToSoundex(s);
            Corrections corrections = findSimilarWords(s, soundexCode);
            if (corrections != null) {
                possibleCorrections.add(corrections);
            }
        }
        return possibleCorrections;
    }

    private Corrections findSimilarWords(String word, String soundex) {
        Corrections corrections = new Corrections();
        corrections.setOriginalWord(word);
        corrections.setSoundexCode(soundex);
        if (soundexLibrary.containsKey(soundex)) {
            List<String> similarWords = soundexLibrary.get(soundex);
            EditDistance editDistance = new EditDistance();
            for (String s : similarWords) {
                if (editDistance.levenshtein(toLowerCase(word), s) == 0) {
                    corrections.clear();
                    corrections.add(s);
                    return corrections;
                }
                if (editDistance.levenshtein(word, s) < 3) {
                    corrections.add(s);
                }
            }
        }
        return corrections;
    }

    private String convertToSoundex(String word) {
        return soundex.encode(word);
    }

    private String toLowerCase(String word) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            sb.append(Character.toLowerCase(word.charAt(i)));
        }
        return sb.toString();
    }

    private void storeSoundex(String word, String soundexCode) {
        if (!soundexLibrary.containsKey(soundexCode)) {
            List<String> originalWords = new ArrayList<>();
            originalWords.add(word);
            soundexLibrary.put(soundexCode, originalWords);
        } else {
            List<String> originalWords = soundexLibrary.get(soundexCode);
            originalWords.add(word);
            soundexLibrary.put(soundexCode, originalWords);
        }
    }
}
