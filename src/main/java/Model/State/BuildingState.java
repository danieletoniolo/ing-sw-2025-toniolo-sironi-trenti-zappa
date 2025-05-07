package Model.State;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;

import java.util.*;


public class BuildingState extends State {
    private final Timer timer;
    private boolean timerRunning;
    private int numberOfTimerFlips;
    private static final long timerDuration = 90000;
    private final Map<PlayerColor, Component> playersHandQueue;

    public BuildingState(Board board) {
        super(board);
        this.timer = new Timer();
        this.numberOfTimerFlips = 0;
        this.timerRunning = false;
        this.playersHandQueue = new HashMap<>();
    }

    public boolean getTimerRunning() {
        return timerRunning;
    }

    public Map<PlayerColor, Component> getPlayersHandQueue() {
        return playersHandQueue;
    }

    public int getNumberOfTimerFlips() {
        return numberOfTimerFlips;
    }

    public static long getTimerDuration() {
        return timerDuration;
    }

    /**
     * Implementation of the method to flip the timer.
     * @see State#flipTimer(PlayerData)
     */
    @Override
    public void flipTimer(PlayerData player) throws IllegalStateException{
        if (board.getBoardLevel() == Level.LEARNING) {
            throw new IllegalStateException("Cannot flip timer in learning level");
        }

        if (timerRunning) {
            throw new IllegalStateException("Cannot flip timer because is already running");
        }

        switch (numberOfTimerFlips) {
            case 0:
                // First flip that is done when the building phase starts
                timerRunning = true;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO: notify the player that the timer is flipped
                        timerRunning = false;
                    }
                }, timerDuration);
                break;
            case 1:
                // This is the second flip that can be done by anyone after the time has run out
                timerRunning = true;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO: notify the player that the timer is flipped
                        timerRunning = false;
                    }
                }, timerDuration);
                break;
            case 2:
                // This is the third flip that can be done only by the player who has finished building

                // Check if the player has finished building
                if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
                    timerRunning = true;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Set the status of the player who has not finished building to PLAYED
                            for (PlayerData p: players) {
                                if (playersStatus.get(p.getColor()) != PlayerStatus.PLAYED) {
                                    playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
                                }
                                // If some one has something in his hand it must be added to the lost components
                                if (playersHandQueue.get(p.getColor()) != null) {
                                    p.getSpaceShip().getLostComponents().add(playersHandQueue.get(p.getColor()));
                                }
                            }

                            // TODO: notify the player that the timer is flipped
                            // TODO: handle the case when the player has something in is hand

                            timerRunning = false;
                        }
                    }, timerDuration);
                } else {
                    throw new IllegalStateException("Cannot flip timer because the player has not finished building");
                }

                break;
            default:
                throw new IllegalStateException("Cannot flip timer more than twice");
        }
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

        switch (usage) {
            case 0 -> board.getDeck(deckIndex, player);
            case 1 -> {
                // TODO: we miss the method to leave the deck
            }
        }
    }

    /**
     * Implementation of the method to place a marker on the board.
     * @see State#placeMarker(PlayerData, int)
     */
    @Override
    public void placeMarker(PlayerData player, int position) throws IllegalStateException {
        board.setPlayer(player, position);
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

        Component component = switch (fromWhere) {
            case 0 ->
                // Get the tile from the board
                    board.popTile(tileID);
            case 1 ->
                // Get the tile from the reserve
                    player.getSpaceShip().unreserveComponent(tileID);
            case 2 ->
                // Get the last placed component
                    player.getSpaceShip().getLastPlacedComponent();
            default -> throw new IllegalStateException("Invalid fromWhere value");
        };

        // Check if the component is null or if the ID of the component is not the same as the tileID
        if (component == null || component.getID() != tileID) {
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

        // Has the player a tile in his hand?
        Component component = playersHandQueue.get(player.getColor());
        if (component == null) {
            throw new IllegalStateException("Player has no tile in his hand");
        }

        switch (toWhere) {
            case 0 ->
                // Place the tile in the board
                    board.putTile(component);
            case 1 ->
                // Place the tile in the reserve
                    player.getSpaceShip().reserveComponent(component);
            case 2 ->
                // Place the tile in the spaceship
                    player.getSpaceShip().placeComponent(component, row, col);
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
    }

    /**
     * The entry method in this state is called when the state is entered.
     * <p>
     * In this state we have to remove all the players from the board since they are in a casual order
     * after the {@link LobbyState} state. To do so we call the {@link Board#clearInGamePlayers()} (PlayerData)}.
     * <p>
     * This can be done because we have the list of players in the
     * {@link State#players} attribute set in the {@link State#State(Board)} constructor.
     * @see State#entry()
     */
    @Override
    public void entry() {
        super.entry();

        board.clearInGamePlayers();
    }

    @Override
    public void execute(PlayerData playerData) {
        super.execute(playerData);
    }

    @Override
    public void exit() {
        super.exit();
    }
}
