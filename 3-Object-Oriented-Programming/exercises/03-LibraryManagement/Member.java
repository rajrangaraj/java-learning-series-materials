public class Member {
    private String memberId;
    private String name;
    private String email;
    private String phone;
    private LocalDate joinDate;
    private MembershipStatus status;
    private List<LibraryItem> borrowedItems;
    private List<BorrowRecord> borrowHistory;
    private double fineBalance;
    
    private static int memberCounter = 1000;
    private static final int MAX_BORROWED_ITEMS = 5;

    public Member(String name, String email, String phone) {
        this.memberId = generateMemberId();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.joinDate = LocalDate.now();
        this.status = MembershipStatus.ACTIVE;
        this.borrowedItems = new ArrayList<>();
        this.borrowHistory = new ArrayList<>();
        this.fineBalance = 0.0;
    }

    private String generateMemberId() {
        return String.format("MEM%04d", ++memberCounter);
    }

    public boolean canBorrow() {
        return status == MembershipStatus.ACTIVE &&
               borrowedItems.size() < MAX_BORROWED_ITEMS &&
               fineBalance < 10.0;
    }

    public void borrowItem(LibraryItem item) throws LibraryException {
        if (!canBorrow()) {
            throw new LibraryException("Member cannot borrow more items");
        }
        item.borrowItem(this);
        borrowedItems.add(item);
    }

    public void returnItem(LibraryItem item) throws LibraryException {
        if (!borrowedItems.contains(item)) {
            throw new LibraryException("Item not borrowed by this member");
        }
        
        item.returnItem();
        borrowedItems.remove(item);
        
        // Update fine balance if returned late
        BorrowRecord record = item.getBorrowHistory().get(
            item.getBorrowHistory().size() - 1
        );
        if (record.getLateFee() > 0) {
            fineBalance += record.getLateFee();
        }
    }

    public void payFine(double amount) {
        if (amount > fineBalance) {
            throw new IllegalArgumentException("Payment amount exceeds fine balance");
        }
        fineBalance -= amount;
    }

    // Getters and setters
    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDate getJoinDate() { return joinDate; }
    public MembershipStatus getStatus() { return status; }
    public double getFineBalance() { return fineBalance; }
    public List<LibraryItem> getBorrowedItems() { 
        return new ArrayList<>(borrowedItems); 
    }
    public List<BorrowRecord> getBorrowHistory() { 
        return new ArrayList<>(borrowHistory); 
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Member[id=%s, name=%s, status=%s, borrowed=%d]",
            memberId, name, status, borrowedItems.size());
    }
} 