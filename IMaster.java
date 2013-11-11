import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMaster extends Remote {
    public void printResult(String result) throws RemoteException;
    public void terminate() throws RemoteException;
}
