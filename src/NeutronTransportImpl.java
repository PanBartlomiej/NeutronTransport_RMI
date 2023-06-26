import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.log;

public class NeutronTransportImpl extends UnicastRemoteObject implements NeutronTransportInterface {
    private static final int NUM_WORKERS = 16;
    private static final long ITERATIONS_PER_WORKER = 1000000L;

    private long reflectedParticles=0;
    private long capturedParticles=0;
    private long passedParticles=0;
    private long totalIterations=0;

    public NeutronTransportImpl() throws RemoteException {
        super();
    }

    public void performMonteCarloSimulation(long iter, NeutronTransportParam params) throws RemoteException {
        reflectedParticles=0;
        capturedParticles=0;
        passedParticles=0;
        totalIterations=0;

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_WORKERS);

        for (int i = 0; i < NUM_WORKERS; i++) {
            executorService.execute(new MonteCarloWorker(iter, params));
        }

        executorService.shutdown();

        while (!executorService.isTerminated()) {
            // Czekaj na zakończenie wszystkich wątków
        }
    }

    public long getReflectedParticles() throws RemoteException {
        return reflectedParticles;
    }

    public long getCapturedParticles() throws RemoteException {
        return capturedParticles;
    }

    public long getPassedParticles() throws RemoteException {
        return passedParticles;
    }

    @Override
    public long getIteration() throws RemoteException {
        return totalIterations;
    }

    private class MonteCarloWorker implements Runnable {



        private long iterations;
        NeutronTransportParam params;

        public MonteCarloWorker(long iterations, NeutronTransportParam params) {
            this.iterations = iterations;
            this.params=params;
            totalIterations=0;
        }

        @Override
        public void run() {

            long reflected = 0;
            long captured = 0;
            long passed = 0;
            Random random = new Random();

            // Wykonaj symulację Monte Carlo dla określonej liczby iteracji
            for (long i = 0; i < iterations; i++) {
                Result result = singleNeutronTransport(params, random);
                switch (result) {
                    case Reflected:
                        reflected++;
                        break;
                    case Captured:
                        captured++;
                        break;
                    case Passed:
                        passed++;
                        break;

                }

            }

            // Zaktualizuj globalne liczniki
            synchronized (NeutronTransportImpl.this) {
                reflectedParticles += reflected;
                capturedParticles += captured;
                passedParticles += passed;
                totalIterations += iterations;
            }
        }
        public Result singleNeutronTransport(NeutronTransportParam params, Random stream) {
            double neutron_pos = 0;
            double u = stream.nextDouble();
            double L = calculateDistance(params.C, u);
            double D = stream.nextDouble() * Math.PI;
            double capturedValue = 0;

            neutron_pos += L * Math.cos(D);

            while (true) {
                capturedValue = stream.nextDouble() * params.C;
                if (capturedValue <= params.Cc) {
                    return Result.Captured;
                }
                if (neutron_pos < 0) {
                    return Result.Reflected;
                } else if (neutron_pos > params.H) {
                    return Result.Passed;
                }

                u = stream.nextDouble();
                L = calculateDistance(params.C, u);
                D = stream.nextDouble() * Math.PI;
                neutron_pos += L * Math.cos(D);
            }
        }

        double calculateDistance(double C, double u) {
            return -1.0 / C * log(u);
        }


    }

}
