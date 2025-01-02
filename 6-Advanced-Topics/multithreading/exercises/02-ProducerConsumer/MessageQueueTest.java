/**
 * Tests for thread-safe message queue implementation
 */
public class MessageQueueTest {
    private MessageQueue<String> queue;
    private ExecutorService executor;
    
    @BeforeEach
    void setUp() {
        queue = new MessageQueue<>(10);
        executor = Executors.newFixedThreadPool(4);
    }
    
    @AfterEach
    void tearDown() {
        executor.shutdown();
    }
    
    @Test
    void testBasicOperations() throws InterruptedException {
        assertTrue(queue.offer("test", 1, TimeUnit.SECONDS));
        assertEquals("test", queue.poll(1, TimeUnit.SECONDS));
        assertNull(queue.poll(100, TimeUnit.MILLISECONDS));
    }
    
    @Test
    void testProducerConsumer() throws InterruptedException {
        int messageCount = 1000;
        CountDownLatch producerLatch = new CountDownLatch(messageCount);
        CountDownLatch consumerLatch = new CountDownLatch(messageCount);
        AtomicInteger consumed = new AtomicInteger();
        
        // Producer
        executor.submit(() -> {
            try {
                for (int i = 0; i < messageCount; i++) {
                    if (queue.offer("Message-" + i, 1, TimeUnit.SECONDS)) {
                        producerLatch.countDown();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Consumer
        executor.submit(() -> {
            try {
                while (consumed.get() < messageCount) {
                    String message = queue.poll(1, TimeUnit.SECONDS);
                    if (message != null) {
                        consumed.incrementAndGet();
                        consumerLatch.countDown();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        assertTrue(producerLatch.await(10, TimeUnit.SECONDS));
        assertTrue(consumerLatch.await(10, TimeUnit.SECONDS));
        assertEquals(messageCount, consumed.get());
    }
    
    @Test
    void testMultipleProducersConsumers() throws InterruptedException {
        int producerCount = 3;
        int consumerCount = 2;
        int messagesPerProducer = 100;
        int totalMessages = producerCount * messagesPerProducer;
        
        CountDownLatch producerLatch = new CountDownLatch(totalMessages);
        CountDownLatch consumerLatch = new CountDownLatch(totalMessages);
        AtomicInteger consumed = new AtomicInteger();
        
        // Producers
        for (int p = 0; p < producerCount; p++) {
            final int producerId = p;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < messagesPerProducer; i++) {
                        if (queue.offer(
                            "Producer-" + producerId + "-Message-" + i, 
                            1, TimeUnit.SECONDS)) {
                            producerLatch.countDown();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Consumers
        for (int c = 0; c < consumerCount; c++) {
            executor.submit(() -> {
                try {
                    while (consumed.get() < totalMessages) {
                        String message = queue.poll(1, TimeUnit.SECONDS);
                        if (message != null) {
                            consumed.incrementAndGet();
                            consumerLatch.countDown();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        assertTrue(producerLatch.await(10, TimeUnit.SECONDS));
        assertTrue(consumerLatch.await(10, TimeUnit.SECONDS));
        assertEquals(totalMessages, consumed.get());
    }
    
    @Test
    void testDrainTo() throws InterruptedException {
        // Fill the queue
        for (int i = 0; i < 5; i++) {
            queue.offer("Message-" + i, 1, TimeUnit.SECONDS);
        }
        
        List<String> drained = new ArrayList<>();
        int count = queue.drainTo(drained, 3);
        
        assertEquals(3, count);
        assertEquals(3, drained.size());
        assertEquals(2, queue.size());
    }
    
    @Test
    void testQueueStats() throws InterruptedException {
        // Produce messages
        for (int i = 0; i < 5; i++) {
            queue.offer("Message-" + i, 1, TimeUnit.SECONDS);
        }
        
        // Consume some messages
        queue.poll(1, TimeUnit.SECONDS);
        queue.poll(1, TimeUnit.SECONDS);
        
        // Try to produce to full queue
        while (queue.offer("Extra", 100, TimeUnit.MILLISECONDS)) {
            // Keep trying to fill the queue
        }
        
        // Try to consume from empty queue
        while (queue.poll(100, TimeUnit.MILLISECONDS) != null) {
            // Keep consuming
        }
        
        QueueStats stats = queue.getStats();
        assertTrue(stats.getProducedMessages() >= 5);
        assertTrue(stats.getConsumedMessages() >= 2);
        assertTrue(stats.getRejectedMessages() > 0);
        assertTrue(stats.getFailedPolls() > 0);
    }
} 