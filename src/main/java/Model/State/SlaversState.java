package Model.State;

import Model.Cards.Slavers;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.AcceptableCredits;
import Model.State.interfaces.RemovableCrew;
import Model.State.interfaces.UsableCannon;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum slaversInternalState {
    SET_CANNONS,
    PENALTY
}

public class SlaversState extends State implements AcceptableCredits, UsableCannon, RemovableCrew {
    private slaversInternalState internalState;
    private final Slavers card;
    private final Map<PlayerData, Float> stats;
    private ArrayList<Pair<Integer, Integer>> crewLoss;
    private Boolean slaversDefeat;
    private Boolean acceptCredits;

    /**
     * Constructor whit players and card
     * @param players List of players in the current order to play
     * @param card Slavers card associated with the state
     */
    public SlaversState(ArrayList<PlayerData> players, Slavers card) {
        super(players);
        this.internalState = slaversInternalState.SET_CANNONS;
        this.card = card;
        this.stats = new HashMap<>();
        this.crewLoss = null;
        this.slaversDefeat = false;
        this.acceptCredits = null;
    }

    /**
     * Add stats to the player
     * @param player PlayerData
     * @param value Float value to add
     * @throws IllegalStateException if execForPlayer != 0
     */
    public void useCannon(PlayerData player, Float value) throws IllegalStateException {
        if (internalState != slaversInternalState.SET_CANNONS) {
            throw new IllegalStateException("Use cannon not allowed in this state");
        }
        stats.merge(player, value, Float::sum);


        int cardValue = card.getCannonStrengthRequired();
        if (stats.get(player) > cardValue) {
            slaversDefeat = true;
        } else if (stats.get(player) < cardValue) {
            slaversDefeat = false;
        } else {
            slaversDefeat = null;
        }

        internalState = slaversInternalState.PENALTY;
    }

    /**
     * Set the crew loss for a cabin
     * @param cabinsID Map of cabins ID and number of crew removed for cabins
     * @throws IllegalStateException if state is not PENALTY
     */
    public void setCrewLoss(ArrayList<Pair<Integer, Integer>> cabinsID) throws IllegalStateException {
        if (internalState != slaversInternalState.PENALTY) {
            throw new IllegalStateException("setCabinsID not allowed in this state");
        }

        int crewRemoved = 0;
        for (Pair<Integer, Integer> cabin : cabinsID) {
            crewRemoved += cabin.getValue1();
        }

        if (crewRemoved != card.getCrewLost()) {
            throw new IllegalStateException("The crew removed is not equal to the crew lost");
        }
        this.crewLoss = cabinsID;
    }

    /**
     * Set if the player accepts the credits
     * @param acceptCredits Boolean value
     * @throws IllegalStateException if execForPlayer != 1
     */
    public void setAcceptCredits(boolean acceptCredits) throws IllegalStateException {
        if (internalState != slaversInternalState.PENALTY) {
            throw new IllegalStateException("setAcceptCredits not allowed in this state");
        }
        this.acceptCredits = acceptCredits;
    }

    /**
     * Check if the slavers are defeated
     * @return Boolean value
     */
    public Boolean isSlaversDefeat() {
        return slaversDefeat;
    }

    /**
     * Entry method, set the stats for the players
     */
    @Override
    public void entry() {
        PlayerData value0;
        for (Pair<PlayerData, PlayerStatus> player : super.players) {
            value0 = player.getValue0();
            useCannon(value0, value0.getSpaceShip().getSingleCannonsStrength());
            if (value0.getSpaceShip().hasPurpleAlien()) {
                useCannon(value0, SpaceShip.getAlienStrength());
            }
        }
    }

    /**
     * Execute the state
     * @param player PlayerData of the player to play
     * @throws IllegalStateException if acceptCredits not set, crewLost not set
     */
    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        SpaceShip spaceShip = player.getSpaceShip();

        switch (internalState) {
            case SET_CANNONS:
                throw new IllegalStateException("Cannons need to be set");
            case PENALTY:
                if (slaversDefeat != null && slaversDefeat) {
                    if (acceptCredits == null) {
                        throw new IllegalStateException("acceptCredits not set");
                    }
                    if (acceptCredits) {
                        player.addCoins(card.getCredit());
                        player.addSteps(-card.getFlightDays());
                    }
                } else if (slaversDefeat != null) {
                    if (crewLoss == null) {
                        throw new IllegalStateException("crewLost not set");
                    }
                    for (Pair<Integer, Integer> cabin : crewLoss) {
                        spaceShip.getCabin(cabin.getValue0()).removeCrewMember(cabin.getValue1());
                    }
                    if (spaceShip.getCrewNumber() <= card.getCrewLost()) {
                        player.setGaveUp(true);
                    }
                }
                internalState = slaversInternalState.SET_CANNONS;
                break;
        }

        if (slaversDefeat != null && slaversDefeat) {
            super.setStatusPlayers(PlayerStatus.PLAYED);
        } else {
            super.execute(player);
        }
    }
}
