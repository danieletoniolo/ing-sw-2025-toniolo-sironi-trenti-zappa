package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.deck.PickedLeftDeck;
import it.polimi.ingsw.event.game.serverToClient.pickedTile.*;
import it.polimi.ingsw.event.game.serverToClient.placedTile.PlacedTileToBoard;
import it.polimi.ingsw.event.game.serverToClient.placedTile.PlacedTileToReserve;
import it.polimi.ingsw.event.game.serverToClient.placedTile.PlacedTileToSpaceship;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import it.polimi.ingsw.event.game.serverToClient.rotatedTile.RotatedTile;
import it.polimi.ingsw.event.game.serverToClient.spaceship.ComponentDestroyed;
import it.polimi.ingsw.event.game.serverToClient.timer.LastTimerFinished;
import it.polimi.ingsw.event.game.serverToClient.timer.TimerFlipped;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.*;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.utils.Logger;
import org.javatuples.Pair;

import java.time.LocalTime;
import java.util.*;


public class BuildingState extends State {
    private final Timer timer;
    private boolean timerRunning;
    private int numberOfTimerFlips;
    private static final long timerDuration = 90000;
    private final Map<PlayerColor, Component> playersHandQueue;

    public BuildingState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.timer = new Timer();
        this.numberOfTimerFlips = 1;
        this.timerRunning = false;
        this.playersHandQueue = new HashMap<>();
    }

    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state");
    }

    /**
     * Implementation of the method to flip the timer.
     * @see State#flipTimer(PlayerData)
     */
    @Override
    public void flipTimer(PlayerData player) throws IllegalStateException {
        if (board.getBoardLevel() == Level.LEARNING) {
            throw new IllegalStateException("Cannot flip timer in learning level");
        }

        if (timerRunning) {
            throw new IllegalStateException("Cannot flip timer because is already running");
        }

        TimerFlipped timerFlippedEvent;
        LocalTime time;
        switch (numberOfTimerFlips) {
            case 1:
                // First flip that is done when the building phase starts
                timerRunning = true;
                time = LocalTime.now();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        timerRunning = false;
                    }
                }, timerDuration);
                timerFlippedEvent = new TimerFlipped(null, time.toString(), numberOfTimerFlips,3, timerDuration);
                eventCallback.trigger(timerFlippedEvent);
                break;
            case 2:
                // This is the second flip that can be done by anyone after the time has run out
                timerRunning = true;
                time = LocalTime.now();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        timerRunning = false;
                    }
                }, timerDuration);
                timerFlippedEvent = new TimerFlipped(player.getUsername(), time.toString(), numberOfTimerFlips, 3, timerDuration);
                eventCallback.trigger(timerFlippedEvent);
                break;
            case 3:
                // This is the third flip that can be done only by the player who has finished building
                if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
                    timerRunning = true;
                    time = LocalTime.now();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Set the status of the player who has not finished building to PLAYED
                            for (PlayerData p: players) {
                                if (playersStatus.get(p.getColor()) != PlayerStatus.PLAYED) {
                                    playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
                                }
                                // If someone has something in his hand it must be added to the lost components
                                if (playersHandQueue.get(p.getColor()) != null) {
                                    p.getSpaceShip().getLostComponents().add(playersHandQueue.get(p.getColor()));
                                    ComponentDestroyed lostComponentsEvent = new ComponentDestroyed(p.getUsername(), p.getSpaceShip().getLostComponents().stream()
                                            .map(temp -> new Pair<>(temp.getRow(), temp.getColumn())).toList());
                                    eventCallback.trigger(lostComponentsEvent);
                                }
                            }
                            timerRunning = false;

                            // Trigger to all the users that the last timer has finished
                            LastTimerFinished lastTimerFinishedEvent = new LastTimerFinished();
                            eventCallback.trigger(lastTimerFinishedEvent);
                        }
                    }, timerDuration);
                    timerFlippedEvent = new TimerFlipped(player.getUsername(),time.toString(), numberOfTimerFlips, 3, timerDuration);
                    eventCallback.trigger(timerFlippedEvent);
                } else {
                    throw new IllegalStateException("Cannot flip timer because the player has not finished building");
                }
                break;
            default:
                throw new IllegalStateException("Cannot flip timer more than twice");
        }
        eventCallback.trigger(timerFlippedEvent);
        numberOfTimerFlips++;
    }

    /**
     * Implementation of the method to use a deck.
     * @see State#useDeck(PlayerData, int, int)
     */
    @Override
    public void useDeck(PlayerData player, int usage,int deckIndex) throws IllegalStateException {
        // Check if the player has placed at least one tile
        if (player.getSpaceShip().getNumberOfComponents() < 1) {
            throw new IllegalStateException("Player has not placed any tile");
        }

        PickedLeftDeck pickLeaveDeckEvent = new PickedLeftDeck(player.getUsername(), usage, deckIndex);
        switch (usage) {
            case 0 -> {
                board.getDeck(deckIndex, player);
                eventCallback.trigger(pickLeaveDeckEvent);
            }
            case 1 -> {
                board.leaveDeck(deckIndex, player);
                eventCallback.trigger(pickLeaveDeckEvent);
            }
        }
        player.getSpaceShip().cleanLastPlacedComponent();
    }

    /**
     * Implementation of the method to place a marker on the board.
     * @see State#placeMarker(PlayerData, int)
     */
    @Override
    public void placeMarker(PlayerData player, int position) throws IllegalStateException {
        if (super.board.getBoardLevel() == Level.SECOND && (numberOfTimerFlips < 3 || (timerRunning && numberOfTimerFlips != 4))) {
            throw new IllegalStateException("Cannot place marker before the second timer flip");
        }

        board.setPlayer(player, position);

        MoveMarker moveMarkerEvent = new MoveMarker(player.getUsername(), player.getStep());
        eventCallback.trigger(moveMarkerEvent);
    }

    /**
     * Implementation of the method to pick a tile from the board, reserve or spaceship.
     * @see State#pickTile(PlayerData, int, int)
     */
    @Override
    public void pickTile(PlayerData player, int fromWhere, int tileID) {
        // Check if the player has finished building
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
            throw new IllegalStateException("Player has finished building");
        }

        // Check if the player has a tile in his hand
        if (playersHandQueue.get(player.getColor()) != null) {
            throw new IllegalStateException("Player already has a tile in his hand");
        }

        Component component;
        switch (fromWhere) {
            case 0 -> {
                // Get the tile from the board
                component = board.popTile(tileID);

                // Trigger the event for picking a tile from the board
                String username = player.getUsername();
                int componentID = component.getID();
                int[] connectors = new int[4];
                for (int i = 0; i < 4; i++) {
                    connectors[i] = component.getConnection(i).getValue();
                }

                if (tileID == -1) {
                    switch (component.getComponentType()) {
                        case SINGLE_ENGINE, DOUBLE_ENGINE -> {
                            PickedEngineFromBoard pickedEngineFromBoard = new PickedEngineFromBoard(username, componentID, component.getClockwiseRotation(), connectors, ((Engine) component).getEngineStrength());
                            eventCallback.trigger(pickedEngineFromBoard);
                        }
                        case SINGLE_CANNON, DOUBLE_CANNON -> {
                            PickedCannonFromBoard pickedCannonFromBoard = new PickedCannonFromBoard(username, componentID, component.getClockwiseRotation(), connectors, ((Cannon) component).getCannonStrength());
                            eventCallback.trigger(pickedCannonFromBoard);
                        }
                        case CABIN -> {
                            PickedCabinFromBoard pickedCabinFromBoard = new PickedCabinFromBoard(username, componentID, component.getClockwiseRotation(), connectors);
                            eventCallback.trigger(pickedCabinFromBoard);
                        }
                        case STORAGE -> {
                            PickedStorageFromBoard pickedStorageFromBoard = new PickedStorageFromBoard(username, componentID, component.getClockwiseRotation(), connectors, ((Storage) component).isDangerous(), ((Storage) component).getGoodsCapacity());
                            eventCallback.trigger(pickedStorageFromBoard);
                        }
                        case BROWN_LIFE_SUPPORT, PURPLE_LIFE_SUPPORT -> {
                            PickedLifeSupportFromBoard pickedLifeSupportFromBoard = new PickedLifeSupportFromBoard(username, componentID, component.getClockwiseRotation(), connectors, component.getComponentType() == ComponentType.BROWN_LIFE_SUPPORT ? 1 : 2);
                            eventCallback.trigger(pickedLifeSupportFromBoard);
                        }
                        case BATTERY -> {
                            PickedBatteryFromBoard pickedBatteryFromBoard = new PickedBatteryFromBoard(username, componentID, component.getClockwiseRotation(), connectors, ((Battery) component).getEnergyNumber());
                            eventCallback.trigger(pickedBatteryFromBoard);
                        }
                        case SHIELD -> {
                            PickedShieldFromBoard pickedShieldFromBoard = new PickedShieldFromBoard(username, componentID, component.getClockwiseRotation(), connectors);
                            eventCallback.trigger(pickedShieldFromBoard);
                        }
                        case CONNECTORS -> {
                            PickedConnectorsFromBoard pickedConnectorsFromBoard = new PickedConnectorsFromBoard(username, componentID, component.getClockwiseRotation(), connectors);
                            eventCallback.trigger(pickedConnectorsFromBoard);
                        }
                    }
                } else {
                    PickedTile pickedTileEvent = new PickedTile(player.getUsername(), tileID);
                    eventCallback.trigger(pickedTileEvent);
                }
            }
            case 1 -> {
                // Get the tile from the reserve
                component = player.getSpaceShip().peekReservedComponent(tileID);
                PickedTileFromReserve pickTileEvent = new PickedTileFromReserve(player.getUsername(), tileID, component.getClockwiseRotation());
                eventCallback.trigger(pickTileEvent);
            }
            case 2 -> {
                // Get the last placed component
                component = player.getSpaceShip().getLastPlacedComponent();
                player.getSpaceShip().destroyComponent(component.getRow(), component.getColumn());
                PickedTileFromSpaceship pickTileEvent = new PickedTileFromSpaceship(player.getUsername(), component.getClockwiseRotation());
                eventCallback.trigger(pickTileEvent);
            }
            default -> throw new IllegalStateException("Invalid fromWhere value");
        }

        // Check if the component is null or if the ID of the component is not the same as the tileID
        if (tileID != -1 && component.getID() != tileID) {
            throw new IllegalStateException("No tile matching the ID found in fromWhere" + fromWhere);
        }

        // Put the tile in the player's hand
        playersHandQueue.put(player.getColor(), component);
    }

    /**
     * Implementation of the method to place a tile in the board, reserve or spaceship.
     * @see State#placeTile(PlayerData, int, int, int)
     */
    @Override
    public void placeTile(PlayerData player, int toWhere, int row, int col) {
        // Check if the player has finished building
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
            throw new IllegalStateException("Player has finished building");
        }

        if (toWhere == 2 && (row < 0 || row >= SpaceShip.getRows() || col < 0 || col >= SpaceShip.getCols())) {
            throw new IllegalArgumentException("Invalid row or column for placing the tile in the spaceship");
        }

        // Has the player a tile in his hand?
        Component component = playersHandQueue.get(player.getColor());
        if (component == null) {
            throw new IllegalStateException("Player has no tile in his hand");
        }

        SpaceShip ship = player.getSpaceShip();

        Component lastPlacedComponent = ship.getLastPlacedComponent();
        if (lastPlacedComponent != null && ship.getLastPlacedComponent().getID() == component.getID()) {
            ship.cleanLastPlacedComponent();
        }

        switch (toWhere) {
            case 0 -> {
                // Place the tile in the board
                if (ship.peekReservedComponent(component.getID()) != null) {
                    throw new IllegalStateException("Cannot put in the pile a reserved component");
                }
                board.putTile(component);
                PlacedTileToBoard placeTileEvent = new PlacedTileToBoard(player.getUsername());
                eventCallback.trigger(placeTileEvent);
            }
            case 1 -> {
                // Place the tile in the reserve
                if (ship.peekReservedComponent(component.getID()) == null) {
                    ship.putReserveComponent(component);
                }
                PlacedTileToReserve placeTileEvent = new PlacedTileToReserve(player.getUsername());
                eventCallback.trigger(placeTileEvent);
            }
            case 2 -> {
                // Place the tile in the spaceship
                if (ship.peekReservedComponent(component.getID()) != null) {
                    ship.removeReserveComponent(component.getID());
                }
                ship.placeComponent(component, row, col);
                PlacedTileToSpaceship placeTileEvent = new PlacedTileToSpaceship(player.getUsername(), row, col);
                eventCallback.trigger(placeTileEvent);
            }
            default -> throw new IllegalStateException("Invalid toWhere value");
        }

        // Remove the tile from the player's hand
        playersHandQueue.remove(player.getColor());
    }

    /**
     * Implementation of the method to rotate a tile in the board.
     * @see State#rotateTile(PlayerData)
     */
    @Override
    public void rotateTile(PlayerData player) {
        // Check if the player has finished building
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
            throw new IllegalStateException("Player has finished building");
        }

        // Has the player a tile in his hand?
        Component component = playersHandQueue.get(player.getColor());
        if (component == null) {
            throw new IllegalStateException("Player has no tile in his hand");
        }

        // Rotate the tile in the board
        component.rotateClockwise();

        int[] connectors = new int[4];
        for (int i = 0; i < 4; i++) {
            connectors[i] = component.getConnection(i).getValue();
        }

        RotatedTile rotatedTileEvent = new RotatedTile(player.getUsername(), component.getID(), connectors);
        eventCallback.trigger(rotatedTileEvent);
    }

    /**
     * The entry method in this state is called when the state is entered.
     * <p>
     * In this state we have to remove all the players from the board since they are in a casual order
     * after the {@link LobbyState} state. To do so we call the {@link Board#clearInGamePlayers()} (PlayerData)}.
     * <p>
     * This can be done because we have the list of players in the
     * {@link State#players} attribute set in the {@link State(Board)} constructor.
     * @see State#entry()
     */
    @Override
    public void entry() {
        super.board.clearInGamePlayers();
        if (super.board.getBoardLevel() == Level.SECOND) {
            this.flipTimer(null);
        }
        for (PlayerData player : players) {
            playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        }
    }

    @Override
    public void execute(PlayerData playerData) {
        super.execute(playerData);
        if (numberOfTimerFlips == 3 && !timerRunning) {
            flipTimer(playerData);
        }
        super.nextState(GameState.VALIDATION);
    }

    @Override
    public void exit() {
        super.exit();
    }
}
