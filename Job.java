import java.util.List;

public class Job {
    private static int jobIdCounter = 0;
    private int id;
    private int min;
    private int max;
    private IWorker worker;
    private String master;
    private List<Integer> primeList;

    public Job(int min, int max, String master) {
        this.min = min;
        this.max = max;
        this.master = master;
        id = jobIdCounter++;
        primeList = null;
    }

    public List<Integer> getPrimeList() {
        return primeList;
    }

    public void setPrimeList(List<Integer> primeList) {
        if (primeList != null) return;

        this.primeList = primeList;
    }

    public int getId() {
        return id;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public IWorker getWorker() {
        return worker;
    }

    public void setWorker(IWorker worker) {
        this.worker = worker;
    }

    public String getMaster() {
        return master;
    }

    public boolean isProcessed() {
        return primeList != null;
    }
}
