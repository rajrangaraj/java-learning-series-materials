/**
 * Chat client implementation with GUI
 */
public class ChatClient extends JFrame {
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final JTextArea chatArea;
    private final JTextField messageField;
    private final JButton sendButton;
    private volatile boolean connected;
    private String username;
    
    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
        
        // Setup GUI
        setTitle("Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        
        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });
    }
    
    public void connect() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            
            // Handle authentication
            String prompt = in.readLine();
            username = JOptionPane.showInputDialog(this, prompt);
            if (username == null || username.trim().isEmpty()) {
                disconnect();
                return;
            }
            out.println(username);
            
            // Start message receiver thread
            new Thread(this::receiveMessages).start();
            
        } catch (IOException e) {
            showError("Could not connect to server: " + e.getMessage());
        }
    }
    
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && connected) {
            out.println(message);
            messageField.setText("");
        }
    }
    
    private void receiveMessages() {
        try {
            String message;
            while (connected && (message = in.readLine()) != null) {
                final String finalMessage = message;
                SwingUtilities.invokeLater(() -> {
                    chatArea.append(finalMessage + "\n");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                });
            }
        } catch (IOException e) {
            if (connected) {
                showError("Lost connection to server: " + e.getMessage());
                disconnect();
            }
        }
    }
    
    private void disconnect() {
        connected = false;
        if (out != null) {
            out.println("/quit");
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(this, message, "Error", 
                JOptionPane.ERROR_MESSAGE));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatClient client = new ChatClient("localhost", 8080);
            client.setVisible(true);
            client.connect();
        });
    }
} 