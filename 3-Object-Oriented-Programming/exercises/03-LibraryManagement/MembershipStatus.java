public enum MembershipStatus {
    ACTIVE("Active member"),
    SUSPENDED("Membership suspended"),
    EXPIRED("Membership expired"),
    CANCELLED("Membership cancelled");

    private final String description;

    MembershipStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 