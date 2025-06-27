package it.polimi.ingsw.model.game.board;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardsManager;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.TilesManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.utils.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a game board that manages the game state, including players, cards, and tiles.
 * The board handles different game levels and maintains the positions of players during gameplay.
 * @author Vittorio Sironi
 */
public class Board implements Serializable {
    /** The difficulty level of the current game */
    private final Level level;
    /** The number of steps required to complete one lap around the board */
    private final int stepsForALap;

    /** Array of card decks available on the board (null for LEARNING level) */
    private final Deck[] decks;
    /** Stack containing all shuffled cards for drawing during the game */
    private final Stack<Card> shuffledDeck;

    /** List of component tiles that are visible and available for players to take */
    private final ArrayList<Component> viewableTiles;
    /** List of component tiles that are hidden and not yet revealed */
    private final ArrayList<Component> hiddenTiles;

    /** List of players currently participating in the game */
    private final ArrayList<PlayerData> inGamePlayers;
    /** List of players who have given up or been eliminated from the game */
    private final ArrayList<PlayerData> gaveUpPlayers;


    /**
     * Create a new board
     * @param level the level of the board
     *
     * @throws IllegalArgumentException if the level is set to an unexpected value
     */
    public Board(Level level) throws IllegalArgumentException, JsonProcessingException {
        this.level = level;

        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }

        switch (level) {
            case LEARNING:
                this.decks = null;
                this.shuffledDeck = CardsManager.createLearningDeck();
                this.stepsForALap = 18;
                break;
            case SECOND:
                this.decks = CardsManager.createDecks(level);
                this.shuffledDeck = CardsManager.createShuffledDeck(this.decks);
                this.stepsForALap = 24;
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + level);
        }

        this.hiddenTiles = TilesManager.getTiles();
        this.viewableTiles = new ArrayList<>();
        inGamePlayers = new ArrayList<>();
        gaveUpPlayers = new ArrayList<>();
    }

    /**
     * Get the number of position for a lap
     * @return the number of position for a lap
     */
    public int getStepsForALap() {
        return stepsForALap;
    }

    /**
     * Retrieves the level of the board.
     * @return the board level
     */
    public Level getBoardLevel() {
        return this.level;
    }

    /**
     * Retrieves the number of hidden tiles on the board.
     * @return the number of hidden tiles
     */
    public int getNumberOfHiddenTiles() {
        return this.hiddenTiles.size();
    }

    /**
     * Retrieves the decks of the board.
     * @return the decks of the board
     */
    public Deck[] getDecks() {
        return this.decks;
    }

    /**
     * Retrieves the deck at the specified index if it is pickable.
     * @param index the index of the deck to retrieve
     * @return the deck at the specified index
     * @throws IllegalStateException if the deck at the specified index is not pickable
     */
    public Deck getDeck(int index, PlayerData player) throws IllegalStateException, NullPointerException, IndexOutOfBoundsException {
        if (level == Level.LEARNING) {
            throw new IllegalStateException("There is no deck in the learning level");
        }
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        if (index < 0 || index >= this.decks.length) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        if (!this.decks[index].isPickable() || player.getSpaceShip().getNumberOfComponents() <= 1) {
            throw new IllegalStateException("Deck is not pickable or player has not enough components");
        }

        this.decks[index].setPickable(false);
        return this.decks[index];
    }

    /**
     * Allows a player to leave a deck, making it pickable again.
     * @param index the index of the deck to leave
     * @param player the player who is leaving the deck
     * @return the deck that was left
     * @throws IllegalStateException if the level is LEARNING or if the deck is already pickable
     * @throws NullPointerException if player is null
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public Deck leaveDeck(int index, PlayerData player) throws IllegalStateException, NullPointerException, IndexOutOfBoundsException {
        if (level == Level.LEARNING) {
            throw new IllegalStateException("There is no deck in the learning level");
        }
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        if (index < 0 || index >= this.decks.length) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        if (this.decks[index].isPickable()) {
            throw new IllegalStateException("You cannot leave a deck that is already on the board");
        }

        this.decks[index].setPickable(true);
        return this.decks[index];
    }

    /**
     * Puts a tile in the board.
     * @param tile the tile to put in the viewable tiles list
     * @throws IllegalStateException if the tile is already in the board
     */
    public void putTile(Component tile) throws IllegalStateException {
        if (tile == null) {
            throw new NullPointerException("Tile is null");
        }
        if (viewableTiles.contains(tile)) {
            throw new IllegalStateException("Tile is already in the board");
        }

        viewableTiles.add(tile);
    }

    /**
     * Pops a tile from the board (if index is -1, it pops a random tile from the hidden tiles list).
     * @param tileID the index of the tile to pop from the viewable tiles list (if index is -1, it pops a random tile from the hidden tiles list)
     * @return the tile popped from the board
     * @throws IndexOutOfBoundsException if the tileID is out of bounds or if there are no more hidden tiles
     */
    public Component popTile(int tileID) throws IndexOutOfBoundsException {
        if (tileID == -1) {
            if (hiddenTiles.isEmpty()) {
                throw new IndexOutOfBoundsException("There are no more hidden tiles");
            }
            Random random = new Random();
            tileID = random.nextInt(hiddenTiles.size());
            return hiddenTiles.remove(tileID);
        }

        for (Component tile : viewableTiles) {
            if (tile.getID() == tileID) {
                viewableTiles.remove(tile);
                return tile;
            }
        }
        throw new IndexOutOfBoundsException("Tile with ID " + tileID + " not found in viewable tiles");
    }

    /**
     * Draws a card from the shuffled deck.
     * @return the card drawn from the deck
     * @throws IllegalStateException if there are no more cards in the deck
     */
    public Card drawCard() throws IllegalStateException {
        if (this.shuffledDeck.isEmpty())
            throw new IllegalStateException("No more cards in the deck");
        return shuffledDeck.pop();
    }

    /**
     * Get the shuffled deck
     * @return the shuffled deck
     */
    public Stack<Card> getShuffledDeck() {
        return shuffledDeck;
    }

    /**
     * Initialize the players on the board
     * @param player player to set
     * @param position position where the player has chosen to start: 0 = leader, 1 = second, etc.
     * @throws NullPointerException if player == null
     * @throws IndexOutOfBoundsException if the position is out of bounds
     * @throws IllegalStateException if the position is already set by another player
     */
    public void setPlayer(PlayerData player, int position) throws NullPointerException, IndexOutOfBoundsException, IllegalStateException {
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        if (((position < 0 && level == Level.SECOND) || (position < -1 && level == Level.LEARNING)) ||
                position >= 4) {
            throw new IndexOutOfBoundsException("The position is not acceptable");
        }
        if (position != -1 && inGamePlayers.get(position) != null) {
            throw new IllegalStateException("There is already a player in this position");
        }
        if (level == Level.LEARNING) {
            int firstFreePosition = 0;
            for (PlayerData p : inGamePlayers) {
                if (p != null && p.getPosition() != -1) {
                    firstFreePosition++;
                }
            }

            if (position > firstFreePosition) {
                throw new IllegalStateException("You have to choose the first free position");
            }
        }

        switch (level) {
            case LEARNING:
                switch (position) {
                    case 0 -> player.setStep(4);
                    case 1 -> player.setStep(2);
                    case 2 -> player.setStep(1);
                    case 3 -> player.setStep(0);
                }
                break;
            case SECOND:
                switch (position) {
                    case 0 -> player.setStep(6);
                    case 1 -> player.setStep(3);
                    case 2 -> player.setStep(1);
                    case 3 -> player.setStep(0);
                }
                break;
        }

        if (position != -1) {
            inGamePlayers.set(position, player);
        } else {
            int positionToReset = player.getPosition();
            inGamePlayers.set(positionToReset, null);
            for (int i = 0; i < inGamePlayers.size(); i++) {
                if (inGamePlayers.get(i) != null && inGamePlayers.get(i).getPosition() > positionToReset) {
                    int newPosition = i - 1;
                    PlayerData p = inGamePlayers.get(i);

                    inGamePlayers.set(i, null);
                    p.setPosition(newPosition);
                    setPlayer(p, newPosition);
                }
            }
        }
    }

    /**
     * Update the status of players: 1 - players who are giveUp are moved to gaveUpPlayers, 2 - Set the correct position to the players, 3 - Sort the players by their position:
     * (first of the list is the leader).
     */
    public void refreshInGamePlayers() {
        inGamePlayers.removeIf(Objects::isNull);
        for (PlayerData inGamePlayer : inGamePlayers) {
            if (inGamePlayer.hasGivenUp()) {
                gaveUpPlayers.add(inGamePlayer);
            }
        }

        for (PlayerData player : inGamePlayers) {
            if (inGamePlayers.getFirst().getStep() - player.getStep() > this.stepsForALap) {
                gaveUpPlayers.add(player);
            }
        }

        for (PlayerData player : gaveUpPlayers) {
            inGamePlayers.remove(player);
        }

        for (int i = 0; i < inGamePlayers.size(); i++) {
            for (int j = i + 1; j < inGamePlayers.size(); j++) {
                if (inGamePlayers.get(i).getStep() < inGamePlayers.get(j).getStep()) {
                    Collections.swap(inGamePlayers, i, j);
                }
            }
            inGamePlayers.get(i).setPosition(i);
        }
    }

    /**
     * Moves the player on the board: two player are never on the same step
     * @param player player to move
     * @param steps number of position that moves the player: position > 0 = player moves forth, position < 0: players moves back, position = 0, player doesn't move
     * @throws NullPointerException if player == null
     */
    public void addSteps(PlayerData player, int steps) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("Player is null");
        }

        int i = 0;
        while (i < Math.abs(steps)) {
            if (steps > 0) player.setStep(player.getStep() + 1);
            else player.setStep(player.getStep() - 1);
            boolean found = false;
            for (PlayerData p : inGamePlayers) {
                if (!p.equals(player) && p.getStep() == player.getStep()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                i++;
            }
        }
    }

    /**
     * get the players who are in game
     * @return the players who are in game
     */
    public ArrayList<PlayerData> getInGamePlayers() {
        return inGamePlayers;
    }

    /**
     * get the players who have given up
     * @return the players who have given up
     */
    public ArrayList<PlayerData> getGaveUpPlayers() {
        return gaveUpPlayers;
    }

    /**
     * Add the player to the inGamePlayers list
     * @param player player to add
     */
    public void addInGamePlayers(PlayerData player) {
        inGamePlayers.add(player);
    }

    /**
     * Remove the player from the inGamePlayers list
     * @param player player to remove
     */
    public void removeInGamePlayer(PlayerData player) {
        inGamePlayers.remove(player);
    }

    /**
     * Clear the inGamePlayers list and set it to null
     */
    public void clearInGamePlayers() {
        inGamePlayers.clear();
        for (int i = 0; i < 4; i++) {
            inGamePlayers.add(null);
        }
    }
}