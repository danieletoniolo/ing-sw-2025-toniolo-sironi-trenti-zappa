package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import controller.EventManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum PlayerStatus {
    WAITING,
    PLAYING,
    PLAYED,
    SKIPPED
}

public abstract class State {
    protected ArrayList<PlayerData> players;
    protected Map<PlayerColor, PlayerStatus> playersStatus;
    protected Board board;
    protected Boolean played;
    protected final EventManager eventManager;

    /**
     * Constructor for State
     *
     * @param board Board associated with the game
     * @throws NullPointerException if board is null
     */
    public State(Board board) throws NullPointerException {
        if (board == null) {
            throw new NullPointerException("board is null");
        }
        this.board = board;
        this.players = board.getInGamePlayers();
        this.playersStatus = new HashMap<>();
        for (PlayerData player : players) {
            this.playersStatus.put(player.getColor(), PlayerStatus.WAITING);
        }
        this.played = false;
        this.eventManager = new EventManager();
    }

    // TODO: If the method is used only in pirates state, remove it from here

    /**
     * Check if all players have played
     *
     * @return Boolean value if all players have played
     */
    protected boolean haveAllPlayersPlayed() {
        for (PlayerData p : players) {
            if (playersStatus.get(p.getColor()) != PlayerStatus.PLAYED && playersStatus.get(p.getColor()) != PlayerStatus.SKIPPED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Set the status of all players
     *
     * @param status PlayerStatus to set to all players
     */
    protected void setStatusPlayers(PlayerStatus status) {
        for (PlayerData p : players) {
            playersStatus.put(p.getColor(), status);
        }
    }

    /**
     * Get the player who has not played yet (current player to play)
     *
     * @return PlayerData of the current player that is playing
     * @throws IllegalStateException if all players have played
     */
    public PlayerData getCurrentPlayer() throws IllegalStateException {
        for (PlayerData player : players) {
            if (playersStatus.get(player.getColor()) == PlayerStatus.WAITING) {
                return player;
            }
        }
        throw new IllegalStateException("All players have played");
    }

    /**
     * Make the player playing in the state
     *
     * @param player PlayerData of the player which is playing
     * @throws NullPointerException player == null
     */
    public void play(PlayerData player) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        playersStatus.replace(player.getColor(), PlayerStatus.PLAYING);
    }

    /**
     * Execute at the beginning of the state
     */
    public void entry() {}

    /**
     * Make the player play in the state
     *
     * @param player PlayerData of the player to play
     * @return Pair of EventType and Object which contains the record that will be sent to the client. In the super.execute(PlayerData player) method we return null
     * @throws NullPointerException player == null
     */
    public void execute(PlayerData player) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }

        PlayerStatus playerStatus = playersStatus.get(player.getColor());
        if (playerStatus == PlayerStatus.PLAYING) {
            playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
        } else {
            playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
        }
    }

    /**
     * Check if all players have played
     *
     * @throws IllegalStateException if not all players have played
     */
    public void exit() throws IllegalStateException {
        for (PlayerData p : players) {
            if (playersStatus.get(p.getColor()) == PlayerStatus.WAITING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        this.played = true;
        board.refreshInGamePlayers();
    }

    /* LobbyState methods */

    /**
     * Manage the joining and leaving of the player from the lobyy
     * @param player PlayerData of the player who is joining or leaving the lobby.
     * @param type Type of the operation: 0 = join, 1 = leave.
     */
    public void manageLobby(PlayerData player, int type) {
        throw new IllegalStateException("Cannot manage lobby in this state");
    }

    /* GameState methods */

    /**
     * Picks a tile from the board, reserve or spaceship.
     * @param player    PlayerData of the player who is picking the tile.
     * @param fromWhere Where the tile is being picked from: 0 = board, 1 = reserve, 2 = spaceship.
     * @param tileID    ID of the tile being picked.
     * @throws IllegalStateException if the state does not allow picking a tile.
     */
    public void pickTile(PlayerData player, int fromWhere, int tileID) throws IllegalStateException {
        throw new IllegalStateException("Cannot pick tile in this state");
    }

    /**
     * Places a tile on the board, reserve or spaceship.
     *
     * @param player  PlayerData of the player who is placing the tile.
     * @param toWhere Where the tile is being placed: 0 = board, 1 = reserve, 2 = spaceship.
     * @param row     Row of the tile being placed (Just for the Spaceship).
     * @param col     Column of the tile being placed (Just for the Spaceship).
     * @throws IllegalStateException if the state does not allow placing a tile.
     * @see BuildingState#placeTile(PlayerData, int, int, int)
     */
    public void placeTile(PlayerData player, int toWhere, int row, int col) throws IllegalStateException {
        throw new IllegalStateException("Cannot place tile in this state");
    }

    /**
     * Get or leave a deck from the board.
     *
     * @param player    PlayerData of the player who is getting or leaving the deck.
     * @param usage     The usage of the deck: 0 = get, 1 = leave.
     * @param deckIndex Index of the deck being used.
     * @throws IllegalStateException if the state does not allow getting or leaving a deck.
     * @see BuildingState#useDeck(PlayerData, int, int)
     */
    public void useDeck(PlayerData player, int usage, int deckIndex) throws IllegalStateException {
        throw new IllegalStateException("Cannot use deck in this state");
    }

    /**
     * Rotates a tile in the player's hand.
     *
     * @param player PlayerData of the player who is rotating the tile.
     * @throws IllegalStateException if the state does not allow rotating a tile.
     * @see BuildingState#rotateTile(PlayerData)
     */
    public void rotateTile(PlayerData player) throws IllegalStateException {
        throw new IllegalStateException("Cannot rotate tile in this state");
    }

    /**
     * Flips the timer of the building phase.
     *
     * @param player PlayerData of the player who is flipping the timer.
     * @throws IllegalStateException if the state does not allow flipping the timer.
     * @see BuildingState#flipTimer(PlayerData)
     */
    public void flipTimer(PlayerData player) throws IllegalStateException {
        throw new IllegalStateException("Cannot flip timer in this state");
    }

    /**
     * Places a marker on the board.
     *
     * @param player   PlayerData of the player who is placing the marker.
     * @param position Position of the marker on the board.
     * @throws IllegalStateException if the state does not allow placing a marker.
     * @see BuildingState#placeMarker(PlayerData, int)
     */
    public void placeMarker(PlayerData player, int position) throws IllegalStateException {
        throw new IllegalStateException("Cannot place marker in this state");
    }

    /**
     * Adds or removes a crew member from the player's ship.
     * @param player   PlayerData of the player who is managing the crew member.
     * @param mode     Mode of the operation: 0 = add, 1 = remove.
     * @param crewType Type of crew member to manage: 0 = crew, 1 = brown alien, 2 = purple alien.
     * @param cabinID  ID of the cabin where the crew member will be managed.
     * @throws IllegalStateException if the state does not allow managing a crew member.
     */
    public void manageCrewMember(PlayerData player, int mode, int crewType, int cabinID) throws IllegalStateException {
        throw new IllegalStateException("Cannot manage crew member in this state");
    }

    /**
     * Use engines or cannons to add strength to the current ship stats.
     * @param player PlayerData of the player who is using the cannons or engines.
     * @param type Type of the extra strength to use: 0 = engine, 1 = cannon.
     * @param extraPowerToUse Amount of extra power to use.
     * @param batteriesID List of Integers representing the batteryID from which we take the energy to use the cannon or engine
     *                    (we use one energy from each batteryID in the list).
     * @throws IllegalStateException if the state does not allow using extra power.
     */
    public void useExtraStrength(PlayerData player, int type, float extraPowerToUse, List<Integer> batteriesID) throws IllegalStateException {
        throw new IllegalStateException("Cannot use extra power in this state");
    }
}