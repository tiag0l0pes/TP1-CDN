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
    private String name;

    public Master(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void initialize() {
        try {
            System.out.println("Connecting to server...");
            UnicastRemoteObject.exportObject(this);
            System.out.println("Looking up TaskBagService at " + SERVER);

            taskBag = (ITaskBagMaster) lookup(SERVER);
            taskBag.registerMaster(getName(), this);
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

    private void calculate(int start, int end) {
        try {
            taskBag.calculate(start, end, getName());
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

            public static void main(String args[]) {
        if (args.length < 3) {
            System.out.println("Syntax: Master name minValue maxValue");
            System.exit(1);
        }
        if (getSecurityManager() == null) {
            setSecurityManager(new RMISecurityManager());
        }

        int min = 0;
        int max = 0;
        try {
            min = Integer.parseInt(args[1]);
            max = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Syntax: Master name minValue maxValue");
            System.out.println("\tminValue and maxValue must be integer numbers");
            System.exit(1);
        }

        if (min > max) {
            int temp = max;
            max = min;
            min = temp;
        }

        Master master = new Master(args[0]);
        master.initialize();
        master.calculate(min, max);
    }

    @Override
    public void printResult(String result) {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println(result);
    }

    @Override
    public void terminate() {
        System.out.println("Server terminated... Aborting!!!");
        System.exit(0);
    }
}
