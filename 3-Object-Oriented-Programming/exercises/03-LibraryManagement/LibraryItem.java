public abstract class LibraryItem {
    private String title;
    private String itemId;
    private boolean isAvailable;
    private LocalDate dueDate;
    private Member borrowedBy;
    private List<BorrowRecord> borrowHistory;
    
    private static int itemCounter = 1000;

    public LibraryItem(String title) {
        this.title = title;
        this.itemId = generateItemId();
        this.isAvailable = true;
        this.borrowHistory = new ArrayList<>();
    }

    private String generateItemId() {
        return String.format("LIB%04d", ++itemCounter);
    }

    // Abstract methods
    public abstract double calculateLateFee(int daysLate);
    public abstract int getMaxBorrowDays();

    // Borrowing operations
    public void borrowItem(Member member) throws LibraryException {
        if (!isAvailable) {
            throw new LibraryException("Item is not available");
        }
        
        isAvailable = false;
        borrowedBy = member;
        dueDate = LocalDate.now().plusDays(getMaxBorrowDays());
        
        BorrowRecord record = new BorrowRecord(this, member);
        borrowHistory.add(record);
    }

    public void returnItem() throws LibraryException {
        if (isAvailable) {
            throw new LibraryException("Item is already returned");
        }

        BorrowRecord currentBorrow = borrowHistory.get(borrowHistory.size() - 1);
        currentBorrow.setReturnDate(LocalDate.now());

        int daysLate = calculateDaysLate();
        if (daysLate > 0) {
            double lateFee = calculateLateFee(daysLate);
            currentBorrow.setLateFee(lateFee);
        }

        isAvailable = true;
        borrowedBy = null;
        dueDate = null;
    }

    public int calculateDaysLate() {
        if (dueDate == null || isAvailable) return 0;
        
        long daysLate = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return daysLate > 0 ? (int) daysLate : 0;
    }

    // Getters
    public String getTitle() { return title; }
    public String getItemId() { return itemId; }
    public boolean isAvailable() { return isAvailable; }
    public LocalDate getDueDate() { return dueDate; }
    public Member getBorrowedBy() { return borrowedBy; }
    public List<BorrowRecord> getBorrowHistory() { 
        return new ArrayList<>(borrowHistory); 
    }

    @Override
    public String toString() {
        return String.format("LibraryItem[id=%s, title=%s, available=%b]",
            itemId, title, isAvailable);
    }
} 