/**
 * Interface for objects that can participate in play activities
 */
public interface Playable {
    void play();
    boolean isPlayful();
    int getEnergyLevel();
    void rest();
} 