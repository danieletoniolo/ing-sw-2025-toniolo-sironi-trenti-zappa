package Model.State;

import Model.Cards.AbandonedShip;
import Model.Player.PlayerData;
import Model.State.interfaces.RemovableCrew;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AbandonedShipState extends State implements RemovableCrew {
    private final AbandonedShip card;
    private Map<Integer, Integer> crewLoss;

    /**
     * Constructor
     * @param players Sorted list of players
     * @param card type of the card
     */
    public AbandonedShipState(ArrayList<PlayerData> players, AbandonedShip card) {
        super(players);
        this.card = card;
        this.crewLoss = null;
    }

    /**
     * Set which cabin loses crew members
     * @param cabinsID Map of cabins ID and number of crew removed for cabins
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setCrewLoss(Map<Integer, Integer> cabinsID) throws IllegalStateException {
        int crewRemoved = 0;
        for (Map.Entry<Integer, Integer> cabinID : cabinsID.entrySet()) {
            crewRemoved += cabinID.getValue();
        }

        if (crewRemoved != card.getCrewRequired()) {
            throw new IllegalStateException("The crew removed is not equal to the crew lost");
        }
        this.crewLoss = cabinsID;
    }

    /**
     * Execute: Remove crew members from cabins.
     * Add credits to player.
     * Change player steps.
     * @param player PlayerData of the player to play
     * @throws NullPointerException if player == null
     * @throws IllegalStateException if played == true: Card playable just once
     * @throws IllegalStateException if crew loss does not match the card requirements
     * @throws IllegalStateException if crew loss not set
     */
    @Override
    public void execute(PlayerData player) throws NullPointerException, IllegalStateException {
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        if (super.played) {
            throw new IllegalStateException("State already played");
        }
        if (crewLoss == null) {
            throw new IllegalStateException("Crew loss does not match the card requirements");
        }

        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                if (p.getValue1().equals(PlayerStatus.PLAYING)) {
                    played = true;
                    crewLoss.forEach((ID, loss) -> {
                        player.getSpaceShip().getCabin(ID).removeCrewMember(loss);
                    });
                    player.addCoins(card.getCredit());
                    player.addSteps(-card.getFlightDays());
                }
                break;
            }
        }
        super.execute(player);
    }
}
