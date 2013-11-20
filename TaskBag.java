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
        try{
            job.setPrimeList(primeList);
            System.out.println("Receiving Result " + job.getMin() + " - " + job.getMax() + " Master: " + job.getMaster());
            freeWorkers.add(job.getWorker());

            if (allMasterJobsCompleted(job.getMaster())){
                List<Integer> result = new ArrayList<Integer>();

                for(Job mjob : masterJobs.get(job.getMaster())){
                    result.addAll(mjob.getPrimeList());
                }

                masterCompletedWork.put(job.getMaster(),result);
                masterJobs.remove(job.getMaster());

                sendCompletedResult(job.getMaster());
            }
        } catch (Exception ex){};

        updateJobs();
    }

    @Override
    public void registerMaster(String name, IMaster master) {
        System.out.println("Accepting Master " + name + "...");
        masters.put(name, master);

        if(masterCompletedWork.containsKey(name))
            try {
                System.out.println("Send completed work to " + name + "...");
                sendCompletedResult(name, master);
            } catch (RemoteException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

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

    private void sendCompletedResult(String name, IMaster master) throws RemoteException{
        System.out.println("Send Completed Result to "+name+"...");
        String responseString = "{";

        for (Integer num : masterCompletedWork.get(name))
        {
            responseString += num + ",";
        }

        responseString += "}";
        master.printResult(responseString);
        masterCompletedWork.remove(name);
    }

    private void sendCompletedResult(String name) throws RemoteException{
        System.out.println("Send Completed Result to "+name+"...");
        IMaster master = masters.get(name);
        String responseString = "{";

        for (Integer num : masterCompletedWork.get(name))
        {
            responseString += num + ",";
        }

        responseString += "}";
        master.printResult(responseString);
        masterCompletedWork.remove(name);
    }

    private boolean allMasterJobsCompleted(String name){
        for(Job job : masterJobs.get(name)){
            if(!job.isProcessed())
                return false;
        }
        return true;
    }

    private void updateJobs() {
        if (freeWorkers.isEmpty()) return;
        if (jobs.isEmpty() && jobsBeingProcessed.isEmpty()) return;
        // if jobs.isEmpty and jobsBeingPRocessed isn't empty maybe a worker is failing so assign the job to another worker
        if (jobs.isEmpty() && !jobsBeingProcessed.isEmpty()) jobs.addAll(jobsBeingProcessed.values());

        IWorker worker = freeWorkers.remove(0);
        Job job = jobs.poll();

        while(job.isProcessed()){
            if(jobs.isEmpty()){
                freeWorkers.add(worker);
                return;
            }
            job = jobs.poll();
        };

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
