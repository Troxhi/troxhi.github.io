import java.util.Scanner;

public class Potenzieren {

	public static void main(String[] args) {
		int n, k;
		
		Scanner console = new Scanner(System.in);
		
		System.out.print("Geben Sie Zahl 1 und 2 ein: ");
		n = console.nextInt();
		k = console.nextInt();
		
		int pot = 1;
		for (int i = 1; i <= k; i++) {
			pot = pot * n;
		}
		
		System.out.println(n + " hoch " + k + " = " + pot);
	}

}

