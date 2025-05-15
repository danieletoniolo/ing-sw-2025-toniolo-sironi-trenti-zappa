package Model.State;

import Model.Cards.Slavers;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.AcceptableCredits;
import Model.State.interfaces.RemovableCrew;
import controller.event.game.*;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum SlaversInternalState {
    ENEMY_DEFEAT,
    PENALTY
}

public class SlaversState extends State implements AcceptableCredits, RemovableCrew {
    private SlaversInternalState internalState;
    private final Slavers card;
    private final Map<PlayerData, Float> stats;
    private ArrayList<Pair<Integer, Integer>> crewLoss;
    private Boolean slaversDefeat;
    private Boolean acceptCredits;

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card Slavers card associated with the state
     */
    public SlaversState(Board board, Slavers card) {
        super(board);
        this.internalState = SlaversInternalState.ENEMY_DEFEAT;
        this.card = card;
        this.stats = new HashMap<>();
        this.crewLoss = null;
        this.slaversDefeat = false;
        this.acceptCredits = null;
    }

    public void setInternalState(SlaversInternalState internalState) {
        this.internalState = internalState;
    }

    public Slavers getCard() {
        return card;
    }

    public Map<PlayerData, Float> getStats() {
        return stats;
    }

    public ArrayList<Pair<Integer, Integer>> getCrewLoss() {
        return crewLoss;
    }

    public Boolean getAcceptCredits() {
        return acceptCredits;
    }

    /**
     * Implementation of the {@link State#useExtraStrength(PlayerData, int, float, List)} to use double engines
     * in this state.
     * @throws IllegalArgumentException if the type is not 0 or 1.
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, float strength, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException {
        switch (type) {
            case 0 -> throw new IllegalStateException("Cannot use double engines in this state");
            case 1 -> {
                if (internalState != SlaversInternalState.ENEMY_DEFEAT) {
                    throw new IllegalStateException("Use cannon not allowed in this state");
                }
                // Use the energy to power the cannon
                SpaceShip ship = player.getSpaceShip();
                for (Integer batteryID : batteriesID) {
                    ship.useEnergy(batteryID);
                }

                // Update the cannon strength stats
                this.stats.merge(player, strength, Float::sum);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }
    }

    /**
     * Set the crew loss for a cabin
     * @param cabinsID Map of cabins ID and number of crew removed for cabins
     * @throws IllegalStateException if state is not PENALTY
     */
    public void setCrewLoss(ArrayList<Pair<Integer, Integer>> cabinsID) throws IllegalStateException {
        if (internalState != SlaversInternalState.PENALTY) {
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
        if (internalState != SlaversInternalState.PENALTY) {
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
        for (PlayerData player : super.players) {
            SpaceShip ship = player.getSpaceShip();
            float initialStrength = ship.getSingleCannonsStrength();
            if (ship.hasPurpleAlien()) {
                initialStrength += SpaceShip.getAlienStrength();
            }
            stats.put(player, initialStrength);
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
            case ENEMY_DEFEAT:
                int cardValue = card.getCannonStrengthRequired();
                if (stats.get(player) > cardValue) {
                    slaversDefeat = true;
                } else if (stats.get(player) < cardValue) {
                    slaversDefeat = false;
                } else {
                    slaversDefeat = null;
                }

                // TODO: EVENT ENEMYDEFEAT
                EnemyDefeat enemyEvent = new EnemyDefeat(player.getUsername(), Boolean.TRUE.equals(slaversDefeat));
            case PENALTY:
                if (slaversDefeat != null && slaversDefeat) {
                    if (acceptCredits == null) {
                        throw new IllegalStateException("acceptCredits not set");
                    }
                    if (acceptCredits) {
                        player.addCoins(card.getCredit());

                        // TODO: EVENT ADDCOINS
                        AddCoins coinsEvent = new AddCoins(player.getUsername(), player.getCoins());
                    }
                    super.execute(player);
                } else if (slaversDefeat != null) {
                    if (crewLoss == null) {
                        throw new IllegalStateException("crewLost not set");
                    }
                    if (spaceShip.getCrewNumber() <= card.getCrewLost()) {
                        player.setGaveUp(true);
                        this.players = super.board.getInGamePlayers();

                        // TODO: EVENT LOSE
                        PlayerLose loseEvent = new PlayerLose(player.getUsername());
                    } else {
                        for (Pair<Integer, Integer> cabin : crewLoss) {
                            spaceShip.removeCrewMember(cabin.getValue0(), cabin.getValue1());
                        }

                        // TODO: EVENT CREWLOSS
                        CrewLoss crewEvent = new CrewLoss(player.getUsername(), crewLoss);
                    }
                    playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
                }
                internalState = SlaversInternalState.ENEMY_DEFEAT;
                break;
        }
    }

    @Override
    public void exit() throws IllegalStateException{
        int flightDays = card.getFlightDays();
        PlayerStatus status;
        for (PlayerData p : players) {
            status = playersStatus.get(p.getColor());
            if (status == PlayerStatus.PLAYED) {
                board.addSteps(p, -flightDays);

                // TODO: EVENT STEPS
                MoveMarker stepsEvent = new MoveMarker(p.getUsername(), p.getStep());
            } else if (status == PlayerStatus.WAITING || status == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        super.exit();
    }
}
