public class SubArray {

	/**
 	 * We assume that A, B will never be null or empty
 	 */ 
	public static boolean arrayStartsWith(int[] A, int[] B, int offset) {
		if (offset < 0 || A.length < B.length || offset > A.length - B.length) {
			return false;
		}
		for (int j = 0; j < B.length; j++) {
			if (A[offset+j] != B[j]) {
				return false;
			}
		}
		return true;
	}
}
