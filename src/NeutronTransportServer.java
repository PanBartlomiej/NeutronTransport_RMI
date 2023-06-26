import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NeutronTransportServer {
    public static void main(String[] args) {
        try {
            // Inicjalizacja serwera
            NeutronTransportInterface server = new NeutronTransportImpl();
            NeutronTransportInterface server2 = new NeutronTransportImpl();

            // Uruchomienie rejestru RMI na porcie 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            Registry registry2 = LocateRegistry.createRegistry(1098);

            // Rejestracja serwera w rejestrze RMI
            registry.bind("NeutronTransport", server);
            registry2.rebind("NeutronTransport2", server2);

            System.out.println("NeutronTransportServer: Server ready.");
        } catch (Exception e) {
            System.err.println("NeutronTransportServer exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
