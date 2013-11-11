import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITaskBagWorker extends Remote {
    public void registerWorker(IWorker worker)throws RemoteException;
}
