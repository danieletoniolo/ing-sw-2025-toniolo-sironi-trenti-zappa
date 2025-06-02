package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.planets.PlanetSelected;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.Planets;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.player.PlayerData;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import org.javatuples.Triplet;

import java.util.List;

public class PlanetsState extends State {
    private final Planets card;
    private final PlayerData[] planetSelected;

    /**
     * Constructor for PlanetsState
     * @param board The board associated with the game
     * @param card Planet card associated with the state
     */
    public PlanetsState(Board board, EventCallback callback, Planets card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
        planetSelected = new PlayerData[card.getPlanetNumbers()];
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

            PlanetSelected planetSelectedEvent = new PlanetSelected(player.getUsername(), planetNumber);
            eventCallback.trigger(planetSelectedEvent);
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

        Event exchangeGoodsEvent = Handler.exchangeGoods(player, exchangeData, card.getPlanet(index));
        eventCallback.trigger(exchangeGoodsEvent);
    }

    /**
     * Implementation of {@link State#swapGoods(PlayerData, int, int, List, List)} to swap the goods between two storage.
     * @throws IllegalStateException if we cannot exchange goods, there is a penalty to serve.
     * @throws IllegalArgumentException if the storage ID is invalid, if the goods to get are not in the planet selected
     * or if the goods to leave are not in the storage.
     */
    @Override
    public void swapGoods(PlayerData player, int storageID1, int storageID2, List<Good> goods1to2, List<Good> goods2to1) throws IllegalStateException {
        Event goodsSwappedEvent = Handler.swapGoods(player, storageID1, storageID2, goods1to2, goods2to1);
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
        super.execute(player);
        super.nextState(GameState.CARDS);
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
