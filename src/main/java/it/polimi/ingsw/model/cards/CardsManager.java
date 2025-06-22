package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.board.Deck;
import it.polimi.ingsw.model.game.board.Level;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.utils.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CardsManager {
    private static final InputStream inputStream = CardsManager.class.getResourceAsStream("/json/Cards.json");
    private static final String json;
    static {
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found!");
        }
        json = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
    }
    static ObjectMapper objectMapper = new ObjectMapper();

    private static final Card[] cards;

    static {
        try {
            cards = objectMapper.readValue(json, Card[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * CardsManager constructor
     */
    public CardsManager(){}

    /**
     * Create decks of cards for the board
     * @return an array of decks
     */
    public static Deck[] createDecks(Level gameLevel)  throws IllegalStateException {
        if (gameLevel == Level.LEARNING) {
            throw new IllegalStateException("Decks are not available in LEARNING mode");
        }

        ArrayList<Card> deck1 = new ArrayList<>();
        ArrayList<Card> deck2 = new ArrayList<>();
        ArrayList<Card> deck3 = new ArrayList<>();
        ArrayList<Card> deck4 = new ArrayList<>();
        ArrayList<Card> selectedCards = new ArrayList<>();

        // TODO: Uncomment to shuffle the cards
        /*
        Random random = new Random();
        int i = 0;
        while (i < 4) {
            int radomIndex = random.nextInt(cards.length/2);
            if (!selectedCards.contains(cards[radomIndex])) {
                selectedCards.add(cards[radomIndex]);
                i++;
            }
        }
        i = 0;
        while (i < 8) {
            int randomIndex = random.nextInt(cards.length/2, cards.length);
            if (!selectedCards.contains(cards[randomIndex])) {
                selectedCards.add(cards[randomIndex]);
                i++;
            }
        }
        */

        /*
            Cards already tested:
                - Planets
                - Epidemic
                - Abandoned Station
                - Stardust
                - OpenSpace
                - Slavers
                - Smugglers
                -
         */
        //                12  9  6   3  11   8   5  2  10  7  4  1
        int[] cardsIDs = {21, 3, 2, 22, 23, 16, 16, 22, 4, 10, 8, 2};

        for (int cardsID : cardsIDs) {
            selectedCards.add(cards[cardsID]);
        }

        // Distribute the selected cards into 4 decks
        Deck[] decks = new Deck[4];
        for (int i = 0; i < selectedCards.size(); i++) {
            switch (i % 4) {
                case 0 -> deck1.add(selectedCards.get(i));
                case 1 -> deck2.add(selectedCards.get(i));
                case 2 -> deck3.add(selectedCards.get(i));
                case 3 -> deck4.add(selectedCards.get(i));
            }
        }

        decks[0] = new Deck(deck1);
        decks[0].setPickable(true);
        decks[1] = new Deck(deck2);
        decks[1].setPickable(true);
        decks[2] = new Deck(deck3);
        decks[2].setPickable(true);
        decks[3] = new Deck(deck4);
        decks[3].setPickable(false);

        return decks;
    }

    /**
     * Create the learning deck: cards of level 1 with the 'L'
     * @return a stack of cards for the learning deck
     */
    public static Stack<Card> createLearningDeck() {
        Stack<Card> learningDeck = new Stack<>();

        learningDeck.push(cards[1]);
        learningDeck.push(cards[3]);
        learningDeck.push(cards[4]);
        learningDeck.push(cards[8]);
        learningDeck.push(cards[12]);
        learningDeck.push(cards[15]);
        learningDeck.push(cards[17]);
        learningDeck.push(cards[18]);

        // TODO: Uncomment to shuffle the learning deck
        // Collections.shuffle(learningDeck);

        return learningDeck;
    }

    /**
     * Create a shuffled deck of cards: the first card is a level 2 card
     * @param decks the decks to shuffle
     * @return a shuffled stack of cards
     * @throws IllegalArgumentException if decks is null or empty
     */
    public static Stack<Card> createShuffledDeck(Deck[] decks) throws IllegalArgumentException {
        if (decks == null || decks.length == 0) {
            throw new IllegalArgumentException("Decks cannot be null or empty");
        }

        Stack<Card> shuffledDeck = new Stack<>();
        for (Deck deck : decks) {
            ArrayList<Card> cards = deck.getCards();
            shuffledDeck.addAll(cards);
        }

        // TODO: Uncomment to shuffle the decks
        /*
        do {
            Collections.shuffle(shuffledDeck);
        } while (shuffledDeck.peek().getCardLevel() != 2);
         */
        // Ensure the first card is a level 2 card

        return shuffledDeck;
    }

    /// TODO: METODO DA ELIMINARE
    /**
     * Get a card by its ID
     * @param ID the ID of the card
     * @return the card with the specified ID
     */
    public static Card getCard(int ID) throws IndexOutOfBoundsException {
        if (ID < 0 || ID >= cards.length) {
            throw new IndexOutOfBoundsException("ID is out of bounds");
        }
        return cards[ID];
    }
}
