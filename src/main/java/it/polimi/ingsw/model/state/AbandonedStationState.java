package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.model.cards.AbandonedStation;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Storage;

import java.util.List;
import java.util.ArrayList;

import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.GoodsSwapped;
import it.polimi.ingsw.event.game.serverToClient.MoveMarker;
import it.polimi.ingsw.event.game.serverToClient.UpdateGoodsExchange;
import org.javatuples.Triplet;

public class AbandonedStationState extends State {
    private final AbandonedStation card;
    private List<Triplet<List<Good>, List<Good>, Integer>> exchangeData;

    public AbandonedStationState(Board board, EventCallback callback, AbandonedStation card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
        this.exchangeData = new ArrayList<>();
    }

    @Override
    public void play(PlayerData player) {
        if (player.getSpaceShip().getCrewNumber() >= card.getCrewRequired()) {
            super.play(player);
        }
        else {
            throw new IllegalStateException("Player " + player.getUsername() + " does not have enough crew to play");
        }
    }

    /**
     * Implementation of {@link State#setGoodsToExchange(PlayerData, List)} to set the goods the player wants to exchange;
     * the goods that want to get and the goods that want to leave.
     * @throws IllegalArgumentException If the goods to get are not in the abandoned station or if the goods to leave are not in the storage.
     * @throws IllegalStateException If the player has not selected to play.
     */
    @Override
    public void setGoodsToExchange(PlayerData player, List<Triplet<List<Good>, List<Good>, Integer>> exchangeData) {
        // Check that the player has selected to play
        if (playersStatus.get(player.getColor()) != PlayerStatus.PLAYING) {
            throw new IllegalStateException("Player " + player.getUsername() + " has not selected to play");
        }
        for (Triplet<List<Good>, List<Good>, Integer> triplet : exchangeData) {
            Storage storage;
            // Check that the storage exists
            try {
                SpaceShip ship = player.getSpaceShip();
                storage = ship.getStorage(triplet.getValue2());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid storage ID: " + triplet.getValue2());
            }
            // Has the player selected to play?
            if (playersStatus.get(player.getColor()) != PlayerStatus.PLAYING) {
                throw new IllegalStateException("Player " + player.getUsername() + " has not selected to play");
            }
            // Check that the goods to get are in the abandoned station
            for (Good good : triplet.getValue0()) {
                if (!card.getGoods().contains(good)) {
                    throw new IllegalArgumentException ("The good " + good + " the player want to get is not in the abandoned station");
                }
                // Check if there is dangerous goods
                if (good.getColor() == GoodType.RED && !storage.isDangerous()) {
                    throw new IllegalArgumentException ("The good " + good + " is dangerous and the storage is not dangerous");
                }
            }
            // Check that the goods to leave are in the storage
            for (Good good : triplet.getValue1()) {
                if (!storage.hasGood(good)) {
                    throw new IllegalArgumentException ("The Good " + good + " the player want to leave is not in storage " + triplet.getValue2());
                }
            }
            // Check that we can store the goods in the storage
            if (storage.getGoodsCapacity() + triplet.getValue1().size() < triplet.getValue0().size()) {
                throw new IllegalArgumentException ("The storage " + triplet.getValue2() + " does not have enough space to store the goods");
            }
        }
        this.exchangeData = exchangeData;
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
        if (playersStatus.get(player.getColor()) != PlayerStatus.PLAYING) {
            throw new IllegalStateException("Player " + player.getUsername() + " has not selected to play");
        }
        // Check that the storage exists
        SpaceShip ship = player.getSpaceShip();
        Storage storage1, storage2;
        try {
            storage1 = ship.getStorage(storageID1);
            storage2 = ship.getStorage(storageID2);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid storage ID: " + storageID1 + " or " + storageID2);
        }
        // Check that the goods to leave are in the storage 1
        for (Good good : goods1to2) {
            if (!storage1.hasGood(good)) {
                throw new IllegalArgumentException ("The Good " + good + " the player want to leave is not in storage " + storageID1);
            }
        }
        // Check that the goods to leave are in the storage 2
        for (Good good : goods2to1) {
            if (!storage2.hasGood(good)) {
                throw new IllegalArgumentException ("The Good " + good + " the player want to leave is not in storage " + storageID2);
            }
        }
        // Check that we can store the goods in the storage 1
        if (storage1.getGoodsCapacity() + goods1to2.size() < goods2to1.size()) {
            throw new IllegalArgumentException ("The storage " + storageID1 + " does not have enough space to store the goods");
        }
        // Check that we can store the goods in the storage 2
        if (storage2.getGoodsCapacity() + goods2to1.size() < goods1to2.size()) {
            throw new IllegalArgumentException ("The storage " + storageID2 + " does not have enough space to store the goods");
        }
        // Swap the goods
        ship.exchangeGood(goods1to2, goods2to1, storageID1);
        ship.exchangeGood(goods2to1, goods1to2, storageID2);

        GoodsSwapped goodsSwappedEvent = new GoodsSwapped(player.getUsername(), storageID1, storageID2, goods1to2.stream().map(Good::getValue).toList(), goods2to1.stream().map(Good::getValue).toList());
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

            // Execute the exchange
            for (Triplet<List<Good>, List<Good>, Integer> triplet : exchangeData) {
                SpaceShip ship = player.getSpaceShip();
                ship.exchangeGood(triplet.getValue0(), triplet.getValue1(), triplet.getValue2());
            }

            super.played = true;

            List<Triplet<List<Integer>, List<Integer>, Integer>> convertedData = exchangeData.stream()
                    .map(t -> new Triplet<>(
                            t.getValue0().stream()
                                    .map(Good::getValue)
                                    .toList(),
                            t.getValue1().stream()
                                    .map(Good::getValue)
                                    .toList(),
                            t.getValue2()))
                    .toList();
            UpdateGoodsExchange exchangeEvent = new UpdateGoodsExchange(player.getUsername(), convertedData);
            eventCallback.trigger(exchangeEvent);
        } else {
            playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
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

                MoveMarker stepEvent = new MoveMarker(player.getUsername(), player.getStep());
                eventCallback.trigger(stepEvent);

                break;
            } else if (status == PlayerStatus.WAITING || status == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        super.played = true;
    }
}
