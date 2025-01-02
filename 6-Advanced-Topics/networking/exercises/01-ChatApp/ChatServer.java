/**
 * Multi-threaded chat server implementation
 */
public class ChatServer {
    private final int port;
    private final Set<ClientHandler> clients;
    private final ExecutorService executor;
    private final ServerStats stats;
    private volatile boolean running;
    
    public ChatServer(int port) {
        this.port = port;
        this.clients = ConcurrentHashMap.newKeySet();
        this.executor = Executors.newCachedThreadPool();
        this.stats = new ServerStats();
        this.running = true;
    }
    
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server started on port " + port);
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket);
                    clients.add(handler);
                    executor.execute(handler);
                    stats.recordConnection();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                        stats.recordError();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            stats.recordError();
        } finally {
            shutdown();
        }
    }
    
    public void shutdown() {
        running = false;
        clients.forEach(ClientHandler::disconnect);
        clients.clear();
        executor.shutdownNow();
    }
    
    private void broadcast(String message, ClientHandler sender) {
        clients.forEach(client -> {
            if (client != sender) {
                client.sendMessage(message);
            }
        });
        stats.recordMessage();
    }
    
    public ServerStats getStats() {
        return stats;
    }
    
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final BufferedReader in;
        private final PrintWriter out;
        private String username;
        
        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }
        
        @Override
        public void run() {
            try {
                // Handle client authentication
                handleAuthentication();
                
                // Process messages
                String message;
                while (running && (message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    }
                    broadcast(username + ": " + message, this);
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
                stats.recordError();
            } finally {
                disconnect();
            }
        }
        
        private void handleAuthentication() throws IOException {
            out.println("Please enter your username:");
            username = in.readLine();
            broadcast(username + " has joined the chat", this);
            stats.recordAuthentication();
        }
        
        public void sendMessage(String message) {
            out.println(message);
        }
        
        public void disconnect() {
            try {
                if (username != null) {
                    broadcast(username + " has left the chat", this);
                }
                clients.remove(this);
                socket.close();
                stats.recordDisconnection();
            } catch (IOException e) {
                System.err.println("Error disconnecting client: " + e.getMessage());
                stats.recordError();
            }
        }
    }
} 