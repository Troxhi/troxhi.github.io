public class FastPrefixConstruction {

	public static void main(String[] args) {
		System.out.println(isPrefixConstruction("aa", "a", 1)); // false
		System.out.println(isPrefixConstruction("aa", "a", 2)); // true
	}

	public static boolean isPrefixConstruction(String s, String t, int n) {
		String mergedString = t + s;
		int[] z = calculateZFunction(mergedString); // O(m + n) <= O(max(m, n))
		
		// we use string / array indexing
		// we add 2 because we have a sentinel character at the end
		int[] jumps = new int[s.length()+2];
		int k = 1;
		jumps[0] = -1;
		jumps[1] = 0;
		while (jumps[k] <= s.length() - 1) {
			k = k + 1;
			for (int i = jumps[k-2] + 1; i <= jumps[k-1]; i++) {
				int jumpLength = Math.min(t.length(), z[t.length() + i]);
				jumps[k] = Math.max(jumps[k], i + jumpLength);
			}
			// we are stuck... famous last words
			if (jumps[k-1] == jumps[k]) {
				return false;
			}
		}
		// we subtract 1 since we start counting at 1
		int requiredJumps = k - 1;
		return requiredJumps <= n;
	}
	
    public static int[] calculateZFunction(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;
        for(int i = 1; i < n; i++) {
            if(i < r) {
                z[i] = Math.min(r - i, z[i - l]);
            }
            while(i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if(i + z[i] > r) {
                l = i;
                r = i + z[i];
            }
        }
        return z;
    }
}
