import java.lang.reflect.Array;
import java.util.ArrayList;

public class PrimeNumberCalc {
    private final int LOWER_LIMIT;
    private final int UPPER_LIMIT;

    public PrimeNumberCalc(int l, int u) {
      this.LOWER_LIMIT = l;
      this.UPPER_LIMIT = u;
    }  

    public ArrayList<Integer> calculatePrimeNumbers() {
        int i = LOWER_LIMIT;
        ArrayList<Integer> primes = new ArrayList<Integer>(100);
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
                primes.add(i);
            }
        }

        return primes;
    }
}
