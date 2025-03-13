package Model.Cards;

import Model.Player.PlayerData;

import java.util.ArrayList;

public class Slavers extends Enemies {
    private int crewLost;
    private int credit;

    /**
     *
     * @param crewLost number of members crew lost
     * @param credit number of credit rewarded
     * @param level level of the card
     * @param cannonStrengthRequired cannon strength of enemies
     * @param flightDays number of flight days lost
     */
    public Slavers(int crewLost, int credit, int level, int cannonStrengthRequired, int flightDays) {
        super(level, cannonStrengthRequired, flightDays);
        this.crewLost = crewLost;
        this.credit = credit;
    }

    /**
     * Get number of credit rewarded
     * @return number of credit rewarded
     */
    public int getReward() {
        return credit;
    }

    /**
     * Get the number of crew members lost
     * @return number of crew members lost
     */
    public int getCrewLost() {
        return crewLost;
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.SLAVERS;
    }

    @Override
    public void entry(ArrayList<PlayerData> players) {
        //TODO
    }

    @Override
    public void execute(PlayerData player) {
        //TODO

    }

    @Override
    public void exit() {
        //TODO
    }
}
