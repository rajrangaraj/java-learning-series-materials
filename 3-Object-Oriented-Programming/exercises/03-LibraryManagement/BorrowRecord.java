public class BorrowRecord {
    private String recordId;
    private LibraryItem item;
    private Member member;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private double lateFee;
    
    private static int recordCounter = 1000;

    public BorrowRecord(LibraryItem item, Member member) {
        this.recordId = generateRecordId();
        this.item = item;
        this.member = member;
        this.borrowDate = LocalDateTime.now();
        this.lateFee = 0.0;
    }

    private String generateRecordId() {
        return String.format("BOR%04d", ++recordCounter);
    }

    // Getters and setters
    public String getRecordId() { return recordId; }
    public LibraryItem getItem() { return item; }
    public Member getMember() { return member; }
    public LocalDateTime getBorrowDate() { return borrowDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public double getLateFee() { return lateFee; }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public void setLateFee(double lateFee) {
        this.lateFee = lateFee;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public long getBorrowDuration() {
        LocalDateTime endDate = returnDate != null ? returnDate : LocalDateTime.now();
        return ChronoUnit.DAYS.between(borrowDate, endDate);
    }

    @Override
    public String toString() {
        return String.format("BorrowRecord[id=%s, item=%s, member=%s, duration=%d days]",
            recordId, item.getItemId(), member.getMemberId(), getBorrowDuration());
    }
} 