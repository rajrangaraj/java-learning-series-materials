/**
 * Interface for accounts that can earn interest
 */
public interface InterestBearing {
    double calculateMonthlyInterest();
    void applyInterest();
    double getInterestRate();
    void setInterestRate(double rate);
} 