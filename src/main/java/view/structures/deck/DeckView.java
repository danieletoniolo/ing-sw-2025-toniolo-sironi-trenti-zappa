package view.structures.deck;

import view.structures.cards.CardView;

import java.util.Stack;

public class DeckView {
    private Stack<CardView> deck;
    private boolean covered;

    public DeckView(Stack<CardView> deck, boolean covered) {
        this.deck = deck;
        this.covered = covered;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
        if (covered) {
            deck.peek().setCovered(true);
        }
        else {
            for (CardView card : deck) {
                card.setCovered(false);
            }
        }
    }

    public Stack<CardView> getDeck() {
        return deck;
    }

    public void setDeck(Stack<CardView> deck) {
        this.deck = deck;
    }

    public void drawDeckGui(){
        //TODO: Implement the GUI drawing logic
    }

    public static int getRowsToDraw() {
        return CardView.getRowsToDraw();
    }

    public String drawLineTui(int line){
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < deck.size(); i++) {
            if (isCovered()) {
                if (i == deck.size() - 1) {
                    str.append(deck.get(i).drawLineTui(line));
                } else {
                    str.append(deck.get(i).drawLineTui(line).charAt(0));
                }
            } else {
                str.append(deck.get(i).drawLineTui(line));
            }
        }

        return str.toString();
    }
}
