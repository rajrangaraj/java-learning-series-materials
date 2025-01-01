public class LibraryException extends Exception {
    private String errorCode;
    private String itemId;
    private String memberId;

    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public LibraryException(String message, String itemId, String memberId) {
        super(message);
        this.itemId = itemId;
        this.memberId = memberId;
    }

    public String getErrorCode() { return errorCode; }
    public String getItemId() { return itemId; }
    public String getMemberId() { return memberId; }
} 