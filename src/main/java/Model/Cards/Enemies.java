package Model.Cards;

import Model.Player.PlayerData;

public abstract class Enemies extends Card {
    private int cannonStrengthRequired;
    private int flightDays;
    private boolean played;

    public Enemies(int level, int cannonStrengthRequired, int flightDays) {
        super(level);
        this.cannonStrengthRequired = cannonStrengthRequired;
        this.flightDays = flightDays;
    }

    public int getCannonStrengthRequired() {
        return cannonStrengthRequired;
    }

    public int getFlightDays() {
        return flightDays;
    }

    public void setPlayed() {
        played = true;
    }

    public boolean isPlayed() {
        return played;
    }
}
