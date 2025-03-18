package Model.State;

import Model.Cards.AbandonedShip;
import Model.Player.PlayerData;
import Model.SpaceShip.Cabin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AbandonedShipState extends State {
    private final AbandonedShip card;
    private final Map<Integer, Integer> crewLoss = new HashMap<>();
    private int crewLostTotal;

    /**
     *
     * @param players Sorted list of players
     * @param card type of the card
     */
    public AbandonedShipState(ArrayList<PlayerData> players, AbandonedShip card) {
        super(players);
        this.card = card;
    }

    /**
     * Set which cabin loses crew members
     * @param ID ID of the card
     * @param crewNumber Number of crew members lost: 1 or 2
     */
    public void setCrewLoss(int ID, int crewNumber) {
        crewLoss.put(ID, crewNumber);
        crewLostTotal += crewNumber;
    }

    /**
     * Execute: Remove crew members from cabins.
     * Add credits to player.
     * Change player steps.
     * @param player PlayerData of the player to play
     * @throws NullPointerException if player == null
     * @throws IllegalStateException if played == true: Card playable just once
     */
    @Override
    public void execute(PlayerData player) throws NullPointerException, IllegalStateException {
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        if (super.played) {
            throw new IllegalStateException("State already played");
        }
        if (crewLostTotal != card.getCrewRequired()) {
            throw new IllegalStateException("Crew loss does not match the card requirements");
        }
        super.execute(player);
        super.played = true;
        crewLoss.forEach((ID, loss) -> {
            Cabin cabin = player.getSpaceShip().getCabin(ID);
            cabin.removeCrewMember(loss);
        });
        player.addCoins(card.getCredit());
        player.addSteps(-card.getFlightDays());
    }
}
