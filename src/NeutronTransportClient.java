import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NeutronTransportClient {
    public static void main(String[] args) {
        try {
            //Program można by łatwo roszerzyć o większą liczbę serwerów dodając dodatkowe rejestry
            // a dla dużej ilości serwerów można wprowadzić pętlę wykonującą poniższy kod z dodatkowym
            // parametrem odpowiednio zmieniającym nazwy serwrów i wywołań

            // Połączenie z rejestracją RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Registry registry2 = LocateRegistry.getRegistry("localhost", 1098);
            // Pobranie referencji do zdalnego obiektu
            NeutronTransportInterface server = (NeutronTransportInterface) registry.lookup("NeutronTransport");
            NeutronTransportInterface server2 = (NeutronTransportInterface) registry2.lookup("NeutronTransport2");


            long iter;
            NeutronTransportParam params = new NeutronTransportParam();
            if(args.length !=4){
            params.Cc = 0.5;
            params.Cs = 0.2;
            params.C = 1.0;
            params.H = 10.0;
            iter=1000000;
            }
            else{
                iter=Long.parseLong(args[0]);
                params.Cc = Double.parseDouble( args[1]);
                params.Cs = Double.parseDouble( args[2]);
                params.C = 1.0;
                params.H = Double.parseDouble( args[3]);
            }

            // Wywołanie metody na zdalnym obiekcie
            server.performMonteCarloSimulation(iter, params);

            // Pobranie wyników
            long reflectedParticles = server.getReflectedParticles();
            long capturedParticles = server.getCapturedParticles();
            long passedParticles = server.getPassedParticles();
            long sum =reflectedParticles+capturedParticles+passedParticles;

            // Wyświetlenie wyników
            System.out.println("SERVER 1: Reflected particles: " + reflectedParticles);
            System.out.println("SERVER 1: Captured particles: " + capturedParticles);
            System.out.println("SERVER 1: Passed particles: " + passedParticles);
            System.out.println("SERVER 1: sum: "+sum);
            System.out.println("SERVER 1: Reflected particle chance: " + (double)reflectedParticles/sum);
            System.out.println("SERVER 1: Captured particle chance: " + (double)capturedParticles/sum);
            System.out.println("SERVER 1: Passed particle chance: " + (double)passedParticles/sum);

            // Wywołanie metody na zdalnym obiekcie
            server2.performMonteCarloSimulation(iter, params);

            // Pobranie wyników
            long reflectedParticles2 = server2.getReflectedParticles();
            long capturedParticles2 = server2.getCapturedParticles();
            long passedParticles2 = server2.getPassedParticles();
            long sum2 =reflectedParticles+capturedParticles+passedParticles;

            // Wyświetlenie wyników
            System.out.println("SERVER 2: Reflected particles: " + reflectedParticles2);
            System.out.println("SERVER 2: Captured particles: " + capturedParticles2);
            System.out.println("SERVER 2: Passed particles: " + passedParticles2);
            System.out.println("SERVER 2: sum: "+sum2);
            System.out.println("SERVER 2: Reflected particle chance: " + (double)reflectedParticles2/sum2);
            System.out.println("SERVER 2: Captured particle chance: " + (double)capturedParticles2/sum2);
            System.out.println("SERVER 2: Passed particle chance: " + (double)passedParticles2/sum2);
            System.out.println("-------------TOTAL-------------");
            System.out.println("Total: Reflected particle chance: " + (double)(reflectedParticles+reflectedParticles2)/(sum+sum2));
            System.out.println("Total: Captured particle chance: " + (double)(capturedParticles+capturedParticles2)/(sum+sum2));
            System.out.println("Total: Passed particle chance: " + (double)(passedParticles+passedParticles2)/(sum+sum2));

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
