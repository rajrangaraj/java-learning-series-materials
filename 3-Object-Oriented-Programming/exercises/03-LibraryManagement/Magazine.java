public class Magazine extends LibraryItem {
    private String publisher;
    private LocalDate publicationDate;
    private String issueNumber;
    private List<String> articles;
    
    private static final double LATE_FEE_PER_DAY = 0.50;
    private static final int MAX_BORROW_DAYS = 14;

    public Magazine(String title, String publisher, LocalDate publicationDate, 
                   String issueNumber) {
        super(title);
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.issueNumber = issueNumber;
        this.articles = new ArrayList<>();
    }

    public void addArticle(String article) {
        articles.add(article);
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
    public String getPublisher() { return publisher; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public String getIssueNumber() { return issueNumber; }
    public List<String> getArticles() { return new ArrayList<>(articles); }

    @Override
    public String toString() {
        return String.format("Magazine[title=%s, publisher=%s, issue=%s]",
            getTitle(), publisher, issueNumber);
    }
} 