/**
 * Generic directed graph implementation
 */
public class GenericGraph<T> {
    private class Vertex {
        T data;
        Map<Vertex, Integer> neighbors; // neighbor -> weight
        
        Vertex(T data) {
            this.data = data;
            this.neighbors = new HashMap<>();
        }
    }
    
    private Map<T, Vertex> vertices;
    
    public GenericGraph() {
        vertices = new HashMap<>();
    }
    
    public void addVertex(T data) {
        vertices.putIfAbsent(data, new Vertex(data));
    }
    
    public void addEdge(T source, T destination, int weight) {
        Vertex sourceVertex = vertices.get(source);
        Vertex destVertex = vertices.get(destination);
        
        if (sourceVertex == null || destVertex == null) {
            throw new IllegalArgumentException("Vertices not found");
        }
        
        sourceVertex.neighbors.put(destVertex, weight);
    }
    
    public List<T> shortestPath(T start, T end) {
        Vertex startVertex = vertices.get(start);
        Vertex endVertex = vertices.get(end);
        
        if (startVertex == null || endVertex == null) {
            return Collections.emptyList();
        }
        
        Map<Vertex, Integer> distances = new HashMap<>();
        Map<Vertex, Vertex> previousVertices = new HashMap<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>(
            Comparator.comparingInt(v -> distances.getOrDefault(v, Integer.MAX_VALUE))
        );
        
        distances.put(startVertex, 0);
        queue.offer(startVertex);
        
        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            
            if (current == endVertex) {
                break;
            }
            
            for (Map.Entry<Vertex, Integer> neighbor : current.neighbors.entrySet()) {
                int newDistance = distances.get(current) + neighbor.getValue();
                
                if (newDistance < distances.getOrDefault(neighbor.getKey(), Integer.MAX_VALUE)) {
                    distances.put(neighbor.getKey(), newDistance);
                    previousVertices.put(neighbor.getKey(), current);
                    queue.offer(neighbor.getKey());
                }
            }
        }
        
        return buildPath(startVertex, endVertex, previousVertices);
    }
    
    private List<T> buildPath(Vertex start, Vertex end, Map<Vertex, Vertex> previousVertices) {
        LinkedList<T> path = new LinkedList<>();
        Vertex current = end;
        
        while (current != null) {
            path.addFirst(current.data);
            current = previousVertices.get(current);
        }
        
        return path;
    }
} 