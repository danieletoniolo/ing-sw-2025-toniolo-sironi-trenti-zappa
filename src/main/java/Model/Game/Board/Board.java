package Model.Game.Board;

import Model.Cards.Card;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import Model.State.State;

import java.util.*;


public class Board {
    private static final int numberOfDecks = 4;
    private static final int cardsPerDeck = 3;

    private final State state;
    private final Level level;
    private final int numberOfCells;

    private Deck[] decks;
    private final Stack<Card> shuffledDeck;
    private final Map<Integer, Component> tiles;
    private int flightDays;

    private final PlayerData blue;
    private final PlayerData red;
    private final PlayerData green;
    private final PlayerData yellow;

    /**
     * Create a new board
     * @param level the level of the board
     * @param tiles the list of tiles on the board
     * @param blue the player data for the blue player
     * @param red the player data for the red player
     * @param green the player data for the green player
     * @param yellow the player data for the yellow player
     * @throws IllegalArgumentException if the level is set to an unexpected value
     */
    public Board(Level level, Map<Integer, Component> tiles, PlayerData blue, PlayerData red, PlayerData green, PlayerData yellow) throws IllegalArgumentException {
        // TODO: dichiarare state iniziale
        this.state = null;
        this.level = level;
        if (!level.equals(Level.LEARNING)) {
            for (int i = 0; i < numberOfDecks; i++)
                this.decks = new Deck[cardsPerDeck];
        }
        switch (level) {
            case LEARNING:
                this.numberOfCells = 18;
                break;
            case SECOND:
                this.numberOfCells = 24;
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + level);
        }
        this.tiles = tiles;
        this.shuffledDeck = new Stack<>();
        this.blue = blue;
        this.red = red;
        this.green = green;
        this.yellow = yellow;
    }

    /**
     * Extracts and removes a random card from the provided list.
     * @param cards the list of cards to select from
     * @return the randomly selected card that has been removed from the list
     */
    private Card extractRandomElementFromList(ArrayList<Card> cards) {
        Random random = new Random();
        int index = random.nextInt(cards.size());
        return cards.remove(index);
    }

    /**
     * Retrieves the level of the board.
     * @return the board level
     */
    public Level getBoardLevel() {
        return this.level;
    }

    /**
     * Retrieves the current number of flight days.
     * @return the number of flight days
     */
    public int getFlightDays() {
        return this.flightDays;
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
    public Deck getDeck(int index) throws IllegalStateException {
        if (!this.decks[index].isPickable())
            throw new IllegalStateException("Deck is not pickable");
        return this.decks[index];
    }

    /**
     * Retrieves the number of cells on the board.
     * @return the number of cells
     */
    public int getNumberOfCells() {
        return this.numberOfCells;
    }

    /**
     * Retrieves the tile with the specified ID.
     * @param ID the ID of the tile to retrieve
     * @return the tile with the specified ID, or null if no tile with that ID exists
     */
    public Component getTile(int ID) {
        return this.tiles.get(ID);
    }

    /**
     * Do the transition to the next state
     */
    public void stateTransition() {
        // TODO: implementare transizione di stato
    }

    /**
     * Creates the decks for the board using the provided cards.
     * @param cardsFirstLevel the list of cards for the first level
     * @param cardsSecondLevel the list of cards for the second level
     * @throws IllegalArgumentException if the board level is set to learning
     */
    public void createDecks(ArrayList<Card> cardsFirstLevel, ArrayList<Card> cardsSecondLevel) throws IllegalArgumentException {
        if (this.level.equals(Level.LEARNING))
            throw new IllegalArgumentException("Cannot create decks at learning level");

        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfDecks; i++) {
            cards.add(extractRandomElementFromList(cardsFirstLevel));
            cards.add(extractRandomElementFromList(cardsSecondLevel));
            cards.add(extractRandomElementFromList(cardsSecondLevel));

            this.decks[i] = new Deck(cards);
            cards.clear();
        }
    }

    /**
     * Shuffles the deck of cards.
     */
    public void shuffleDeck() {
        Collections.shuffle(this.shuffledDeck);
    }

    /**
     * Shuffles the provided list of cards.
     * @param cards the list of cards to shuffle
     * @return the shuffled list of cards
     */
    public ArrayList<Card> shuffleDeck(ArrayList<Card> cards) {
        Collections.shuffle(cards);
        return cards;
    }

    /**
     * Merges the decks into a single shuffled deck.
     */
    public void mergeDecks() {
        for (Deck deck : this.decks) {
            for (Card card : deck.getCards()) {
                this.shuffledDeck.push(card);
            }
        }
    }

    /**
     * Draws a card from the shuffled deck.
     * @return the card drawn from the deck
     * @throws IllegalStateException if there are no more cards in the deck
     */
    public Card drawCard() throws IllegalStateException {
        if (this.shuffledDeck.isEmpty())
            throw new IllegalStateException("No more cards in the deck");
        return this.shuffledDeck.pop();
    }

    /**
     * Increments the number of flight days.
     */
    public void incrementFlightDays() {
        this.flightDays++;
    }
}
