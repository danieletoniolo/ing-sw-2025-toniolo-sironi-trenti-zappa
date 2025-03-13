package Model.Cards;

import Model.Cards.Hits.Hit;
import Model.Player.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class CombatZone extends Card {
    private int flightDays;
    private int lost;
    private List<Hit> fires;

    /**
     *
     * @param flightDays number of flight days lost for the quest
     * @param lost number of objects lost (varies depending on level card)
     * @param fires list of Hits
     * @param level level of the card
     * @throws NullPointerException if fires == null
     */
    public CombatZone(int flightDays, int lost, List<Hit> fires, int level) throws NullPointerException{
        super(level);
        this.flightDays = flightDays;
        this.lost = lost;

        if (fires == null || fires.isEmpty()) {
            throw new NullPointerException("Fires can't be null");
        }
        this.fires = fires;
    }

    /**
     * Get the number of flight days lost for the quest
     * @return number of flight days lost for the quest
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Get number of objects lost
     * @return number of objects lost
     */
    public int getLost() {
        return lost;
    }

    /**
     * Get the list of hits
     * @return list of hits
     */
    public List<Hit> getFires() {
        return fires;
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.COMBATZONE;
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
