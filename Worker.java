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
}
