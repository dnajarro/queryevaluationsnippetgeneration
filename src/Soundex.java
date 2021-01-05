public class Soundex {
    private final int MAXDIGITS = 3;
    public String encode(String word) {
        StringBuilder sb = new StringBuilder();

        if (!word.isEmpty()) {
            capitalizeFirstLetter(word.charAt(0), sb);  // step 1: capitalize first letter
            generalizeLetters(word, sb);    // steps 2 and 3

            char[] newWord = sb.toString().toCharArray();
            sb.setLength(0);

            markAdjRepeatNums(newWord, sb);   // step 4
            removeHyphensAndMarked(newWord, sb);    // step 4 and 5

            shortenCode(sb);   // step 6
            padCode(sb);
        }

        return sb.toString();
    }

    private void padCode(StringBuilder sb) {
        int digits = 0;
        for (int i = 0; i < sb.length(); i++) { // step 6: pad with 0's if less than 3 digits
            if (Character.isDigit(sb.charAt(i))) {
                digits++;
            }
        }

        if (MAXDIGITS - digits > 0) {
            for (int i = 0; i < MAXDIGITS - digits; i++) {
                sb.append(0);
            }
        }
    }

    private void shortenCode(StringBuilder sb) {
        int digits = 0;
        for (int i = 0; i < sb.length(); i++) { // step 6: keep the first 3 digits
            if (Character.isDigit(sb.charAt(i))) {
                digits++;
            }
            if (digits == MAXDIGITS) {
                sb.delete(i + 1, sb.length());
                break;
            }
        }
    }

    private void removeHyphensAndMarked(char[] word, StringBuilder sb) {
        for (int i = 0; i < word.length; i++) {  // step 4 and 5: remove hyphens and adjacent repeated numbers
            if (word[i] != '-' && word[i] != ' ') {
                sb.append(word[i]);
            }
        }
    }

    private void markAdjRepeatNums(char[] word, StringBuilder sb) {
        char first = ' ';
        char second = ' ';

        for (int i = 0; i < word.length; i++) {  // step 4: replace adjacent repeated numbers with spaces
            if (i == 0) {
                first = word[i];
                second = word[i];
            } else {
                second = word[i];
                if (first == second) {
                    word[i] = ' ';
                }
                first = second;
            }
        }
    }

    private void generalizeLetters(String word, StringBuilder sb) {
        for (int i = 1; i < word.length(); i++) {
            if (isVowelSemivowel(word.charAt(i))) {   // step 2: replace vowels, semivowels, and h with -
                sb.append('-');
            }
            if (isLabial(word.charAt(i))) { // step 3: replace consonants with numbers
                sb.append(1);
            }
            if (isVelarPostalveolar(word.charAt(i))) {
                sb.append(2);
            }
            if (isDentalAlveolar(word.charAt(i))) {
                sb.append(3);
            }
            if (isLateralApproximant(word.charAt(i))) {
                sb.append(4);
            }
            if (isNasal(word.charAt(i))) {
                sb.append(5);
            }
            if (isApproximant(word.charAt(i))) {
                sb.append(6);
            }
        }

    }

    private void capitalizeFirstLetter(char c, StringBuilder sb) {
        if (Character.isAlphabetic(c)) {   // step 1: keep 1st letter in upper case
            sb.append(Character.toUpperCase(c));
        }
    }

    private boolean isApproximant(char c) {
        return c == 'r';
    }

    private boolean isNasal(char c) {
        if (c != 'm') {
            return c == 'n';
        }
        return true;
    }

    private boolean isLateralApproximant(char c) {
        return c == 'l';
    }

    private boolean isDentalAlveolar(char c) {
        if (c != 'd') {
            return c == 't';
        }
        return true;
    }

    private boolean isVelarPostalveolar(char c) {
        if (c != 'k') {
            if (c != 'g') {
                if (c != 'x') {
                    if (c != 'z') {
                        if (c != 'j') {
                            if (c != 'c') {
                                if (c != 's') {
                                    return c == 'q';
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isLabial(char c) {
        if (c != 'b') {
            if (c != 'f') {
                if (c != 'p') {
                    return c == 'v';
                }
            }
        }
        return true;
    }

    private boolean isVowelSemivowel(char c) {
        if (c != 'a') {
            if (c != 'e') {
                if (c != 'i') {
                    if (c != 'o') {
                        if (c != 'u') {
                            if (c != 'y') {
                                if (c != 'h') {
                                    return c == 'w';
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
