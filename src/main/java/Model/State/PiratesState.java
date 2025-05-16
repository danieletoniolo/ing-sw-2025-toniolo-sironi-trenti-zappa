package Model.State;

import Model.Cards.Pirates;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.AcceptableCredits;
import Model.State.interfaces.ChoosableFragment;
import Model.State.interfaces.Fightable;
import controller.EventCallback;
import event.game.AddCoins;
import event.game.EnemyDefeat;
import event.game.UseCannons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PiratesState extends State implements Fightable, ChoosableFragment, AcceptableCredits {
    private final Pirates card;
    private final Map<PlayerData, Float> stats;
    private PiratesInternalState internalState;
    Boolean piratesDefeat;
    Boolean acceptCredits;
    ArrayList<PlayerData> playersDefeated;
    FightHandlerSubState fightHandler;

    /**
     * Enum to represent the internal state of the pirates state.
     */
    private enum PiratesInternalState {
        DEFAULT,
        MIDDLE,
        PENALTY
    }

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card Pirates card associated with the state
     */
    public PiratesState(Board board, EventCallback callback, Pirates card) {
        super(board, callback);
        this.card = card;
        this.stats = new HashMap<>();
        this.piratesDefeat = false;
        this.acceptCredits = false;
        this.internalState = PiratesInternalState.DEFAULT;
        this.playersDefeated = new ArrayList<>();
        this.fightHandler = new FightHandlerSubState(super.board, super.eventCallback);
    }

    public void setInternalStatePirates(PiratesInternalState internalState) {
        this.internalState = internalState;
    }

    public Map<PlayerData, Float> getStats() {
        return stats;
    }

    public Pirates getCard() {
        return card;
    }

    /**
     * Set if the player accepts the credits
     * @param acceptCredits Boolean value
     * @throws IllegalStateException if not in the correct state
     */
    public void setAcceptCredits(boolean acceptCredits) throws IllegalStateException {
        if (internalState != PiratesInternalState.MIDDLE) {
            throw new IllegalStateException("setAcceptCredits not allowed in this state");
        }
        this.acceptCredits = acceptCredits;
    }

    /**
     * Set the fragment choice
     * @param fragmentChoice index of the fragment choice
     * @throws IllegalStateException if not in the correct state
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        fightHandler.setFragmentChoice(fragmentChoice);
    }

    /**
     * Set the use energy
     * @param protect_ use energy
     * @param batteryID_ battery ID
     * @throws IllegalStateException if not in the right state in order to do the action
     * @throws IllegalArgumentException if batteryID_ is null and protect_ is true
     */
    public void setProtect(boolean protect_, Integer batteryID_) throws IllegalStateException, IllegalArgumentException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("setProtect not allowed in this state");
        }
        fightHandler.setProtect(protect_, batteryID_);
    }

    /**
     * Set the dice
     * @param dice dice value
     */
    public void setDice(int dice) throws IllegalStateException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("setDice not allowed in this state");
        }
        fightHandler.setDice(dice);
    }

    /**
     * Implementation of the {@link State#useExtraStrength(PlayerData, int, float, List)} to use double engines
     * in this state.
     * @throws IllegalArgumentException if the type is not 0 or 1.
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, float strength, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException {
        switch (type) {
            case 0 -> throw new IllegalStateException("Cannot use double engine in this state");
            case 1 -> {
                if (internalState == PiratesInternalState.DEFAULT) {
                    throw new IllegalStateException("Cannot use double cannons in this state");
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
     * Entry method, set the stats for the players
     */
    @Override
    public void entry() {
        for (PlayerData player : super.players) {
            float initialStrength = player.getSpaceShip().getSingleCannonsStrength();
            if (player.getSpaceShip().hasPurpleAlien()) {
                initialStrength += SpaceShip.getAlienStrength();
            }
            this.stats.put(player, initialStrength);
        }
    }

    /**
     * Execute.
     * @param player PlayerData of the player to play
     * @throws IllegalStateException if acceptCredits not set
     */
    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        int cardValue = card.getCannonStrengthRequired();

        switch (internalState) {
            case DEFAULT:
                if (stats.get(player) > cardValue) {
                    piratesDefeat = true;
                } else if (stats.get(player) < cardValue) {
                    piratesDefeat = false;
                } else {
                    piratesDefeat = null;
                }

                EnemyDefeat enemyDefeat = new EnemyDefeat(player.getUsername(), piratesDefeat);
                eventCallback.trigger(enemyDefeat);

                internalState = PiratesInternalState.MIDDLE;
                break;
            case MIDDLE:
                if (piratesDefeat == null) {
                    break;
                }

                if (piratesDefeat) {
                    if (acceptCredits == null) {
                        throw new IllegalStateException("acceptCredits not set");
                    }
                    if (acceptCredits) {
                        player.addCoins(card.getCredit());
                        board.addSteps(player, -card.getFlightDays());

                        AddCoins addCoinsEvent = new AddCoins(player.getUsername(), player.getCoins());
                        eventCallback.trigger(addCoinsEvent);
                    }
                } else {
                    this.playersDefeated.add(player);
                }
                break;
            case PENALTY:
                if (!playersDefeated.contains(player)) {
                    throw new IllegalStateException("Player was not defeated");
                }
                int currentHitIndex = fightHandler.getHitIndex();
                if (currentHitIndex >= card.getFires().size()) {
                    throw new IndexOutOfBoundsException("Hit index out of bounds");
                }
                fightHandler.executeFight(player, () -> card.getFires().get(currentHitIndex));
                break;
        }

        if (piratesDefeat != null && piratesDefeat && internalState == PiratesInternalState.MIDDLE) {
            super.setStatusPlayers(PlayerStatus.PLAYED);
        } else {
            super.execute(player);
        }

        if (players.indexOf(player) == players.size() - 1 && (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED || playersStatus.get(player.getColor()) == PlayerStatus.SKIPPED)) {
            internalState = PiratesInternalState.PENALTY;
            super.setStatusPlayers(PlayerStatus.WAITING);
        }
    }
}
