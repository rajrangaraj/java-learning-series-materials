/**
 * Generic binary tree implementation
 */
public class GenericTree<T extends Comparable<T>> {
    private class Node {
        T data;
        Node left;
        Node right;
        
        Node(T data) {
            this.data = data;
        }
    }
    
    private Node root;
    private int size;
    
    public void insert(T data) {
        root = insertRec(root, data);
        size++;
    }
    
    private Node insertRec(Node node, T data) {
        if (node == null) {
            return new Node(data);
        }
        
        if (data.compareTo(node.data) < 0) {
            node.left = insertRec(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = insertRec(node.right, data);
        }
        
        return node;
    }
    
    public boolean contains(T data) {
        return containsRec(root, data);
    }
    
    private boolean containsRec(Node node, T data) {
        if (node == null) {
            return false;
        }
        
        if (data.equals(node.data)) {
            return true;
        }
        
        return data.compareTo(node.data) < 0 
            ? containsRec(node.left, data)
            : containsRec(node.right, data);
    }
    
    public List<T> inorderTraversal() {
        List<T> result = new ArrayList<>();
        inorderRec(root, result);
        return result;
    }
    
    private void inorderRec(Node node, List<T> result) {
        if (node != null) {
            inorderRec(node.left, result);
            result.add(node.data);
            inorderRec(node.right, result);
        }
    }
    
    public int size() {
        return size;
    }
} 