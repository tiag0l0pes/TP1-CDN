import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.System.getSecurityManager;
import static java.lang.System.setSecurityManager;
import static java.rmi.Naming.lookup;

public class Worker implements IWorker {
    private final String SERVER = "rmi://localhost:2001/TaskBag";
    private ITaskBagWorker taskBag;

    private void initialize() {
        try {
            System.out.println("Connecting to server...");
            UnicastRemoteObject.exportObject(this);
            System.out.println("Looking up TaskBagService at " + SERVER);

            taskBag = (ITaskBagWorker) lookup(SERVER);
            taskBag.registerWorker(this);
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NotBoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String args[]) {
        //set the security manager
        if (getSecurityManager() == null) {
            setSecurityManager(new RMISecurityManager());
        }

        Worker worker = new Worker();
        worker.initialize();
    }

    @Override
    public void terminate() {
        System.out.println("Server terminated... Aborting!!!");
        System.exit(0);
    }

    @Override
    public void calculate(int jobId, int min, int max) {
        PrimeThread thread = new PrimeThread(jobId, min, max, taskBag);
        thread.start();
    }
}

class PrimeThread extends Thread {
    private int min;
    private int max;
    private int jobId;
    private ITaskBagWorker taskBag;

    public PrimeThread(int jobId, int min, int max, ITaskBagWorker taskBag) {
        this.jobId = jobId;
        this.min = min;
        this.max = max;
        this.taskBag = taskBag;
    }

    @Override
    public void run() {
        PrimeNumberCalc primes = new PrimeNumberCalc(min, max);
        System.out.println(jobId + ", "+  min + ", " +max);
        try {
            Thread.sleep(3000);
            taskBag.jobResult(jobId, primes.calculatePrimeNumbers());
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
