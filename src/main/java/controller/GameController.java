package controller;

import Model.Game.Board.Board;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.State.State;
import Model.State.interfaces.*;
import controller.event.Event;
import controller.event.EventType;
import controller.event.game.GameEvents;
import controller.event.game.RemoveCrew;
import controller.event.game.UseCannons;
import controller.event.game.UseEngine;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;
import java.util.function.Consumer;

public class GameController {
    private final Board board;
    private State state;
    private final Map<EventType<? extends Event>, List<EventHandler<? extends Event>>> eventHandlers;

    public GameController() {
        board = null;
        state = null;
        eventHandlers = new HashMap<>();
    }

    private <T extends Event> List<Consumer<T>> getEventHandlers(UUID userID, EventType<T> type) {
        List<EventHandler<? extends Event>> presentConsumers = eventHandlers.get(type);
        if (presentConsumers != null) {
            return presentConsumers
                    .stream()
                    .map(h -> (EventHandler<T>) h)
                    .filter(h -> h.userID().equals(userID) || userID == null)
                    .map(EventHandler::handler)
                    .toList();
        }
        return new ArrayList<>();
    }

    public void startGame() {
        // state = new BuildingState(board);
    }

    public void joinGame(UUID uuid) {
        // TODO
    }

    public void leaveGame(UUID uuid) {
        // TODO
    }

    public void endGame() {
        // TODO
    }

    // Game actions

    public void pickTile(UUID uuid, int tileID) {
    }

    void showDeck(UUID uuid, int deckIndex) {
        if (state instanceof Buildable) {
            ((Buildable) state).showDeck(uuid, deckIndex);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    void leaveDeck(UUID uuid, int deckIndex) {
        if (state instanceof Buildable) {
            ((Buildable) state).leaveDeck(uuid, deckIndex);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    void pickTileFromPile(UUID uuid, int tileID) {
        if (state instanceof Buildable) {
            ((Buildable) state).pickTileFromPile(uuid, tileID);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    void pickTileFromReserve(UUID uuid, int tileID) {
        if (state instanceof Buildable) {
            ((Buildable) state).pickTileFromReserve(uuid, tileID);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    void pickTileFromBoard(UUID uuid, int tileID) {
        if (state instanceof Buildable) {
            ((Buildable) state).pickTileFromBoard(uuid, tileID);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    void leaveTile(UUID uuid) {
        if (state instanceof Buildable) {
            ((Buildable) state).leaveTile(uuid);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    /**
     * Place a tile on the spaceship at the given row and column
     * @param uuid player's uuid
     * @param row row to place the tile
     * @param col column to place the tile
     */
    public void placeTile(UUID uuid, int row, int col) {
        if (state instanceof Buildable) {
            ((Buildable) state).placeTile(uuid, row, col);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    public void reserveTile(UUID uuid) {
        if (state instanceof Buildable) {
            ((Buildable) state).reserveTile(uuid);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    public void rotateTile(UUID uuid) {
        if (state instanceof Buildable) {
            ((Buildable) state).rotateTile(uuid);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    public void placeMarker(UUID uuid, int position) {
        if (state instanceof Buildable) {
            ((Buildable) state).placeMarker(uuid, position);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    public void flipTimer(UUID uuid) {
        if (state instanceof Buildable) {
            ((Buildable) state).flipTimer(uuid);
        } else {
            throw new IllegalStateException("State is not a Buildable");
        }
    }

    public void choseFragment(UUID uuid, int fragmentID) {
        if (state instanceof ChoosableFragment) {
            ((ChoosableFragment) state).setFragmentChoice(fragmentID);
        } else {
            throw new IllegalStateException("State is not a ChoosableFragment");
        }
    }

    public void startTurn(UUID uuid) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            state.play(player);
        }
    }

    // ex finish()
    public void endTurn(UUID uuid) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            state.execute(player);
        }
    }

    public void giveUp(UUID uuid) {
        // TODO
    }

    public void selectPlanet(UUID uuid, int planetID) {
        if (state instanceof SelectablePlanet) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((SelectablePlanet) state).selectPlanet(player, planetID);
            }
        } else {
            throw new IllegalStateException("State is not a SelectablePlanet");
        }
    }

    /**
     * Exchange goods between in an adventure state
     * @param uuid player's uuid
     * @param exchangeData ArrayList of Triplet containing (in order) the goods to get, the goods to leave and the ID of the storage
     */
    public void exchangeGoods(UUID uuid, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) {
        if (state instanceof ExchangeableGoods) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((ExchangeableGoods) state).setGoodsToExchange(player, exchangeData);
            }
        } else {
            throw new IllegalStateException("State is not a ExchangeableGoods");
        }
    }

    /**
     * Swap goods between two storage
     * @param uuid player's uuid
     * @param storageID1 storage ID 1
     * @param storageID2 storage ID 2
     * @param goods1to2 ArrayList of goods to exchange from storage 1 to storage 2
     * @param goods2to1 ArrayList of goods to exchange from storage 2 to storage 1
     */
    public void swapGoods(UUID uuid, int storageID1, int storageID2, ArrayList<Good> goods1to2, ArrayList<Good> goods2to1) {
        if (state instanceof ExchangeableGoods) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                player.getSpaceShip().exchangeGood(goods2to1, goods1to2, storageID1);
                player.getSpaceShip().exchangeGood(goods1to2, goods2to1, storageID2);
            }
        }
    }

    /**
     * Use the cannons of the spaceship
     * @param uuid player's uuid
     * @param cannonsPowerToUse cannons power to use (float)
     */
    public void useCannons(UUID uuid, float cannonsPowerToUse, List<Integer> batteriesID) {
        if (state instanceof UsableCannon) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((UsableCannon) state).useCannon(player, cannonsPowerToUse, batteriesID);
                UseCannons info = new UseCannons(player.getSpaceShip());
                executeHandlers(GameEvents.USE_CANNONS, info);
            }
        }
    }

    /**
     * Use the engines of the spaceship
     * @param uuid player's uuid
     * @param enginesPowerToUse engines power to use (float)
     */
    public void useEngines(UUID uuid, float enginesPowerToUse, List<Integer> batteriesIDs) {
        if (state instanceof UsableEngine) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((UsableEngine) state).useEngine(player, enginesPowerToUse, batteriesIDs);
                UseEngine info = new UseEngine(player.getSpaceShip());
                executeHandlers(GameEvents.USE_ENGINE, info);
            }
        }
    }

    public void removeCrew(UUID uuid, ArrayList<Pair<Integer, Integer>> cabinID) {
        if (state instanceof RemovableCrew) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((RemovableCrew) state).setCrewLoss(cabinID);
                RemoveCrew info = new RemoveCrew(player.getSpaceShip());
                executeHandlers(GameEvents.REMOVE_CREW, info);
            }
        }
    }

    public void addCrew(UUID uuid, int cabinID, int numberOfCrew) {
    }

    public void rollDice(UUID uuid, int numberOfDice) {
        if (state instanceof Fightable) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((Fightable) state).setDice(numberOfDice);
                // TODO: SHOULD THE VIEW ROLL THE DICE?
            }
        } else {
            throw new IllegalStateException("State is not a Fightable");
        }
    }

    public<T extends Event> void addEventHandler(EventType<T> type, EventHandler<T> handler) {
        eventHandlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
        eventHandlers.get(type).add(handler);
    }

    public<T extends Event> void removeEventHandlersOfUser(UUID userID) {
        eventHandlers.values().forEach(handlers -> handlers.removeIf(handler -> handler.userID().equals(userID)));
    }

    public <T extends Event> void executeHandlers(EventType<T> type, T info) {
        getEventHandlers(null, type).forEach(handler -> (new Thread(() -> handler.accept(info))).start());
    }
}
