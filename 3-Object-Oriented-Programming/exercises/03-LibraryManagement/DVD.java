public class DVD extends LibraryItem {
    private String director;
    private int runtime;
    private String rating;
    private List<String> cast;
    
    private static final double LATE_FEE_PER_DAY = 1.00;
    private static final int MAX_BORROW_DAYS = 7;

    public DVD(String title, String director, int runtime, String rating) {
        super(title);
        this.director = director;
        this.runtime = runtime;
        this.rating = rating;
        this.cast = new ArrayList<>();
    }

    public void addCastMember(String actor) {
        cast.add(actor);
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
    public String getDirector() { return director; }
    public int getRuntime() { return runtime; }
    public String getRating() { return rating; }
    public List<String> getCast() { return new ArrayList<>(cast); }

    @Override
    public String toString() {
        return String.format("DVD[title=%s, director=%s, runtime=%d min, rating=%s]",
            getTitle(), director, runtime, rating);
    }
} 