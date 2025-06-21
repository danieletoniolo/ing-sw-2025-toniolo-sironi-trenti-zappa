package it.polimi.ingsw.view.miniModel.deck;

import it.polimi.ingsw.view.gui.controllers.deck.DeckController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class DeckView implements MiniModelObservable {
    private final Stack<CardView> deck;
    private boolean covered;
    private boolean onlyLast;
    private final List<MiniModelObserver> observers;

    public DeckView() {
        this.deck = new Stack<>();
        this.observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        synchronized (observers) {
            for (MiniModelObserver observer : observers) {
                observer.react();
            }
        }
    }

    public Node getNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cards/deck.fxml"));
            Node root = loader.load();

            DeckController controller = loader.getController();
            controller.setModel(this);

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;

        for (CardView card : deck) {
            card.setCovered(covered);
        }
        notifyObservers();
    }

    public boolean isOnlyLast() {
        return onlyLast;
    }

    public void setOnlyLast(boolean onlyLast) {
        this.onlyLast = onlyLast;

        if (onlyLast) {
            deck.peek().setCovered(false);
        }
        notifyObservers();
    }

    public void addCard(CardView card) {
        deck.push(card);
        notifyObservers();
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
        notifyObservers();
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
