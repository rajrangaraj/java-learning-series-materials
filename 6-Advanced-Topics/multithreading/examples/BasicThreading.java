/**
 * Demonstrates basic thread creation and management
 */
public class BasicThreading {
    
    public static void main(String[] args) {
        // Creating thread using Thread class
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 1: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread 1 interrupted");
                    return;
                }
            }
        });
        
        // Creating thread using Runnable
        Runnable runnable = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 2: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread 2 interrupted");
                    return;
                }
            }
        };
        Thread thread2 = new Thread(runnable);
        
        // Thread lifecycle demonstration
        System.out.println("Starting threads...");
        thread1.start();
        thread2.start();
        
        try {
            // Wait for threads to complete
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread interrupted");
        }
        
        System.out.println("All threads completed");
        
        // Thread state demonstration
        Thread stateThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        System.out.println("New thread state: " + stateThread.getState());
        stateThread.start();
        System.out.println("Running thread state: " + stateThread.getState());
        
        try {
            Thread.sleep(100);
            System.out.println("Sleeping thread state: " + stateThread.getState());
            stateThread.join();
            System.out.println("Terminated thread state: " + stateThread.getState());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Custom thread class example
    static class CustomThread extends Thread {
        @Override
        public void run() {
            System.out.println("Custom thread running");
        }
    }
} 