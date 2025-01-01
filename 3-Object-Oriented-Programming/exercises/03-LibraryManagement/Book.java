/**
 * TODO: Implement Book class that extends LibraryItem
 */
public class Book extends LibraryItem {
    private String author;
    private String isbn;
    private int pages;
    private String genre;
    
    private static final double LATE_FEE_PER_DAY = 0.25;
    private static final int MAX_BORROW_DAYS = 21;

    public Book(String title, String author, String isbn, int pages, String genre) {
        super(title);
        this.author = author;
        this.isbn = isbn;
        this.pages = pages;
        this.genre = genre;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        if (daysLate <= 0) return 0;
        return daysLate * LATE_FEE_PER_DAY;
    }

    @Override
    public int getMaxBorrowDays() {
        return MAX_BORROW_DAYS;
    }

    // Getters
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getPages() { return pages; }
    public String getGenre() { return genre; }

    @Override
    public String toString() {
        return String.format("Book[title=%s, author=%s, isbn=%s, genre=%s]",
            getTitle(), author, isbn, genre);
    }
} 