package Model.Game.Board;

import Model.Cards.Card;
import Model.Cards.CardsManager;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import Model.State.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.Main;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Board {
    private State state;
    private final Level level;
    private final int stepsForALap;

    private final Deck[] decks;
    private final Stack<Card> shuffledDeck;

    private final Component[] tiles;

    private final PlayerData blue;
    private final PlayerData red;
    private final PlayerData green;
    private final PlayerData yellow;

    // TODO: da sistemare

    /**
     * Create a new board
     * @param level the level of the board
     * @param blue the player data for the blue player
     * @param red the player data for the red player
     * @param green the player data for the green player
     * @param yellow the player data for the yellow player
     * @throws IllegalArgumentException if the level is set to an unexpected value
     */
    public Board(Level level, PlayerData blue, PlayerData red, PlayerData green, PlayerData yellow) throws IllegalArgumentException, JsonProcessingException {
        // TODO: dichiarare state iniziale
        this.state = null;
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

        this.blue = blue;
        this.red = red;
        this.green = green;
        this.yellow = yellow;

        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("Json/Tiles.json");
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found!");
        }
        String json = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        ObjectMapper objectMapper = new ObjectMapper();
        this.tiles = objectMapper.readValue(json, Component[].class);
    }

    /**
     * Get the number of steps for a lap
     * @return the number of steps for a lap
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
     * Retrieves the player data associated with the specified player color.
     * @param playerColor the color of the player to retrieve
     * @return the player data for the specified color, or null if the color is not recognized
     */
    public PlayerData getPlayer(PlayerColor playerColor) {
        return switch (playerColor) {
            case BLUE -> this.blue;
            case RED -> this.red;
            case GREEN -> this.green;
            case YELLOW -> this.yellow;
            default -> null;
        };
    }

    /**
     * Retrieves the deck at the specified index if it is pickable.
     * @param index the index of the deck to retrieve
     * @return the deck at the specified index
     * @throws IllegalStateException if the deck at the specified index is not pickable
     */
    public Deck getDeck(int index, PlayerData player) throws IllegalStateException, NullPointerException {
        if (player == null) {
            throw new NullPointerException("Player is null");
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
     */
    public Component getTile(int ID) {
        return tiles[ID];
    }

    /**
     * Do the transition to the next state
     */
    public void stateTransition() {
        // TODO: implementare transizione di stato
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
}
