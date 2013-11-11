import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITaskBagMaster extends Remote {
    public void registerMaster(IMaster master) throws RemoteException;
    public void calculate(long start, long end, IMaster master) throws RemoteException;
}
