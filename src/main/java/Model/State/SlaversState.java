package Model.State;

import Model.Cards.Slavers;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import controller.EventCallback;
import event.game.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SlaversState extends State {
    private SlaversInternalState internalState;
    private final Slavers card;
    private final Map<PlayerData, Float> stats;
    private List<Integer> crewLoss;
    private Boolean slaversDefeat;

    /**
     * Enum to represent the internal state of the slavers state.
     */
    private enum SlaversInternalState {
        ENEMY_DEFEAT,
        PENALTY
    }

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card Slavers card associated with the state
     */
    public SlaversState(Board board, EventCallback callback, Slavers card) {
        super(board, callback);
        this.internalState = SlaversInternalState.ENEMY_DEFEAT;
        this.card = card;
        this.stats = new HashMap<>();
        this.crewLoss = null;
        this.slaversDefeat = false;
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

                UseCannons useCannonsEvent = new UseCannons(player.getUsername(), strength, (ArrayList<Integer>) batteriesID);
                eventCallback.trigger(useCannonsEvent);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }
    }

    /**
     * Implementation of the {@link State #setPenaltyLoss(PlayerData, int, List)} to set the crew to lose in
     * order to serve the penalty.
     * @throws IllegalArgumentException if the type is not 0, 1 or 2.
     */
    @Override
    public void setPenaltyLoss(PlayerData player, int type, List<Integer> cabinsID) throws IllegalStateException {
        switch (type) {
            case 0 -> throw new IllegalStateException("No goods to remove in this state");
            case 1 -> throw new IllegalStateException("No batteries to remove in this state");
            case 2 -> {
                // Check if there are the provided number of crew members in the provided cabins
                Map<Integer, Integer> cabinCrewMap = new HashMap<>();
                for (int cabinID : cabinsID) {
                    cabinCrewMap.merge(cabinID, 1, Integer::sum);
                }
                SpaceShip ship = player.getSpaceShip();
                for (int cabinID : cabinCrewMap.keySet()) {
                    if (ship.getCabin(cabinID).getCrewNumber() < cabinCrewMap.get(cabinID)) {
                        throw new IllegalStateException("Not enough crew members in cabin " + cabinID);
                    }
                }
                // Check if the number of crew members to remove is equal to the number of crew members required to lose
                if (cabinsID.size() != card.getCrewLost()) {
                    throw new IllegalStateException("The crew removed is not equal to the crew required to lose");
                }
                this.crewLoss = cabinsID;
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0, 1 or 2.");
        }
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

                EnemyDefeat enemyEvent = new EnemyDefeat(player.getUsername(), Boolean.TRUE.equals(slaversDefeat));
                eventCallback.trigger(enemyEvent);
            case PENALTY:
                if (slaversDefeat != null && slaversDefeat) {
                    if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
                        player.addCoins(card.getCredit());

                        AddCoins coinsEvent = new AddCoins(player.getUsername(), player.getCoins());
                        eventCallback.trigger(coinsEvent);
                    }
                    super.execute(player);
                } else if (slaversDefeat != null) {
                    if (crewLoss == null) {
                        throw new IllegalStateException("crewLost not set");
                    }
                    /*
                       TODO:
                        I think we need to check if the player has enough crew members before going to the penalty
                        So that if the player has not enough crew members we send the lose event without making the player
                        send an unnecessary setPenaltyLoss
                     */

                    if (spaceShip.getCrewNumber() <= card.getCrewLost()) {
                        player.setGaveUp(true);
                        this.players = super.board.getInGamePlayers();

                        PlayerLose loseEvent = new PlayerLose(player.getUsername());
                        eventCallback.trigger(loseEvent);
                    } else {
                        for (int cabinID : crewLoss) {
                            spaceShip.removeCrewMember(cabinID, 1);
                        }
                        // TODO: Due to the change of crewLoss to List<Integer> we need to change the event
                        //AddLoseCrew crewEvent = new AddLoseCrew(player.getUsername(), false, crewLoss);
                        //eventCallback.trigger(crewEvent);
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

                MoveMarker stepsEvent = new MoveMarker(p.getUsername(), p.getStep());
                eventCallback.trigger(stepsEvent);
            } else if (status == PlayerStatus.WAITING || status == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        super.exit();
    }
}
