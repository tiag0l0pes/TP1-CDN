import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITaskBagMaster extends Remote {
    public void registerMaster(String name, IMaster master) throws RemoteException;
    public void calculate(int start, int end, String name) throws RemoteException;
}
