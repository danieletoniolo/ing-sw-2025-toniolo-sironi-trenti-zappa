package view.tui.card;

import Model.Cards.Card;
import Model.Cards.CardsManager;
import Model.Game.Board.Deck;
import Model.Game.Board.Level;
import org.javatuples.Pair;

import java.util.ArrayList;

public class CardMainView {
    public static void main(String[] args) {
        try{
            int COLS = 7;
            CardView cardView = new CardView();

            ArrayList<Card> toVisualize = new ArrayList<>();
            for(int i = 0; i < 40; i++) {
                toVisualize.add(CardsManager.getCard(i));
            }

            drawToVisualize(COLS, toVisualize, cardView);

            System.out.println();
            System.out.println();
            System.out.println();

            Deck[] decks = CardsManager.createDecks(Level.SECOND);
            drawDecks(decks, cardView);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void drawToVisualize(int COLS, ArrayList<Card> toVisualize, CardView cv) {
        int i;
        for (i = 0; i < toVisualize.size() / COLS; i++) {
            ArrayList<Pair<Card, Boolean>> cards = new ArrayList<>();
            for (Card c : toVisualize.subList(i * COLS, i * COLS + COLS)) {
                cards.add(new Pair<>(c, true));
            }
            cv.drawCardsOnOneLine(cards);
        }
        ArrayList<Pair<Card, Boolean>> cards = new ArrayList<>();
        for (Card c : toVisualize.subList(i * COLS, toVisualize.size())) {
            cards.add(new Pair<>(c, true));
        }
        cv.drawCardsOnOneLine(cards);
    }

    private static void drawDecks(Deck[] toVisualize, CardView cv) {
        for (int i = 0; i < toVisualize.length; i++) {
            ArrayList<Pair<Card, Boolean>> deck = new ArrayList<>();
            for (Card c : toVisualize[i].getCards()) {
                deck.add(new Pair<>(c, true));
            }
            int j = i + 1;
            System.out.println("Deck " + j + " ( pickable: " + toVisualize[i].isPickable() + " ) " + ":");
            cv.drawCardsOnOneLine(deck);
            System.out.println();
        }
    }
}
