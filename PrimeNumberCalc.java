import java.lang.reflect.Array;

public class PrimeNumberCalc {
    private final int LOWER_LIMIT;
    private final int UPPER_LIMIT;

    public PrimeNumberCalc(int l, int u) {
      this.LOWER_LIMIT = l;
      this.UPPER_LIMIT = u;
    }  

    public static void main(String[] args) {
        if (Array.getLength(args) == 2) {
                PrimeNumberCalc pnc = new PrimeNumberCalc(
                    new Integer(args[0]),
                    new Integer(args[1])
                    );
            pnc.calculatePrimeNumbers();
        } else {
            System.out.println("Sintaxe: java PrimeNumberCacl LOWER_LIMIT UPPER_LIMIT");
        }
    }

    public void calculatePrimeNumbers() {
        int i = LOWER_LIMIT;
        int primeNumberCounter = 0;
        while (++i <= UPPER_LIMIT) {
            int i1 = (int) Math.ceil(Math.sqrt(i));
            boolean isPrimeNumber = false;
            while (i1 > 1) {
                if ((i != i1) && (i % i1 == 0)) {
                    isPrimeNumber = false;
                    break;
                } else if (!isPrimeNumber) {
                    isPrimeNumber = true;
                }
                --i1;
            }
            if (isPrimeNumber) {
                System.out.println(i);
                ++primeNumberCounter;
            }
        }
        System.out.println("Nr of prime numbers found: "
				+ primeNumberCounter);
    }
}
