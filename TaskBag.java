import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskBag implements Remote, ITaskBag {
    private final int SPLITER = 1000;
    private int workersCounter = 0;
    private Map<Integer, Job> jobsBeingProcessed = new Hashtable<Integer, Job>();
    private Map<String, List<Integer>> masterCompletedWork = new Hashtable<String, List<Integer>>();
    private Map<String, List<Job>> masterJobs = new Hashtable<String, List<Job>>();
    private List<IWorker> freeWorkers = new ArrayList<IWorker>();
    private List<IWorker> workers = new ArrayList<IWorker>(10);
    private Map<String, IMaster> masters = new Hashtable<String, IMaster>();
    private Queue<Job> jobs = new ConcurrentLinkedQueue<Job>();

    //TODO: case a master isn't responding save the data in masterCompleteWork and when the master reconnect send the data
    //TODO: reassign jobs from broken workers to another worker

    @Override
    public void registerWorker(IWorker worker) {
        System.out.println("Accepting Worker #" + workersCounter++ + "...");
        workers.add(worker);
        freeWorkers.add(worker);

        updateJobs();
    }

    @Override
    public void jobResult(int jobId, ArrayList<Integer> primeList) throws RemoteException {
        Job job = jobsBeingProcessed.remove(jobId);
        job.setPrimeList(primeList);

        freeWorkers.add(job.getWorker());

        //TODO: logic to see if the all jobs of  this master are completed, if yes return the data to the master
        //TODO: if failing to communicate with the cliente save the data in masterCompleteWork
        IMaster master = masters.get(job.getMaster());
        master.printResult("asdasdsad");


        updateJobs();
    }

    @Override
    public void registerMaster(String name, IMaster master) {
        System.out.println("Accepting Master " + name + "...");
        masters.put(name, master);
    }

    @Override
    public void calculate(int start, int end, String name) throws RemoteException {
        List<Job> jobList = new ArrayList<Job>(20);
        int interval = (end - start) / SPLITER;

        while(interval < end) {
            if (interval + SPLITER > end) {
                interval = end;
            } else {
                interval = start + SPLITER - 1;
            }

            Job job = new Job(start, interval, name);
            jobs.add(job);
            jobList.add(job);
            start = interval + 1;
        }

        masterJobs.put(name, jobList);
        updateJobs();
    }

    private void updateJobs() {
        if (freeWorkers.isEmpty()) return;
        if (jobs.isEmpty() && jobsBeingProcessed.isEmpty()) return;
        //TODO: if jobs.isEmpty and jobsBeingPRocessed isn't empty maybe a worker is failing so assign the job to another worker

        IWorker worker = freeWorkers.remove(0);
        Job job = jobs.poll();
        job.setWorker(worker);
        jobsBeingProcessed.put(job.getId(), job);

        try {
            worker.calculate(job.getId(), job.getMin(), job.getMax());
        } catch (RemoteException e) {
            jobs.add(job);
            jobsBeingProcessed.remove(job);
            workers.remove(worker);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void terminateAllConnections() {
        System.out.println("Terminating all connections...");
        System.out.println("Masters...");
        for (String key : masters.keySet()) {
            try {
                masters.get(key).terminate();
            } catch (RemoteException e) {
                //all throw the error
            }
        }

        System.out.println("Workers...");
        for (IWorker worker : workers) {
            try {
                worker.terminate();
            } catch (RemoteException e) {
                //all throw the error
            }
        }
    }
}
