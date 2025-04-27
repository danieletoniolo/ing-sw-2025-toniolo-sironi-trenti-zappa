package Model.Game.Board;

import Model.Cards.Card;
import Model.Cards.CardsManager;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.Main;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Board {
    private final Level level;
    private final int stepsForALap;

    private final Deck[] decks;
    private final Stack<Card> shuffledDeck;

    private Component[] tiles;

    private ArrayList<PlayerData> inGamePlayers;
    private ArrayList<PlayerData> gaveUpPlayers;


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

        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("Json/Tiles.json");
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found!");
        }
        String json = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        ObjectMapper objectMapper = new ObjectMapper();
        this.tiles = objectMapper.readValue(json, Component[].class);

        inGamePlayers = new ArrayList<>(Arrays.asList(null, null, null, null));
        gaveUpPlayers = new ArrayList<>();
    }

    /**
     * Get the number of steps for a lap
     * @return the number of steps for a lap
     */
    public int getStepsForALap() {
        return stepsForALap;
    }

    public Component[] getTiles() {
        return tiles;
    }

    /**
     * Retrieves the level of the board.
     * @return the board level
     */
    public Level getBoardLevel() {
        return this.level;
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
            throw new IllegalStateException("Deck is not pickable");
        }
        return this.decks[index];
    }

    /**
     * Retrieves the tile with the specified ID.
     * @param ID the ID of the tile to retrieve
     * @return the tile with the specified ID, or null if no tile with that ID exists
     * @throws IndexOutOfBoundsException ID is out of bounds
     */
    public Component popTile(int ID) throws IndexOutOfBoundsException {
        if (ID < 0 || ID >= tiles.length) {
            throw new IndexOutOfBoundsException("ID is out of bounds");
        }
        Component component = tiles[ID];
        tiles[ID] = null;
        return component;
    }

    public void putTile(Component tile) throws IllegalStateException {
        if (tile == null) {
            throw new NullPointerException("Tile is null");
        }
        int ID = tile.getID();
        if (tiles[ID] != null) {
            throw new IllegalStateException("Tile is already in the board");
        }
        tiles[ID] = tile;
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
    public void setPlayer(PlayerData player, int position) throws NullPointerException, IndexOutOfBoundsException, IllegalStateException{
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        if (position < 0 || position >= 4) {
            throw new IndexOutOfBoundsException("The position is not acceptable");
        }
        if (inGamePlayers.get(position) != null) {
            throw new IllegalStateException("There is already a player in this position");
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
        inGamePlayers.set(position, player);
    }

    /**
     * Update the status of players: 1 - players who are giveUp are moved to gaveUpPlayers, 2 - Set the correct position to the players, 3 - Sort the players by their position:
     * (first of the list is the leader)
     * @return ArrayList of sorted players
     */
    public ArrayList<PlayerData> updateInGamePlayers() {
        inGamePlayers.removeIf(Objects::isNull);
        for (int i = 0; i < inGamePlayers.size(); i++) {
            if (inGamePlayers.get(i).hasGivenUp()) gaveUpPlayers.add(inGamePlayers.remove(i));
        }

        for (int i = 0; i < inGamePlayers.size(); i++) {
            for (int j = i + 1; j < inGamePlayers.size(); j++) {
                if (inGamePlayers.get(i).getStep() < inGamePlayers.get(j).getStep()) {
                    Collections.swap(inGamePlayers, i, j);
                }
            }
            inGamePlayers.get(i).setPosition(i);
        }

        for (PlayerData player : inGamePlayers) {
            if (inGamePlayers.getFirst().getStep() - player.getStep() > this.stepsForALap) {
                gaveUpPlayers.add(player);
            }
        }

        for (PlayerData player : gaveUpPlayers) {
            inGamePlayers.remove(player);
        }

        return inGamePlayers;
    }

    /**
     * Moves the player on the board: two player are never on the same step
     * @param player player to move
     * @param steps number of steps that moves the player: steps > 0 = player moves forth, steps < 0: players moves back, steps = 0, player doesn't move
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

    public ArrayList<PlayerData> getGaveUpPlayers() {
        return gaveUpPlayers;
    }

    public Component getTile(int ID) {
        return tiles[ID];
    }
}