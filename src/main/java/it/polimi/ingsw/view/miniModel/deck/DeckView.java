package it.polimi.ingsw.view.miniModel.deck;

import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.view.miniModel.cards.CardView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class DeckView {
    private final Stack<CardView> deck;
    private boolean covered;
    private boolean onlyLast;

    public DeckView() {
        deck = new Stack<>();
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

    public boolean isOnlyLast() {
        return onlyLast;
    }

    public void setOnlyLast(boolean onlyLast) {
        this.onlyLast = onlyLast;

        if (onlyLast) {
            deck.peek().setCovered(false);
        }
    }

    public void addCard(CardView card) {
        deck.push(card);
    }

    public void popCard() {
        deck.pop();
    }

    public Stack<CardView> getDeck() {
        return deck;
    }

    public void order(List<Integer> ids) {
        for (int i = 0; i < ids.size(); i++) {
            int j;
            for (j = 0; j < deck.size(); j++) {
                if (deck.get(j).getID() == ids.get(i)) {
                    break;
                }
            }
            Collections.swap(deck, j, i);
        }
    }

    public void drawDeckGui(){
        //TODO: Implement the GUI drawing logic
    }

    public static int getRowsToDraw() {
        return CardView.getRowsToDraw();
    }

    public int getColsToDraw() {
        return CardView.getColsToDraw() + deck.size() - 1;
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
