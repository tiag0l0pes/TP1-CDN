import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.System.setSecurityManager;
import static java.rmi.Naming.rebind;
import static java.rmi.Naming.unbind;

public class TaskBagService {
    private static final int PORT = 2001;
    private static final String SERVICE = "//localhost:" + PORT + "/TaskBag";
    private static TaskBag taskBag;

    public static void main(String args[]) {
        setSecurityManager(new RMISecurityManager());

        taskBag = new TaskBag();
        try {
            System.out.println("Opening Port: " + PORT);
            LocateRegistry.createRegistry(PORT);
            System.out.println("Port Open!!\nCreating binds...");
            UnicastRemoteObject.exportObject(taskBag);
            rebind(SERVICE, taskBag);
            System.out.println("Bindings done successful!!!");
            System.out.println("\nWaiting for requests...");
        } catch (RemoteException e) {
            System.out.println("ERROR: An exception occur opening the Port!!");
            cleanup();
            e.printStackTrace();
            System.exit(1);
        } catch (MalformedURLException e) {
            System.out.println("ERROR: An exception occur creating the bind!!");
            cleanup();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Server started.");
        System.out.println("Enter <CR> to end.");
        try {
            System.in.read();
            cleanup();
        } catch (IOException ioException) {
        }
        System.exit(0);
    }

    private static void cleanup() {
        try {
            unbind(SERVICE);
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NotBoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        taskBag.terminateAllConnections();
    }
}
