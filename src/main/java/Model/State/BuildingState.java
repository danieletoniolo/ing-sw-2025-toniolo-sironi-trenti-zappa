package Model.State;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import Model.State.interfaces.Buildable;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class BuildingState extends State implements Buildable {
    private Timer timer;
    private boolean timerRunning;
    private int numberOfTimerFlips;
    private static long timerDuration = 90000;

    private Map<PlayerColor, Component> playersHandQueue;

    public BuildingState(Board board) {
        super(board);
        this.timer = new Timer();
        this.numberOfTimerFlips = 0;
        this.timerRunning = false;
    }

    public void flipTimer(UUID uuid) throws IllegalStateException{
        if (board.getBoardLevel() == Level.LEARNING) {
            throw new IllegalStateException("Cannot flip timer in learning level");
        }

        if (timerRunning) {
            throw new IllegalStateException("Cannot flip timer because is already running");
        }

        numberOfTimerFlips++;
        switch (numberOfTimerFlips) {
            case 1:
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
            case 2:
                // This is the second flit that can be done by anyone after the time has run out
                timerRunning = true;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO: notify the player that the timer is flipped
                        timerRunning = false;
                    }
                }, timerDuration);
                break;
            case 3:
                // This is the third flip that can be done only by the player who has finished building

                PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));
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
    }

    public void showDeck(UUID uuid, int deckIndex) {
        // Get the player who is showing the deck
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));

        // Check if the player has placed at least one tile
        if (player.getSpaceShip().getNumberOfComponents() < 1) {
            throw new IllegalStateException("Player has not placed any tile");
        }

        // TODO: we have to decide how we want to notify the client that the deck is shown
        board.getDeck(deckIndex, player);
    }

    public void leaveDeck(UUID uuid, int deckIndex) {
        // Get the player who is showing the deck
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));

        // Leave the deck
        // TODO: we miss the method to leave the deck
    }

    /**
     * Place the marker of the player in the board
     * @param uuid UUID of the player who is placing the marker
     * @param position the position where the player is placing the marker
     */
    public void placeMarker(UUID uuid, int position) throws IllegalStateException {
        // TODO: check if the player has to met more conditions
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));
        board.setPlayer(player, position);
    }

    /**
     * Pick a tile from the pile and put it in the player's hand
     * @param uuid UUID of the player who is picking the tile
     * @param tileID ID of the tile to be picked
     */
    public void pickTileFromPile(UUID uuid, int tileID) {
        // Get the player who is placing the tile
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));

        // Check if the player has finished building
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
            throw new IllegalStateException("Player has finished building");
        }

        // Get the tile from the pile
        Component component = board.popTile(tileID);

        // Put the tile in the player's hand
        if (component != null) {
            playersHandQueue.put(player.getColor(), component);
        } else {
            throw new IllegalStateException("Tile not found in pile");
        }
    }

    /**
     * Pick a tile from the reserve and put it in the player's hand
     * @param uuid UUID of the player who is picking the tile
     * @param tileID ID of the tile to be picked
     */
    public void pickTileFromReserve(UUID uuid, int tileID) {
        // Get the player who is placing the tile
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));

        // Check if the player has finished building
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
            throw new IllegalStateException("Player has finished building");
        }

        // Get the tile from the reserve
        Component component = player.getSpaceShip().getReservedComponents().stream()
                .filter(c -> c.getID() == tileID)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tile not found in reserve"));

        // Put the tile in the player's hand
        playersHandQueue.put(player.getColor(), component);
    }

    public void pickTileFromBoard(UUID uuid, int tileID) {
        // Get the player who is placing the tile
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));

        // Check if the player has finished building
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
            throw new IllegalStateException("Player has finished building");
        }

        // Get the last placed component
        Component component = player.getSpaceShip().getLastPlacedComponent();

        // Put the tile in the player's hand
        if (component != null && component.getID() == tileID) {
            playersHandQueue.put(player.getColor(), component);
        } else {
            throw new IllegalStateException("No tile matching the ID found in the last placed component");
        }
    }

    /**
     * Leave the tile the player has in his hand and put it in the board or in the reserve if it was from the reserve
     * @param uuid UUID of the player who is leaving the tile
     */
    public void leaveTile(UUID uuid) {
        // Get the player who is placing the tile
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));

        // Check if the player has finished building
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
            throw new IllegalStateException("Player has finished building");
        }

        // Has the player a tile in his hand?
        Component component = playersHandQueue.get(player.getColor());
        if (component == null) {
            throw new IllegalStateException("Player has no tile in his hand");
        }

        // If the tile was not from the reserve it must be put back in the board
        if (!player.getSpaceShip().getReservedComponents().contains(component)) {
            board.putTile(component.getID(), component);
        }

        // Remove the tile from the player's hand
        playersHandQueue.remove(player.getColor());
    }

    /**
     * Place the tile the player has in his hand in the board
     * @param uuid UUID of the player who is placing the tile
     * @param row row where the player is placing the tile
     * @param col column where the player is placing the tile
     */
    public void placeTile(UUID uuid, int row, int col) {
        // Get the player who is placing the tile
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));

        // Check if the player has finished building
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
            throw new IllegalStateException("Player has finished building");
        }

        // Has the player a tile in his hand?
        Component component = playersHandQueue.get(player.getColor());
        if (component == null) {
            throw new IllegalStateException("Player has no tile in his hand");
        }

        // Place the tile in the board
        player.getSpaceShip().placeComponent(component, row, col);

        // If the tile was from the reserve, remove it from the reserve
        if (player.getSpaceShip().getReservedComponents().contains(component)) {
            player.getSpaceShip().unreserveComponent(component);
        }
    }

    /**
     * Put the tile the player has in his hand and put it in the reserved tiles of the player
     * @param uuid UUID of the player who is reserving the tile
     */
    public void reserveTile(UUID uuid) {
        // Get the player who is reserving the tile
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));

        // Check if the player has finished building
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
            throw new IllegalStateException("Player has finished building");
        }

        // Has the player a tile in his hand?
        Component component = playersHandQueue.get(player.getColor());
        if (component == null) {
            throw new IllegalStateException("Player has no tile in his hand");
        }

        // Reserve the tile in the board
        player.getSpaceShip().reserveComponent(component);
    }

    /**
     * Rotate the tile the player has in his hand
     * @param uuid UUID of the player who is rotating the tile
     */
    public void rotateTile(UUID uuid) {
        // Get the player who is rotating the tile
        PlayerData player = players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Player not found"));

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

    // TODO: Could be that we pass the UUID instead of the player because this state is not played in turn
    public void pickTile(PlayerData player, int tileID) {
        // Check if the player has already picked a tile
        if (playersHandQueue.containsKey(player.getColor())) {
            throw new IllegalStateException("Player has already picked a tile");
        }

        Component component = board.popTile(tileID);
        if (component == null) {
            component = player.getSpaceShip().getLastPlacedComponent();
            if (component == null || component.getID() != tileID) {
                for (Component[] row : player.getSpaceShip().getComponents()) {
                    for (Component c : row) {
                        if (c != null && c.getID() == tileID) {
                            component = c;
                            break;
                        }
                    }
                    if (component != null) break;
                }
            }
        }
        playersHandQueue.put(player.getColor(), component);
   }

    @Override
    public void entry() {
        super.entry();
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
