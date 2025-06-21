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

public class AbandonedShipState extends State {
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
                Event event = Handler.loseCrew(player, cabinsID, card.getCrewRequired());
                eventCallback.trigger(event);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0, 1 or 2.");
        }
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

    @Override
    public void exit() throws IllegalStateException{
        for (PlayerData player : players) {
            PlayerStatus status = playersStatus.get(player.getColor());
            if (status == PlayerStatus.PLAYED) {
                int flightDays = card.getFlightDays();
                board.addSteps(player, -flightDays);

                MoveMarker stepEvent = new MoveMarker(player.getUsername(), player.getModuleStep(board.getStepsForALap()));
                eventCallback.trigger(stepEvent);

                break;
            } else if (status == PlayerStatus.WAITING || status == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        super.played = true;
    }
}
