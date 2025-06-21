package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.CardPlayed;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.AbandonedStation;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import org.javatuples.Triplet;

public class AbandonedStationState extends State {
    private final AbandonedStation card;

    public AbandonedStationState(Board board, EventCallback callback, AbandonedStation card, StateTransitionHandler transitionHandler) {
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
     * Implementation of {@link State#setGoodsToExchange(PlayerData, List)} to set the goods the player wants to exchange;
     * the goods that want to get and the goods that want to leave.
     * After we check that the information is valid, we execute the exchange.
     * @throws IllegalArgumentException If the goods to get are not in the abandoned station or if the goods to leave are not in the storage.
     * @throws IllegalStateException If the player has not selected to play.
     */
    @Override
    public void setGoodsToExchange(PlayerData player, List<Triplet<List<Good>, List<Good>, Integer>> exchangeData) {
        // Check that the player has selected to play
        if (playersStatus.get(player.getColor()) != PlayerStatus.PLAYING) {
            throw new IllegalStateException("Player " + player.getUsername() + " has not selected to play");
        }

        // Has the player selected to play?
        if (playersStatus.get(player.getColor()) != PlayerStatus.PLAYING) {
            throw new IllegalStateException("Player " + player.getUsername() + " has not selected to play");
        }

        Event exchangeEvent = Handler.exchangeGoods(player, exchangeData, card.getGoods());
        eventCallback.trigger(exchangeEvent);
    }

    /**
     * Implementation of {@link State#swapGoods(PlayerData, int, int, List, List)} to swap the goods between two storage.
     * @throws IllegalStateException if we cannot exchange goods, there is a penalty to serve.
     * @throws IllegalArgumentException if the storage ID is invalid, if the goods to get are not in the planet selected
     * or if the goods to leave are not in the storage.
     */
    @Override
    public void swapGoods(PlayerData player, int storageID1, int storageID2, List<Good> goods1to2, List<Good> goods2to1) throws IllegalStateException {
        // Check that the player has selected to play
        if (playersStatus.get(player.getColor()) != State.PlayerStatus.PLAYING) {
            throw new IllegalStateException("Player " + player.getUsername() + " has not selected to play");
        }

        Event goodsSwappedEvent = Handler.swapGoods(player, storageID1, storageID2, goods1to2, goods2to1);
        eventCallback.trigger(goodsSwappedEvent);
    }

    @Override
    public void entry() {
        super.entry();
    }

    @Override
    public void execute(PlayerData player) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
            playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
            super.played = true;
        } else {
            playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
        }

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

                MoveMarker stepEvent = new MoveMarker(player.getUsername(),  player.getModuleStep(board.getStepsForALap()));
                eventCallback.trigger(stepEvent);

                break;
            } else if (status == PlayerStatus.WAITING || status == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        super.played = true;
    }
}
