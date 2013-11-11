
public class Job {
    private long min;
    private long max;
    private IWorker worker;
    private IMaster master;

    public Job(long min, long max, IMaster master) {
        this.min = min;
        this.max = max;
        this.master = master;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public IWorker getWorker() {
        return worker;
    }

    public void setWorker(IWorker worker) {
        this.worker = worker;
    }

    public IMaster getMaster() {
        return master;
    }
}
