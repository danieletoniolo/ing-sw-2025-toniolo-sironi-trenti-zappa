package it.polimi.ingsw.model.state;

import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.controller.EventCallback;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class State implements Serializable {
    protected final EventCallback eventCallback;
    protected ArrayList<PlayerData> players;
    protected Map<PlayerColor, PlayerStatus> playersStatus;
    protected Board board;
    protected Boolean played;


    /**
     * Enum to represent the status of the player in the state.
     */
    protected enum PlayerStatus {
        WAITING,
        PLAYING,
        PLAYED,
        SKIPPED
    }

    /**
     * Constructor for State
     *
     * @param board Board associated with the game
     * @throws NullPointerException if board is null
     */
    public State(Board board, EventCallback eventCallback) throws NullPointerException {
        if (board == null) {
            throw new NullPointerException("board is null");
        }
        this.board = board;
        this.eventCallback = eventCallback;
        this.players = board.getInGamePlayers();
        this.playersStatus = new HashMap<>();
        for (PlayerData player : players) {
            this.playersStatus.put(player.getColor(), PlayerStatus.WAITING);
        }
        this.played = false;
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
    public void entry() {
    }

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
     *
     * @param player PlayerData of the player who is joining or leaving the lobby.
     * @param type   Type of the operation: 0 = join, 1 = leave.
     */
    public void manageLobby(PlayerData player, int type) {
        throw new IllegalStateException("Cannot manage lobby in this state");
    }

    /* GameState methods */

    /**
     * Picks a tile from the board, reserve or spaceship.
     *
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
     *
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
     *
     * @param player          PlayerData of the player who is using the cannons or engines.
     * @param type            Type of the extra strength to use: 0 = engine, 1 = cannon.
     * @param IDs             List of Integers representing the ID of the engines or cannons from which we take the energy to use.
     * @param batteriesID     List of Integers representing the batteryID from which we take the energy to use the cannon or engine
     *                        (we use one energy from each batteryID in the list).
     * @throws IllegalStateException if the state does not allow using extra power.
     */
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException {
        throw new IllegalStateException("Cannot use extra power in this state");
    }

    /**
     * Set the goods, batteries or crew members to discard to serve the penalty.
     *
     * @param player      PlayerData of the player who is discarding the goods.
     * @param type        Type of the penalty: 0 = goods, 1 = batteries, 2 = crew members.
     * @param penaltyLoss List of Integers representing the ID of storage, batteries or cabins from
     *                    which we take the penalty to serve.
     *                    We pick one from each ID in the list, in case of goods we pick the most valuable ones.
     * @throws IllegalStateException if there is no penalty to serve in this state.
     */
    public void setPenaltyLoss(PlayerData player, int type, List<Integer> penaltyLoss) throws IllegalStateException {
        throw new IllegalStateException("Cannot set penalty loss in this state");
    }

    /**
     * Select a planet to land on.
     *
     * @param player       PlayerData of the player who is selecting the planet.
     * @param planetNumber Number of the planet to select.
     * @throws IllegalStateException if the state does not allow selecting a planet.
     */
    public void selectPlanet(PlayerData player, int planetNumber) throws IllegalStateException {
        throw new IllegalStateException("Cannot select planet in this state");
    }

    /**
     * Select which fragment of the ship to preserve
     *
     * @param fragmentChoice
     * @throws IllegalStateException
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        throw new IllegalStateException("Cannot set fragment choice in this state");
    }

    /**
     * Set component to destroy in the ship.
     *
     * @param player              PlayerData of the player who is destroying the component.
     * @param componentsToDestroy List of pairs representing the coordinates of the components to destroy.
     * @throws IllegalStateException    if the state does not allow destroying components.
     * @throws IllegalArgumentException if the coordinates are invalid.
     */
    public void setComponentToDestroy(PlayerData player, List<Pair<Integer, Integer>> componentsToDestroy) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("Cannot set component to destroy in this state");
    }

    /**
     * Roll the two dice.
     *
     * @throws IllegalStateException if the state does not allow rolling the dice.
     */
    public void rollDice() throws IllegalStateException {
        throw new IllegalStateException("Cannot roll dice in this state");
    }

    /**
     * Set the protect mode for the player.
     *
     * @param player    PlayerData of the player who is setting the protect mode.
     * @param batteryID ID of the battery to use for the protect mode.
     * @throws IllegalStateException    if the state does not allow setting the protect mode.
     * @throws IllegalArgumentException if the batteryID is invalid.
     */
    public void setProtect(PlayerData player, int batteryID) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("Cannot set protect mode in this state");
    }

    /**
     * Set the goods to exchange (the goods to leave and the goods to get).
     * @param player PlayerData of the player who is exchanging the goods.
     * @param exchangeData contains an List of triplets, each triplet contains (in this order):
     * the goods that the player wants to get, the good that the player wants to leave, the storage ID.
     * @throws IllegalStateException if the state does not allow setting the goods to exchange.
     * @throws IllegalArgumentException if the exchangeData is invalid.
     */
    public void setGoodsToExchange(PlayerData player, List<Triplet<List<Good>, List<Good>, Integer>> exchangeData) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("Cannot set good to exchange mode in this state");
    }

    /**
     * Swap goods between two storage.
     * @param player PlayerData of the player who is swapping the goods.
     * @param storageID1 ID of the first storage.
     * @param storageID2 ID of the second storage.
     * @param goods1to2 List of goods to exchange from storage 1 to storage 2.
     * @param goods2to1 List of goods to exchange from storage 2 to storage 1.
     * @throws IllegalStateException if the state does not allow swapping goods.
     * @throws IllegalArgumentException if the storageID is invalid or if the goods are not in the storage.
     */
    public void swapGoods(PlayerData player, int storageID1, int storageID2, List<Good> goods1to2, List<Good> goods2to1) throws IllegalStateException {
        throw new IllegalStateException("Cannot set good to swap mode in this state");
    }
}