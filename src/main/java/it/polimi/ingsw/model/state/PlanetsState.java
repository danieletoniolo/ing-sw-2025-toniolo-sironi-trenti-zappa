package it.polimi.ingsw.model.state;

import it.polimi.ingsw.model.cards.Planets;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.model.spaceship.Storage;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.GoodsSwapped;
import it.polimi.ingsw.event.game.serverToClient.MoveMarker;
import it.polimi.ingsw.event.game.serverToClient.UpdateGoodsExchange;
import org.javatuples.Triplet;

import java.util.List;

public class PlanetsState extends State {
    private final Planets card;
    private final PlayerData[] planetSelected;
    private List<Triplet<List<Good>, List<Good>, Integer>> exchangeData;
    /**
     * Constructor for PlanetsState
     * @param board The board associated with the game
     * @param card Planet card associated with the state
     */
    public PlanetsState(Board board, EventCallback callback, Planets card) {
        super(board, callback);
        this.card = card;
        planetSelected = new PlayerData[card.getPlanetNumbers()];
    }

    /**
     * Getter for the card
     * @return The card
     */
    public Planets getCard() {
        return card;
    }

    /**
     * Getter for the planet selected
     * @return The planet selected
     */
    public PlayerData[] getPlanetSelected() {
        return planetSelected;
    }

    /**
     * Implementation of {@link State#selectPlanet(PlayerData, int)} to select a planet to land on.
     * @throws IllegalArgumentException If the planet number is invalid.
     * @throws IllegalStateException If the planet is already selected by another player.
     */
    @Override
    public void selectPlanet(PlayerData player, int planetNumber) throws IllegalStateException{
        if (planetNumber < 0 || planetNumber >= card.getPlanetNumbers()) {
            throw new IllegalArgumentException("Invalid planet number: " + planetNumber);
        }
        if (planetSelected[planetNumber] == null) {
            planetSelected[planetNumber] = player;
        } else {
            throw new IllegalStateException("Planet already selected by" + planetSelected[planetNumber].getUsername());
        }
    }

    /**
     * Implementation of {@link State#setGoodsToExchange(PlayerData, List)} to set the goods the player wants to exchange;
     * the goods that want to get and the goods that want to leave.
     * @throws IllegalArgumentException If the storage ID is invalid, if the good is not in the planet selected or if the good is not in the storage.
     * @throws IllegalStateException If the player has not selected a planet.
     */
    @Override
    public void setGoodsToExchange(PlayerData player, List<Triplet<List<Good>, List<Good>, Integer>> exchangeData) {
        for (Triplet<List<Good>, List<Good>, Integer> triplet : exchangeData) {
            Storage storage;
            // Check that the storage exists
            try {
                SpaceShip ship = player.getSpaceShip();
                storage = ship.getStorage(triplet.getValue2());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid storage ID: " + triplet.getValue2());
            }
            // Has the player selected a planet?
            int index = -1;
            for (int i = 0; i < card.getPlanetNumbers(); i++) {
                if (planetSelected[i] == player) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                throw new IllegalStateException("The player has not selected a planet");
            }
            // Check that the goods to get are in the planet selected
            for (Good good : triplet.getValue0()) {
                if (!card.getPlanet(index).contains(good)) {
                    throw new IllegalArgumentException ("The good " + good + " the player want to get is not in planet " + index);
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

        GoodsSwapped goodsSwappedEvent = new GoodsSwapped(player.getUsername(), storageID1, storageID2, goods1to2, goods2to1);
        eventCallback.trigger(goodsSwappedEvent);
    }

    @Override
    public void entry() {
        super.entry();
    }

    /**
     * Marks the player as played
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) throws NullPointerException {
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
            playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);

            // Execute the exchange
            for (Triplet<List<Good>, List<Good>, Integer> triplet : exchangeData) {
                SpaceShip ship = player.getSpaceShip();
                ship.exchangeGood(triplet.getValue0(), triplet.getValue1(), triplet.getValue2());
            }

            UpdateGoodsExchange exchangeGoodsEvent = new UpdateGoodsExchange(player.getUsername(), exchangeData);
            eventCallback.trigger(exchangeGoodsEvent);

        } else if (playersStatus.get(player.getColor()) == PlayerStatus.WAITING) {
            playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
        }
    }

    /**
     * Exits the state and removes the flight days from the players that have selected a planet
     * If a player has not selected a planet, the flight days are not removed
     * @throws IllegalStateException If not all players have played
     */
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
