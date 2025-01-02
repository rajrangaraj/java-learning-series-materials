/**
 * Demonstrates thread synchronization mechanisms
 */
public class ThreadSynchronization {
    private int counter = 0;
    private final Object lock = new Object();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final Semaphore semaphore = new Semaphore(2);
    
    public static void main(String[] args) throws InterruptedException {
        ThreadSynchronization demo = new ThreadSynchronization();
        
        // Synchronized method demo
        demo.demonstrateSynchronizedMethod();
        
        // Synchronized block demo
        demo.demonstrateSynchronizedBlock();
        
        // ReentrantLock demo
        demo.demonstrateReentrantLock();
        
        // Semaphore demo
        demo.demonstrateSemaphore();
        
        // Wait/Notify demo
        demo.demonstrateWaitNotify();
    }
    
    private void demonstrateSynchronizedMethod() throws InterruptedException {
        counter = 0;
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(this::incrementSynchronized);
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Synchronized method counter: " + counter);
    }
    
    private synchronized void incrementSynchronized() {
        for (int i = 0; i < 1000; i++) {
            counter++;
        }
    }
    
    private void demonstrateSynchronizedBlock() throws InterruptedException {
        counter = 0;
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    synchronized (lock) {
                        counter++;
                    }
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Synchronized block counter: " + counter);
    }
    
    private void demonstrateReentrantLock() throws InterruptedException {
        counter = 0;
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    reentrantLock.lock();
                    try {
                        counter++;
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("ReentrantLock counter: " + counter);
    }
    
    private void demonstrateSemaphore() throws InterruptedException {
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " acquired permit");
                    Thread.sleep(100);
                    System.out.println(Thread.currentThread().getName() + " releasing permit");
                    semaphore.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
    }
    
    private void demonstrateWaitNotify() throws InterruptedException {
        Object sharedObject = new Object();
        boolean[] done = {false};
        
        Thread waiter = new Thread(() -> {
            synchronized (sharedObject) {
                while (!done[0]) {
                    try {
                        System.out.println("Waiter waiting...");
                        sharedObject.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("Waiter notified!");
            }
        });
        
        Thread notifier = new Thread(() -> {
            synchronized (sharedObject) {
                try {
                    Thread.sleep(1000);
                    done[0] = true;
                    System.out.println("Notifier notifying...");
                    sharedObject.notify();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        waiter.start();
        notifier.start();
        
        waiter.join();
        notifier.join();
    }
} 