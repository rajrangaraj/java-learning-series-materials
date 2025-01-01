# Collection Performance Comparisons

## List Implementations

### ArrayList
- **Advantages**
  - O(1) random access
  - Memory efficient
  - Good for iteration
- **Disadvantages**
  - O(n) insertion/deletion in middle
  - Resizing cost
  
### LinkedList
- **Advantages**
  - O(1) insertion/deletion at ends
  - No resizing cost
  - Good for queue operations
- **Disadvantages**
  - O(n) random access
  - More memory overhead
  
## Set Implementations

### HashSet
- **Advantages**
  - O(1) add/remove/contains
  - Memory efficient
- **Disadvantages**
  - No ordering
  - Hash collisions
  
### TreeSet
- **Advantages**
  - Ordered elements
  - O(log n) operations
- **Disadvantages**
  - More memory usage
  - Slower than HashSet
  
## Map Implementations

### HashMap
- **Advantages**
  - O(1) operations
  - Null keys/values
- **Disadvantages**
  - No ordering
  - Hash collisions
  
### TreeMap
- **Advantages**
  - Ordered keys
  - Range operations
- **Disadvantages**
  - O(log n) operations
  - No null keys

## Operation Costs

| Operation | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|-----------|-----------|------------|---------|---------|---------|---------|
| Add       | O(1)*     | O(1)       | O(1)    | O(log n)| O(1)    | O(log n)|
| Remove    | O(n)      | O(1)**     | O(1)    | O(log n)| O(1)    | O(log n)|
| Contains  | O(n)      | O(n)       | O(1)    | O(log n)| O(1)    | O(log n)|
| Get       | O(1)      | O(n)       | O(1)    | O(log n)| O(1)    | O(log n)|

\* Amortized
\** When position is known

## Memory Usage

| Collection | Memory Overhead |
|------------|----------------|
| ArrayList  | Low            |
| LinkedList | High           |
| HashSet    | Medium         |
| TreeSet    | High           |
| HashMap    | Medium         |
| TreeMap    | High           |

## Choosing the Right Collection

1. Need ordered elements? → TreeSet/TreeMap
2. Need fast lookups? → HashSet/HashMap
3. Need random access? → ArrayList
4. Need fast insertions? → LinkedList
5. Need unique elements? → Set
6. Need key-value pairs? → Map 