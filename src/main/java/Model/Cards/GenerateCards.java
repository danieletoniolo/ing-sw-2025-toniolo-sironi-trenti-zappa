package Model.Cards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.Main;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GenerateCards {
    private final Card[] cards;

    /**
     * Constructor to initialize the cards from a JSON file
     * @throws JsonProcessingException if there is an error processing the JSON
     */
    public GenerateCards() throws JsonProcessingException {
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("Json/Cards.json");

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found!");
        }

        String json = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();

        ObjectMapper objectMapper = new ObjectMapper();
        cards = objectMapper.readValue(json, Card[].class);
    }

    /**
     * Get a list of cards based on the game level
     * @param gameLevel 0 for learning, 2 for second level
     * @return a list of cards: 8 cards for learning, 12 cards for second level (4 of level 1 + 8 of level 2)
     * @throws IllegalStateException if the game level is not 0 or 2
     */
    public ArrayList<Card> getRandomizedCards(int gameLevel) throws IllegalStateException {
        ArrayList<Card> selectedCards = new ArrayList<>();
        Random random = new Random();

        if (gameLevel != 0 && gameLevel != 2) {
            throw new IllegalArgumentException("Invalid game level: " + gameLevel);
        }
        if (gameLevel == 2) {
            int i = 0;
            while (i < 4) {
                int randomIndex = random.nextInt(cards.length/2);
                if (!selectedCards.contains(cards[randomIndex])) {
                    selectedCards.add(cards[randomIndex]);
                    i++;
                }
            }
            i = 0;
            while (i < 8) {
                int randomIndex = random.nextInt(cards.length / 2, cards.length);
                if (!selectedCards.contains(cards[randomIndex])) {
                    selectedCards.add(cards[randomIndex]);
                    i++;
                }
            }

            return selectedCards;
        }

        selectedCards.add(cards[1]);
        selectedCards.add(cards[3]);
        selectedCards.add(cards[4]);
        selectedCards.add(cards[8]);
        selectedCards.add(cards[12]);
        selectedCards.add(cards[15]);
        selectedCards.add(cards[17]);
        selectedCards.add(cards[18]);

        return selectedCards;
    }

    /// METODO DA ELIMINARE
    /**
     * Get a card by its ID
     * @param ID the ID of the card
     * @return the card with the specified ID
     */
    public Card getCard(int ID) {
        return cards[ID];
    }
}
