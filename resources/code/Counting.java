public class Counting {
    public static boolean containsSubstringAt(String str, int position, String sub) {
        return str.startsWith(sub, position);
    }

    public static int countSubstrings(String str, String sub) {
        if (str.length() < sub.length()) {
            return 0;
        }
        int occurrences = 0;
        for (int i = 0; i < str.length(); i++) {
            if (containsSubstringAt(str, i, sub)) {
                occurrences++;
            }
        }

        return occurrences;
    }

    public static int countDisjointSubstrings(String str, String sub) {
        if (str.length() < sub.length()) {
            return 0;
        }

        int occurrences = 0;
        int i = 0;
        while (i <= str.length() - sub.length()) {
            if (containsSubstringAt(str, i, sub)) {
                occurrences++;
                i += sub.length();
            } else {
                i++;
            }
        }

        return occurrences;
    }
}
