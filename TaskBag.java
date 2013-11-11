import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskBag implements Remote, ITaskBag {
    private final int SPLITER = 1000;
    private int mastersCounter = 0;
    private int workersCounter = 0;
    private Map<Integer, IWorker> workers = new Hashtable<Integer, IWorker>();
    private Map<Integer, IMaster> masters = new Hashtable<Integer, IMaster>();
    private Queue<Job> jobs = new ConcurrentLinkedQueue<Job>();

    //TODO: remove jobs from queue that master is broken
    //TODO: reassign jobs from broken workers to another worker
    //TODO: prolly implement calculate in a thread

    @Override
    public void registerWorker(IWorker worker) {
        System.out.println("Accepting Worker " + workersCounter + "...");
        workers.put(workersCounter++, worker);
    }

    @Override
    public void registerMaster(IMaster master) {
        System.out.println("Accepting Master " + mastersCounter + "...");
        masters.put(mastersCounter++, master);
    }

    @Override
    public void calculate(long start, long end, IMaster master) throws RemoteException {
       //TODO: refactor this, debugging purposes only
        long x;
        if (end > start) {
            x = end - start;
        }
        else {
            x = start - end;
        }

        x /= SPLITER;
        System.out.println(x);
        while(x < end) {
            if (x + SPLITER > end) {
                x = end;
            } else {
                x = start + SPLITER - 1;
            }

            jobs.add(new Job(start, x, master));
            System.out.println("de " + start + " a " + x);
            start = x + 1;
        }


    }

    public void terminateAllConnections() {
        System.out.println("Terminating all connections...");
        System.out.println("Masters...");
        for (int key : masters.keySet()) {
            try {
                masters.get(key).terminate();
            } catch (RemoteException e) {
                //all throw the error
            }
        }

        System.out.println("Workers...");
        for (int key : workers.keySet()) {
            try {
                workers.get(key).terminate();
            } catch (RemoteException e) {
                //all throw the error
            }
        }
    }
}
