package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.CardPlayed;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.AbandonedShip;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.player.UpdateCoins;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;

import java.util.List;

/**
 * State class representing the Abandoned Ship card state in the game.
 * This state handles the logic for playing the Abandoned Ship card, which requires
 * crew members to be removed and provides credits and movement penalties in return.
 * @author Vittorio Sironi
 */
public class AbandonedShipState extends State {
    /** The Abandoned Ship card associated with this state */
    private final AbandonedShip card;

    /**
     * Constructor for AbandonedShipState
     * @param board The board associated with the game
     * @param card The AbandonedShip card associated with the state
     */
    public AbandonedShipState(Board board, EventCallback callback, AbandonedShip card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
    }

    /**
     * Initiates the play of the Abandoned Ship card for the specified player.
     * Validates that the player has sufficient crew members to meet the card's requirements
     * before allowing the card to be played.
     *
     * @param player The player attempting to play the Abandoned Ship card
     * @throws IllegalStateException if the player does not have enough crew members
     */
    @Override
    public void play(PlayerData player) {
        if (player.getSpaceShip().getCrewNumber() >= card.getCrewRequired()) {
            super.play(player);
            CardPlayed cardPlayedEvent = new CardPlayed(player.getUsername());
            eventCallback.trigger(cardPlayedEvent);
        }
        else {
            throw new IllegalStateException("Player " + player.getUsername() + " does not have enough crew to play");
        }
    }

    /**
     * Implementation of the {@link State #loseCrew(PlayerData, int, List)} to set the crew to lose in
     * order to serve the penalty.
     * @throws IllegalArgumentException if the type is not 0, 1 or 2.
     */
    @Override
    public void setPenaltyLoss(PlayerData player, int type, List<Integer> cabinsID) {
        switch (type) {
            case 0 -> throw new IllegalStateException("No goods to remove in this state");
            case 1 -> throw new IllegalStateException("No batteries to remove in this state");
            case 2 -> {
                List<Event> events = Handler.loseCrew(player, cabinsID, card.getCrewRequired());
                for (Event event : events) {
                    eventCallback.trigger(event);
                }
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0, 1 or 2.");
        }
    }

    /**
     * Handles the entry into the Abandoned Ship state.
     * Calls the parent's entry method to perform common initialization tasks.
     */
    @Override
    public void entry() {
        super.entry();
    }

    /**
     * Execute:
     * <ul>
     *     <li> Remove crew members from cabins.</li>
     *     <li> Add credits to player. </li>
     *     <li> Change player position. </li>
     * </ul>
     * @param player PlayerData of the player to play
     * @throws NullPointerException  if player == null
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

        if (playersStatus.get(player.getColor()).equals(PlayerStatus.PLAYING)) {
            player.addCoins(card.getCredit());

            UpdateCoins coinsEvent = new UpdateCoins(player.getUsername(), card.getCredit());
            eventCallback.trigger(coinsEvent);

            played = true;
        }
        super.execute(player);

        if (!played) {
            try {
                CurrentPlayer currentPlayerEvent = new CurrentPlayer(this.getCurrentPlayer().getUsername());
                eventCallback.trigger(currentPlayerEvent);
            } catch (Exception e) {
                // Ignore the exception
            }
        }

        super.nextState(GameState.CARDS);
    }

    /**
     * Handles the exit from the Abandoned Ship state.
     * Validates that all players have played, then applies movement penalties
     * to players who successfully played the card and refreshes the board state.
     *
     * @throws IllegalStateException if not all players have played when required
     */
    @Override
    public void exit() throws IllegalStateException{
        // There are two for loops here, because we need first to control the exception and then move the marker
        if (!played) {
            allPlayersPlayed();
        }

        for (PlayerData player : players) {
            PlayerStatus status = playersStatus.get(player.getColor());
            if (status == PlayerStatus.PLAYED) {
                int flightDays = card.getFlightDays();
                board.addSteps(player, -flightDays);

                MoveMarker stepEvent = new MoveMarker(player.getUsername(), player.getModuleStep(board.getStepsForALap()));
                eventCallback.trigger(stepEvent);

                break;
            }
        }

        board.refreshInGamePlayers();
    }
}
