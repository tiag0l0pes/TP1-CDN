import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IWorker extends Remote {
    public void terminate() throws RemoteException;
}
