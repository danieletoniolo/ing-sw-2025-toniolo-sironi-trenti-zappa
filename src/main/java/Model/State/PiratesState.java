package Model.State;

import Model.Cards.Pirates;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import controller.EventCallback;
import event.game.AddCoins;
import event.game.EnemyDefeat;
import event.game.UseCannons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PiratesState extends State {
    private final Pirates card;
    private final Map<PlayerData, Float> stats;
    private PiratesInternalState internalState;
    Boolean piratesDefeat;
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
     * Implementation of {@link State#setFragmentChoice(int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        if (fragmentChoice < 0 || fragmentChoice >= card.getFires().size()) {
            throw new IllegalArgumentException("Fragment choice is out of bounds");
        }
        fightHandler.setFragmentChoice(fragmentChoice);
    }

    /**
     * Implementation of the {@link State#setProtect(PlayerData, int)} to set whether the player wants to protect or not.
     */
    @Override
    public void setProtect(PlayerData player, int batteryID) throws IllegalStateException, IllegalArgumentException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("setProtect not allowed in this state");
        }
        try {
            if (batteryID != -1) {
                SpaceShip ship = player.getSpaceShip();
                if (ship.getBattery(batteryID).getEnergyNumber() < 1) {
                    throw new IllegalArgumentException("Not enough energy in battery " + batteryID);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid battery ID: " + batteryID);
        }
        if (batteryID == -1) {
            fightHandler.setProtect(false, null);
        } else {
            fightHandler.setProtect(true, batteryID);
        }
    }

    /**
     * Implementation of the {@link State#rollDice()} to roll the dice and set the value in the fight handler.
     */
    @Override
    public void rollDice() throws IllegalStateException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("setDice not allowed in this state");
        }
        // Roll the first dice
        int firstDice = (int) (Math.random() * 6) + 1;
        // Roll the second dice
        int secondDice = (int) (Math.random() * 6) + 1;
        // TODO: We should notify the two dice separately to handle visualization in the UI
        fightHandler.setDice(firstDice + secondDice);
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
                    if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
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
            for (PlayerData p : players) {
                playersStatus.put(p.getColor(), PlayerStatus.PLAYED);
            }
        } else {
            super.execute(player);
        }

        if (players.indexOf(player) == players.size() - 1 && (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED || playersStatus.get(player.getColor()) == PlayerStatus.SKIPPED)) {
            internalState = PiratesInternalState.PENALTY;
            for (PlayerData p : players) {
                playersStatus.put(p.getColor(), PlayerStatus.WAITING);
            }
        }
    }
}
