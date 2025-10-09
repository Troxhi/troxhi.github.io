public class Pow {

    public static void main(String[] args) {
        int result = pow(2,3);
        System.out.println(result); // expect 8
    }

    // time complexity: O(log k)
    public static int pow(int n, int k) {
        int result = 1;
        while (k > 0) {
            if (k % 2 == 1) {
                result = result * n; // result *= n would also work
            }
            n = n * n;
            k = k / 2;
        }
        return result;
    }
}
