package Model.State;

import Model.Cards.AbandonedShip;
import Model.Player.PlayerData;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AbandonedShipState extends State {
    private final AbandonedShip card;
    private final Map<Integer, Integer> crewLoss = new HashMap<>();
    private int crewLostTotal;
    private int state;

    /**
     * Constructor
     * @param players Sorted list of players
     * @param card type of the card
     */
    public AbandonedShipState(ArrayList<PlayerData> players, AbandonedShip card) {
        super(players);
        this.card = card;
        state = 0;
    }

    /**
     * Set which cabin loses crew members
     * @param ID ID of the card
     * @param crewNumber Number of crew members lost: 1 or 2
     */
    public void setCrewLoss(int ID, int crewNumber) {
        crewLoss.put(ID, crewNumber);
        crewLostTotal += crewNumber;
        state = 1;
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
        if (crewLostTotal != card.getCrewRequired()) {
            throw new IllegalStateException("Crew loss does not match the card requirements");
        }
        if (state == 0) {
            throw new IllegalStateException("Crew loss not set");
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
