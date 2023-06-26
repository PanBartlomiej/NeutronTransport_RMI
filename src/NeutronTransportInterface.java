import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NeutronTransportInterface extends Remote {
    void performMonteCarloSimulation(long totalIterations,NeutronTransportParam params) throws RemoteException;
    long getReflectedParticles() throws RemoteException;
    long getCapturedParticles() throws RemoteException;
    long getPassedParticles() throws RemoteException;
    long getIteration() throws RemoteException;
}
