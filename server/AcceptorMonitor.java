package server;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class AcceptorMonitor implements Runnable {

    private final List<Thread> acceptorThreads;
    private final int restartDelay; // Delay in milliseconds between restarts
    private volatile boolean running = true; // Flag to control loop termination


    public AcceptorMonitor(List<Thread> acceptorThreads, int restartDelay) {
        this.acceptorThreads = acceptorThreads;
        this.restartDelay = restartDelay;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (int i = 0; i < acceptorThreads.size(); i++) {
                    Thread acceptorThread = acceptorThreads.get(i);
                    if (!acceptorThread.isAlive()) {
                        System.err.println("Acceptor " + i + " failed. Restarting...");
                        int failureRate = 10; // Set desired failure rate (0-100)
                        Acceptor acceptor = new AcceptorImpl(failureRate);
                        Thread newAcceptorThread = new Thread(acceptor);
                        newAcceptorThread.start();
                        acceptorThreads.set(i, newAcceptorThread);
                    }
                }
                Thread.sleep(restartDelay);
            } catch (InterruptedException e) {
                System.err.println("AcceptorMonitor interrupted. Stopping...");
                running = false; // Set flag to stop the loop
            }
        }
    }
}
