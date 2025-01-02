/**
 * Demonstrates HTTP client operations using HttpClient
 */
public class HttpClientExample {
    private final HttpClient client;
    
    public HttpClientExample() {
        this.client = HttpClient.newBuilder()
            .version(Version.HTTP_2)
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
    
    public void demonstrateGetRequest() throws IOException, InterruptedException {
        System.out.println("\n=== GET Request ===");
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/users/octocat"))
            .header("Accept", "application/json")
            .GET()
            .build();
        
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Headers: " + response.headers());
        System.out.println("Body: " + response.body());
    }
    
    public void demonstrateAsyncRequest() {
        System.out.println("\n=== Async Request ===");
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/users/octocat/repos"))
            .header("Accept", "application/json")
            .GET()
            .build();
        
        CompletableFuture<HttpResponse<String>> future = client.sendAsync(request,
            HttpResponse.BodyHandlers.ofString());
        
        future.thenAccept(response -> {
            System.out.println("Async status code: " + response.statusCode());
            System.out.println("Async body length: " + response.body().length());
        }).join();
    }
    
    public void demonstratePostRequest() throws IOException, InterruptedException {
        System.out.println("\n=== POST Request ===");
        
        String jsonBody = "{\"name\": \"test\", \"description\": \"test post\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://httpbin.org/post"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());
        
        System.out.println("POST Status: " + response.statusCode());
        System.out.println("POST Response: " + response.body());
    }
    
    public void demonstrateMultipleRequests() {
        System.out.println("\n=== Multiple Requests ===");
        
        List<URI> urls = Arrays.asList(
            URI.create("https://api.github.com/users/octocat"),
            URI.create("https://api.github.com/users/torvalds"),
            URI.create("https://api.github.com/users/defunkt")
        );
        
        List<CompletableFuture<String>> futures = urls.stream()
            .map(url -> HttpRequest.newBuilder(url)
                .header("Accept", "application/json")
                .GET()
                .build())
            .map(request -> client.sendAsync(request, 
                HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body))
            .collect(Collectors.toList());
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenRun(() -> {
                for (int i = 0; i < urls.size(); i++) {
                    try {
                        System.out.printf("Response %d: %s%n", 
                            i + 1, futures.get(i).get());
                    } catch (Exception e) {
                        System.err.println("Error getting response " + (i + 1));
                    }
                }
            })
            .join();
    }
    
    public void demonstrateFileDownload() throws IOException, InterruptedException {
        System.out.println("\n=== File Download ===");
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://raw.githubusercontent.com/octocat/Spoon-Knife/master/README.md"))
            .GET()
            .build();
        
        Path downloadPath = Files.createTempFile("download", ".md");
        
        HttpResponse<Path> response = client.send(request,
            HttpResponse.BodyHandlers.ofFile(downloadPath));
        
        System.out.println("File downloaded to: " + downloadPath);
        System.out.println("File size: " + Files.size(downloadPath) + " bytes");
        System.out.println("Content preview: " + 
            Files.readString(downloadPath).substring(0, 100) + "...");
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClientExample example = new HttpClientExample();
        
        example.demonstrateGetRequest();
        example.demonstrateAsyncRequest();
        example.demonstratePostRequest();
        example.demonstrateMultipleRequests();
        example.demonstrateFileDownload();
    }
} 