package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.model.cards.MeteorSwarm;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;

public class MeteorSwarmState extends State {
    private final MeteorSwarm card;
    private final FightHandlerSubState fightHandler;

    /**
     * Constructor
     * @param board The board associated with the game
     * @param card card type
     */
    public MeteorSwarmState(Board board, EventCallback callback, MeteorSwarm card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
        this.fightHandler = new FightHandlerSubState(super.eventCallback);
    }

    public FightHandlerSubState getFightHandler() {
        return fightHandler;
    }

    public MeteorSwarm getCard() {
        return card;
    }

    /**
     * Implementation of {@link State#setFragmentChoice(int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        fightHandler.setFragmentChoice(fragmentChoice);
    }

    /**
     * Implementation of the {@link State#setProtect(PlayerData, int)} to set whether the player wants to protect or not.
     */
    @Override
    public void setProtect(PlayerData player, int batteryID) throws IllegalStateException, IllegalArgumentException {
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
        // Roll the first dice
        int firstDice = (int) (Math.random() * 6) + 1;
        // Roll the second dice
        int secondDice = (int) (Math.random() * 6) + 1;
        // TODO: We should notify the two dice separately to handle visualization in the UI
        fightHandler.setDice(firstDice + secondDice);
    }

    /**
     * Execute: Check if player can protect, destroy components if necessary, choose which components to keep if necessary
     * @param player PlayerData of the player to play
     * @throws IndexOutOfBoundsException hitIndex out of bounds, toKeepComponents is out of bounds
     * @throws IllegalStateException Dice not set
     */
    @Override
    public void execute(PlayerData player) throws IndexOutOfBoundsException, IllegalStateException {
        int currentHitIndex = fightHandler.getHitIndex();
        if (currentHitIndex >= card.getMeteors().size()) {
            throw new IndexOutOfBoundsException("Hit index out of bounds");
        }

        fightHandler.executeFight(player, () -> card.getMeteors().get(currentHitIndex));
        super.nextState(GameState.CARDS);
    }
}