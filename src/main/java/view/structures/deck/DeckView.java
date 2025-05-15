package view.structures.deck;

import view.structures.cards.CardView;

import java.util.Stack;

public class DeckView {
    private Stack<CardView> deck;
    private boolean covered;
    private boolean onlyLast;

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

    public boolean isOnlyLast() {
        return onlyLast;
    }

    public void setOnlyLast(boolean onlyLast) {
        this.onlyLast = onlyLast;

        if (onlyLast) {
            deck.peek().setCovered(false);
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
            } else if (onlyLast) {
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
