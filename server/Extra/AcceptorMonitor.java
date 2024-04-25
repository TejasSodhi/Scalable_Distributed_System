// package server.paxos;

// import java.util.List;
// import java.util.ArrayList;
// import java.util.concurrent.TimeUnit;

// import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.TimeUnit;

// public class AcceptorMonitor implements Runnable {

//     private final List<Acceptor> acceptors;
//     private final int restartDelay; // Delay in milliseconds between restarts
//     private final ScheduledExecutorService scheduler;
//     private volatile boolean running = true; // Flag to control loop termination

//     public AcceptorMonitor(List<Acceptor> acceptors, int restartDelay, ScheduledExecutorService scheduler) {
//         this.acceptors = acceptors;
//         this.restartDelay = restartDelay;
//         this.scheduler = scheduler;
//     }

//     @Override
//     public void run() {
//         while (running) {
//             try {
//                 for (int i = 0; i < acceptors.size(); i++) {
//                     Acceptor acceptor = acceptors.get(i);
//                     if (!((Thread) acceptor).isAlive()) {
//                         System.err.println("Acceptor " + i + " failed. Restarting...");
//                         int failureRate = 10; // Set desired failure rate (0-100)
//                         Runnable restartTask = createRestartTask(i, failureRate);
//                         scheduler.schedule(restartTask, restartDelay, TimeUnit.MILLISECONDS);
//                     }
//                 }
//                 Thread.sleep(restartDelay); // Adjust as needed
//             } catch (InterruptedException e) {
//                 System.err.println("AcceptorMonitor interrupted. Stopping...");
//                 running = false; // Set flag to terminate the loop
//             }
//         }
//         scheduler.shutdown(); // Shutdown the scheduler when done
//     }

//     private Runnable createRestartTask(int index, int failureRate) {
//         return () -> {
//             Acceptor newAcceptor = new AcceptorImpl(failureRate);
//             List<Acceptor> updatedList = new ArrayList<>(acceptors); // Create a copy
//             if (!((Thread) updatedList.get(index)).isAlive()) {
//                 updatedList.set(index, newAcceptor);
//             }
//         };
//     }
// }


package server.paxos;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AcceptorMonitor implements Runnable {

    private final List<Acceptor> acceptors;
    private final int restartDelay; // Delay in milliseconds between restarts
    private final ScheduledExecutorService scheduler;
    private volatile boolean running = true; // Flag to control loop termination

    public AcceptorMonitor(final List<Acceptor> acceptors, int restartDelay, ScheduledExecutorService scheduler) {
        this.acceptors = acceptors;
        this.restartDelay = restartDelay;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void run() {
        while (running) {
            try {
                for (int i = 0; i < acceptors.size(); i++) {
                    Acceptor acceptor = acceptors.get(i);
                    if (!acceptor.isActive()) {
                        System.err.println("Acceptor " + i + " failed. Restarting...");
                        int failureRate = 10; // Set desired failure rate (0-100)
                        scheduler.schedule(() -> restartAcceptor(i, failureRate), restartDelay, TimeUnit.MILLISECONDS);
                    }
                }
                Thread.sleep(restartDelay); // Adjust as needed
            } catch (InterruptedException e) {
                System.err.println("AcceptorMonitor interrupted. Stopping...");
                running = false; // Set flag to terminate the loop
            }
        }
        scheduler.shutdown(); // Shutdown the scheduler when done
    }

    private void restartAcceptor(int index, int failureRate) {
        Acceptor newAcceptor = new AcceptorImpl(failureRate);
        acceptors.set(index, newAcceptor);
    }
}
