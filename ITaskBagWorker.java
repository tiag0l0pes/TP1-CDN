import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ITaskBagWorker extends Remote {
    public void registerWorker(IWorker worker)throws RemoteException;
    public void jobResult(int jobId, ArrayList<Integer> primeList) throws RemoteException;
}
