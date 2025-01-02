/**
 * Tests for chat server implementation
 */
public class ChatServerTest {
    private static final int TEST_PORT = 8081;
    private ChatServer server;
    private ExecutorService executor;
    
    @BeforeEach
    void setUp() {
        server = new ChatServer(TEST_PORT);
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> server.start());
        
        // Give server time to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @AfterEach
    void tearDown() {
        server.shutdown();
        executor.shutdownNow();
    }
    
    @Test
    void testClientConnection() throws IOException {
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            assertTrue(socket.isConnected());
            
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            String prompt = in.readLine();
            
            assertEquals("Please enter your username:", prompt);
        }
    }
    
    @Test
    void testClientAuthentication() throws IOException {
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            // Read prompt and send username
            in.readLine(); // Consume prompt
            out.println("TestUser");
            
            // Should receive join message
            String response = in.readLine();
            assertTrue(response.contains("TestUser has joined the chat"));
            
            ServerStats stats = server.getStats();
            assertEquals(1, stats.getTotalAuthentications());
        }
    }
    
    @Test
    void testMessageBroadcast() throws IOException {
        // Connect first client
        Socket client1 = new Socket("localhost", TEST_PORT);
        BufferedReader in1 = new BufferedReader(
            new InputStreamReader(client1.getInputStream()));
        PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
        
        // Connect second client
        Socket client2 = new Socket("localhost", TEST_PORT);
        BufferedReader in2 = new BufferedReader(
            new InputStreamReader(client2.getInputStream()));
        PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true);
        
        try {
            // Authenticate clients
            in1.readLine(); // Consume prompt
            out1.println("User1");
            in2.readLine(); // Consume prompt
            out2.println("User2");
            
            // Consume join messages
            in1.readLine(); // User1 joined
            in1.readLine(); // User2 joined
            in2.readLine(); // User1 joined
            in2.readLine(); // User2 joined
            
            // Send message from client1
            out1.println("Hello, everyone!");
            
            // Client2 should receive the message
            String received = in2.readLine();
            assertEquals("User1: Hello, everyone!", received);
            
            ServerStats stats = server.getStats();
            assertEquals(1, stats.getTotalMessages());
            
        } finally {
            client1.close();
            client2.close();
        }
    }
    
    @Test
    void testClientDisconnection() throws IOException {
        Socket socket = new Socket("localhost", TEST_PORT);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        // Authenticate
        in.readLine(); // Consume prompt
        out.println("TestUser");
        in.readLine(); // Consume join message
        
        // Connect another client to receive disconnect message
        Socket observer = new Socket("localhost", TEST_PORT);
        BufferedReader observerIn = new BufferedReader(
            new InputStreamReader(observer.getInputStream()));
        PrintWriter observerOut = new PrintWriter(observer.getOutputStream(), true);
        
        try {
            // Authenticate observer
            observerIn.readLine(); // Consume prompt
            observerOut.println("Observer");
            observerIn.readLine(); // Consume join messages
            
            // Disconnect first client
            socket.close();
            
            // Observer should receive disconnect message
            String disconnectMessage = observerIn.readLine();
            assertTrue(disconnectMessage.contains("TestUser has left the chat"));
            
        } finally {
            observer.close();
        }
    }
    
    @Test
    void testServerStats() throws IOException {
        // Connect and authenticate a client
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            in.readLine(); // Consume prompt
            out.println("TestUser");
            in.readLine(); // Consume join message
            
            // Send a message
            out.println("Test message");
            
            // Verify stats
            ServerStats stats = server.getStats();
            assertEquals(1, stats.getTotalConnections());
            assertEquals(1, stats.getCurrentConnections());
            assertEquals(1, stats.getTotalAuthentications());
            assertEquals(1, stats.getTotalMessages());
            assertTrue(stats.getUptime() > 0);
        }
        
        // After disconnection
        ServerStats stats = server.getStats();
        assertEquals(1, stats.getTotalConnections());
        assertEquals(0, stats.getCurrentConnections());
    }
    
    @Test
    void testConcurrentConnections() throws Exception {
        int numClients = 10;
        List<Socket> clients = new ArrayList<>();
        CountDownLatch connectLatch = new CountDownLatch(numClients);
        
        // Connect multiple clients simultaneously
        for (int i = 0; i < numClients; i++) {
            final int clientId = i;
            new Thread(() -> {
                try {
                    Socket socket = new Socket("localhost", TEST_PORT);
                    clients.add(socket);
                    
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    
                    in.readLine(); // Consume prompt
                    out.println("User" + clientId);
                    
                    connectLatch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        
        // Wait for all clients to connect
        assertTrue(connectLatch.await(5, TimeUnit.SECONDS));
        
        // Verify server stats
        ServerStats stats = server.getStats();
        assertEquals(numClients, stats.getTotalConnections());
        assertEquals(numClients, stats.getCurrentConnections());
        assertEquals(numClients, stats.getTotalAuthentications());
        
        // Cleanup
        for (Socket client : clients) {
            client.close();
        }
    }
} 