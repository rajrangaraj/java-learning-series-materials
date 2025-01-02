/**
 * Demonstrates basic socket communication
 */
public class BasicSocketExample {
    
    public static void main(String[] args) throws IOException {
        // Start server in a separate thread
        Thread serverThread = new Thread(() -> {
            try {
                runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        
        // Give server time to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        
        // Run client
        runClient();
    }
    
    private static void runServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server listening on port 8080...");
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(
                         new InputStreamReader(clientSocket.getInputStream()))) {
                    
                    System.out.println("Client connected from: " + 
                        clientSocket.getInetAddress());
                    
                    // Read client message
                    String message = in.readLine();
                    System.out.println("Received from client: " + message);
                    
                    // Send response
                    String response = "Server received: " + message;
                    out.println(response);
                    
                    // Exit if client sends "exit"
                    if ("exit".equalsIgnoreCase(message)) {
                        break;
                    }
                }
            }
        }
    }
    
    private static void runClient() {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(
                 new InputStreamReader(System.in))) {
            
            System.out.println("Connected to server. Type messages (type 'exit' to quit):");
            
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                // Send message to server
                out.println(userInput);
                
                // Read server's response
                String response = in.readLine();
                System.out.println("Server response: " + response);
                
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 