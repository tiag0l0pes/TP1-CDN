import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.System.getSecurityManager;
import static java.lang.System.setSecurityManager;
import static java.rmi.Naming.lookup;

public class Master implements IMaster {
    private final String SERVER = "rmi://localhost:2001/TaskBag";
    private ITaskBagMaster taskBag;

    private void initialize() {
        try {
            System.out.println("Connecting to server...");
            UnicastRemoteObject.exportObject(this);
            System.out.println("Looking up TaskBagService at " + SERVER);

            taskBag = (ITaskBagMaster) lookup(SERVER);
            taskBag.registerMaster(this);
        } catch (RemoteException e) {
            System.out.println("ERROR: An exception occur connecting to server!!");
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NotBoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void calculate(long start, long end) {
        try {
            taskBag.calculate(start, end, this);
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String args[]) {
        if (args.length < 2) {
            System.out.println("Syntax: Master minValue maxValue");
            System.exit(1);
        }
        if (getSecurityManager() == null) {
            setSecurityManager(new RMISecurityManager());
        }

        Master master = new Master();
        master.initialize();
        master.calculate(Long.parseLong(args[0]), Long.parseLong(args[1]));
    }

    @Override
    public void printResult(String result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void terminate() {
        System.out.println("Server terminated... Aborting!!!");
        System.exit(0);
    }
}
