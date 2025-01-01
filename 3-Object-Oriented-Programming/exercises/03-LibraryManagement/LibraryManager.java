/**
 * Main class to manage library operations
 */
public class LibraryManager {
    private Map<String, LibraryItem> inventory;
    private Map<String, Member> members;
    private List<BorrowRecord> borrowRecords;
    
    public LibraryManager() {
        inventory = new HashMap<>();
        members = new HashMap<>();
        borrowRecords = new ArrayList<>();
    }
    
    public void addItem(LibraryItem item) {
        if (inventory.containsKey(item.getItemId())) {
            throw new IllegalArgumentException("Item already exists in inventory");
        }
        inventory.put(item.getItemId(), item);
    }
    
    public void registerMember(Member member) {
        if (members.containsKey(member.getMemberId())) {
            throw new IllegalArgumentException("Member already registered");
        }
        members.put(member.getMemberId(), member);
    }
    
    public BorrowRecord checkoutItem(String memberId, String itemId) 
            throws LibraryException {
        Member member = members.get(memberId);
        LibraryItem item = inventory.get(itemId);
        
        // Validate member and item
        if (member == null) {
            throw new LibraryException("Member not found", "ERR_MEMBER_NOT_FOUND");
        }
        if (item == null) {
            throw new LibraryException("Item not found", "ERR_ITEM_NOT_FOUND");
        }
        
        // Check member eligibility
        if (!member.canBorrow()) {
            throw new LibraryException(
                "Member not eligible to borrow", 
                "ERR_MEMBER_NOT_ELIGIBLE"
            );
        }
        
        // Check item availability
        if (!item.isAvailable()) {
            throw new LibraryException(
                "Item not available", 
                "ERR_ITEM_NOT_AVAILABLE"
            );
        }
        
        // Process checkout
        member.borrowItem(item);
        BorrowRecord record = new BorrowRecord(item, member);
        borrowRecords.add(record);
        
        return record;
    }
    
    public void returnItem(String memberId, String itemId) throws LibraryException {
        Member member = members.get(memberId);
        LibraryItem item = inventory.get(itemId);
        
        // Validate member and item
        if (member == null || item == null) {
            throw new LibraryException(
                "Invalid member or item ID",
                itemId,
                memberId
            );
        }
        
        // Process return
        member.returnItem(item);
        
        // Update latest borrow record
        BorrowRecord record = findLatestBorrowRecord(itemId, memberId);
        if (record != null) {
            record.setReturnDate(LocalDateTime.now());
            
            // Calculate and set late fees if applicable
            int daysLate = item.calculateDaysLate();
            if (daysLate > 0) {
                double lateFee = item.calculateLateFee(daysLate);
                record.setLateFee(lateFee);
            }
        }
    }
    
    public List<LibraryItem> getOverdueItems() {
        return inventory.values().stream()
            .filter(item -> !item.isAvailable() && item.calculateDaysLate() > 0)
            .collect(Collectors.toList());
    }
    
    public void generateReports() {
        System.out.println(generateInventoryReport());
        System.out.println(generateMembershipReport());
        System.out.println(generateOverdueReport());
    }
    
    private String generateInventoryReport() {
        StringBuilder report = new StringBuilder();
        report.append("Inventory Report\n");
        report.append("================\n");
        
        // Count items by type
        Map<String, Long> itemCounts = inventory.values().stream()
            .collect(Collectors.groupingBy(
                item -> item.getClass().getSimpleName(),
                Collectors.counting()
            ));
        
        report.append(String.format("Total Items: %d\n", inventory.size()));
        itemCounts.forEach((type, count) -> 
            report.append(String.format("%s: %d\n", type, count)));
        
        // Available vs. borrowed items
        long availableCount = inventory.values().stream()
            .filter(LibraryItem::isAvailable)
            .count();
        
        report.append(String.format("\nAvailable Items: %d\n", availableCount));
        report.append(String.format("Borrowed Items: %d\n", 
            inventory.size() - availableCount));
        
        return report.toString();
    }
    
    private String generateMembershipReport() {
        StringBuilder report = new StringBuilder();
        report.append("Membership Report\n");
        report.append("=================\n");
        
        report.append(String.format("Total Members: %d\n", members.size()));
        
        // Members by status
        Map<MembershipStatus, Long> statusCounts = members.values().stream()
            .collect(Collectors.groupingBy(
                Member::getStatus,
                Collectors.counting()
            ));
        
        statusCounts.forEach((status, count) ->
            report.append(String.format("%s: %d\n", status, count)));
        
        // Members with fines
        List<Member> membersWithFines = members.values().stream()
            .filter(m -> m.getFineBalance() > 0)
            .collect(Collectors.toList());
        
        report.append(String.format("\nMembers with Fines: %d\n", 
            membersWithFines.size()));
        
        double totalFines = membersWithFines.stream()
            .mapToDouble(Member::getFineBalance)
            .sum();
        
        report.append(String.format("Total Outstanding Fines: $%.2f\n", 
            totalFines));
        
        return report.toString();
    }
    
    private String generateOverdueReport() {
        StringBuilder report = new StringBuilder();
        report.append("Overdue Items Report\n");
        report.append("===================\n");
        
        List<LibraryItem> overdueItems = getOverdueItems();
        report.append(String.format("Total Overdue Items: %d\n\n", 
            overdueItems.size()));
        
        if (!overdueItems.isEmpty()) {
            overdueItems.forEach(item -> {
                Member borrower = item.getBorrowedBy();
                int daysLate = item.calculateDaysLate();
                double lateFee = item.calculateLateFee(daysLate);
                
                report.append(String.format(
                    "Item: %s (%s)\n" +
                    "Borrowed by: %s (%s)\n" +
                    "Days Late: %d\n" +
                    "Late Fee: $%.2f\n\n",
                    item.getTitle(),
                    item.getItemId(),
                    borrower.getName(),
                    borrower.getMemberId(),
                    daysLate,
                    lateFee
                ));
            });
        }
        
        return report.toString();
    }
    
    private BorrowRecord findLatestBorrowRecord(String itemId, String memberId) {
        return borrowRecords.stream()
            .filter(r -> r.getItem().getItemId().equals(itemId) &&
                        r.getMember().getMemberId().equals(memberId))
            .max(Comparator.comparing(BorrowRecord::getBorrowDate))
            .orElse(null);
    }
    
    // Getters for testing and external access
    public Map<String, LibraryItem> getInventory() {
        return new HashMap<>(inventory);
    }
    
    public Map<String, Member> getMembers() {
        return new HashMap<>(members);
    }
    
    public List<BorrowRecord> getBorrowRecords() {
        return new ArrayList<>(borrowRecords);
    }
} 