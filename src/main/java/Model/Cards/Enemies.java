package Model.Cards;

import Model.Player.PlayerData;

public abstract class Enemies extends Card {
    private int cannonStrengthRequired;
    private int flightDays;
    private boolean played;

    /**
     *
     * @param level level of the card
     * @param cannonStrengthRequired cannon strength of enemies
     * @param flightDays number of flight days lost for the quest
     */
    public Enemies(int level, int cannonStrengthRequired, int flightDays) {
        super(level);
        this.cannonStrengthRequired = cannonStrengthRequired;
        this.flightDays = flightDays;
    }

    /**
     * Get the cannon power of enemies
     * @return cannon power of enemies
     */
    public int getCannonStrengthRequired() {
        return cannonStrengthRequired;
    }

    /**
     * Get the number of flight days lost for the quest
     * @return number of flight days lost for the quest
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * set
     */
    public void setPlayed() {
        played = true;
    }

    public boolean isPlayed() {
        return played;
    }
}
