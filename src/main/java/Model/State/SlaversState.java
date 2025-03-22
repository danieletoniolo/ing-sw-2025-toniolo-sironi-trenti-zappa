package Model.State;

import Model.Cards.Slavers;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SlaversState extends State {
    private int execForPlayer;
    private final Slavers card;
    private final Map<PlayerData, Float> stats;
    private Map<Integer, Integer> crewLost;
    private Boolean slaversDefeat;
    private Boolean acceptCredits;

    /**
     * Constructor whit players and card
     * @param players List of players in the current order to play
     * @param card Slavers card associated with the state
     */
    public SlaversState(ArrayList<PlayerData> players, Slavers card) {
        super(players);
        this.execForPlayer = 0;
        this.card = card;
        this.stats = new HashMap<>();
        this.crewLost = null;
        this.slaversDefeat = false;
        this.acceptCredits = null;
    }

    /**
     * Add stats to the player
     * @param player PlayerData
     * @param value Float value to add
     * @throws IllegalStateException if execForPlayer != 0
     */
    public void addStats(PlayerData player, Float value) throws IllegalStateException {
        if (execForPlayer != 0) {
            throw new IllegalStateException("addStats not allowed in this state");
        }
        stats.merge(player, value, Float::sum);
    }

    /**
     * Set the crew lost for a cabin
     * @param cabin Cabin ID
     * @param crew Number of crew members lost
     * @throws IllegalStateException if execForPlayer != 1
     */
    public void setCrewLost(int cabin, int crew) throws IllegalStateException {
        if (execForPlayer != 1) {
            throw new IllegalStateException("setCrewLost not allowed in this state");
        }
        if (this.crewLost == null) {
            this.crewLost = new HashMap<>();
        }
        crewLost.put(cabin, crew);
    }

    /**
     * Set if the player accepts the credits
     * @param acceptCredits Boolean value
     * @throws IllegalStateException if execForPlayer != 1
     */
    public void setAcceptCredits(boolean acceptCredits) throws IllegalStateException {
        if (execForPlayer != 1) {
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
            addStats(value0, value0.getSpaceShip().getSingleCannonsStrength());
            if (value0.getSpaceShip().getPurpleAlien()) {
                addStats(value0, SpaceShip.getAlienStrength());
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
        int cardValue = card.getCannonStrengthRequired();

        switch (execForPlayer) {
            case 0:
                if (stats.get(player) > cardValue) {
                    slaversDefeat = true;
                } else if (stats.get(player) < cardValue) {
                    slaversDefeat = false;
                } else {
                    slaversDefeat = null;
                }
                execForPlayer++;
                break;
            case 1:
                if (slaversDefeat == null)
                    break;
                if (slaversDefeat) {
                    if (acceptCredits == null) {
                        throw new IllegalStateException("acceptCredits not set");
                    }
                    if (acceptCredits) {
                        player.addCoins(card.getCredit());
                        player.addSteps(-card.getFlightDays());
                    }
                } else {
                    if (crewLost == null) {
                        throw new IllegalStateException("crewLost not set");
                    }
                    for (Map.Entry<Integer, Integer> entry : crewLost.entrySet()) {
                        spaceShip.getCabin(entry.getKey()).removeCrewMember(entry.getValue());
                    }
                    if (spaceShip.getCrewNumber() <= card.getCrewLost()) {
                        player.setGaveUp(true);
                    }
                }
                break;
        }

        if (slaversDefeat != null && slaversDefeat && execForPlayer == 1) {
            super.setStatusPlayers(PlayerStatus.PLAYED);
        } else {
            super.execute(player);
        }
    }
}
