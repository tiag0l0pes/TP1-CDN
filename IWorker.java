import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IWorker extends Remote {
    public void terminate() throws RemoteException;
    public void calculate(int jobId, int min, int max) throws RemoteException;
}
