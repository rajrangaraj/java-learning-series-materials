public class Library {
    private String name;
    private Map<String, LibraryItem> items;
    private Map<String, Member> members;
    private List<BorrowRecord> borrowRecords;
    
    public Library(String name) {
        this.name = name;
        this.items = new HashMap<>();
        this.members = new HashMap<>();
        this.borrowRecords = new ArrayList<>();
    }

    // Item management
    public void addItem(LibraryItem item) {
        items.put(item.getItemId(), item);
    }

    public void removeItem(String itemId) throws LibraryException {
        LibraryItem item = items.get(itemId);
        if (item == null) {
            throw new LibraryException("Item not found");
        }
        if (!item.isAvailable()) {
            throw new LibraryException("Cannot remove borrowed item");
        }
        items.remove(itemId);
    }

    // Member management
    public void addMember(Member member) {
        members.put(member.getMemberId(), member);
    }

    public void removeMember(String memberId) throws LibraryException {
        Member member = members.get(memberId);
        if (member == null) {
            throw new LibraryException("Member not found");
        }
        if (!member.getBorrowedItems().isEmpty()) {
            throw new LibraryException("Member has borrowed items");
        }
        members.remove(memberId);
    }

    // Borrowing operations
    public void checkoutItem(String memberId, String itemId) 
            throws LibraryException {
        Member member = members.get(memberId);
        LibraryItem item = items.get(itemId);
        
        if (member == null || item == null) {
            throw new LibraryException("Invalid member or item ID");
        }
        
        member.borrowItem(item);
        BorrowRecord record = new BorrowRecord(item, member);
        borrowRecords.add(record);
    }

    public void returnItem(String memberId, String itemId) 
            throws LibraryException {
        Member member = members.get(memberId);
        LibraryItem item = items.get(itemId);
        
        if (member == null || item == null) {
            throw new LibraryException("Invalid member or item ID");
        }
        
        member.returnItem(item);
    }

    // Search methods
    public List<LibraryItem> searchByTitle(String title) {
        return items.values().stream()
            .filter(item -> item.getTitle().toLowerCase()
                              .contains(title.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<LibraryItem> searchByAuthor(String author) {
        return items.values().stream()
            .filter(item -> item instanceof Book)
            .map(item -> (Book) item)
            .filter(book -> book.getAuthor().toLowerCase()
                              .contains(author.toLowerCase()))
            .collect(Collectors.toList());
    }

    // Reports
    public List<LibraryItem> getOverdueItems() {
        return items.values().stream()
            .filter(item -> !item.isAvailable() && item.calculateDaysLate() > 0
    }
} 