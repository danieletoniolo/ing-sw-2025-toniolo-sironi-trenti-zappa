package Model.Game.Board;

import Model.Player.PlayerColor;
import Model.Player.PlayerData;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;


public class Board {
    private static final int numberOfDecks = 4;
    private static final int cardsPerDeck = 3;
//    private static final int numberOfFirstLevelCardsPerDeck = 1;
//    private static final int numberOfSecondLevelCardsPerDeck = 2;

    private final Level level;

    private Deck[] decks;
    private final Stack<Card> shuffledDeck;
    private int flightDays;

    private final PlayerData blue;
    private final PlayerData red;
    private final PlayerData green;
    private final PlayerData yellow;

    Board(Level level, PlayerData blue, PlayerData red, PlayerData green, PlayerData yellow) {
        this.level = level;
        if (!level.equals(Level.LEARNING)) {
            for (int i = 0; i < numberOfDecks; i++)
                this.decks = new Deck[cardsPerDeck];
        }
        this.shuffledDeck = new Stack<>();
        this.blue = blue;
        this.red = red;
        this.green = green;
        this.yellow = yellow;
    }

    // DA METTERE NEL UML -> Metodi privati in UML?
    private Card extractRandomElementFromList(ArrayList<Card> cards) {
        Random random = new Random();
        int index = random.nextInt(cards.size());
        return cards.remove(index);
    }

    public Level getBoardLevel() {
        return this.level;
    }

    public int getFlightDays() {
        return this.flightDays;
    }

    public PlayerData getPlayer(PlayerColor playerColor) {
        return switch (playerColor) {
            case BLUE -> this.blue;
            case RED -> this.red;
            case GREEN -> this.green;
            case YELLOW -> this.yellow;
            default -> null;
        };
    }

    public Deck getDeck(int index) throws IllegalStateException {
        if (!this.decks[index].isPickable())
            throw new IllegalStateException("Deck is not pickable");
        return this.decks[index];
    }

    public void createDecks(ArrayList<Card> cardsFirstLevel, ArrayList<Card> cardsSecondLevel) throws IllegalStateException {
        if (this.level.equals(Level.LEARNING))
            throw new IllegalStateException("Cannot create decks at learning level");

        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfDecks; i++) {
            cards.add(extractRandomElementFromList(cardsFirstLevel));
            cards.add(extractRandomElementFromList(cardsSecondLevel));
            cards.add(extractRandomElementFromList(cardsSecondLevel));

            this.decks[i] = new Deck(cards);
            cards.clear();
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.shuffledDeck);
    }

    public ArrayList<Card> shuffleDeck(ArrayList<Card> cards) {
        Collections.shuffle(cards);
        return cards;
    }

    public void mergeDecks() {
        for (Deck deck : this.decks) {
            for (Card card : deck.getCards()) {
                this.shuffledDeck.push(card);
            }
        }
    }

    public Card drawCard() throws IllegalStateException {
        if (this.shuffledDeck.isEmpty())
            throw new IllegalStateException("No more cards in the deck");
        return this.shuffledDeck.pop();
    }

    public void incrementFlightDays() {
        this.flightDays++;
    }
}
