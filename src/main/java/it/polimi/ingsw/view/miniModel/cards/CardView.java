package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.gui.controllers.cards.CardController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class CardView implements Structure, MiniModelObservable {
    /**
     * This string is used to draw the top of the card in the TUI.
     */
    protected static String Up =       "╭───────────────────╮";

    /**
     * This string is used to draw an empty line of the card in the TUI.
     */
    protected static String Clear =    "│                   │";

    /**
     * These strings are used to draw a covered card in the TUI for level 1.
     */
    private static final String Covered1 = "│      ───┬───      │";
    private static final String Covered2 = "│         │         │";
    private static final String Covered3 = "│         │         │";
    private static final String Covered4 = "│      ───┴───      │";

    /**
     * These strings are used to draw a covered card in the TUI for level 2.
     */
    private static final String Covered5 = "│      ─┬───┬─      │";
    private static final String Covered6 = "│       │   │       │";
    private static final String Covered7 = "│       │   │       │";
    private static final String Covered8 = "│      ─┴───┴─      │";

    /**
     * This string is used to draw the bottom of the card in the TUI.
     */
    public static String Down =     "╰───────────────────╯";

    private final int ID;
    private boolean covered;
    private final int level;

    // TODO: We may need just one observer for a card.
    /**
     * This list contains the observers that are registered to this CardView.
     * It is used to notify the observers when the model changes.
     */
    private final List<MiniModelObserver> listeners;


    public CardView(int ID, boolean covered, int level) {
        this.ID = ID;
        this.covered = covered;
        this.level = level;
        this.listeners = new ArrayList<>();
    }

    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (listeners) {
            listeners.add(observer);
            listeners.notifyAll();
        }
    }

    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (listeners) {
            listeners.remove(observer);
            listeners.notifyAll();
        }
    }

    @Override
    public void notifyObservers() {
        synchronized (listeners) {
            for (MiniModelObserver observer : listeners) {
                observer.onModelChanged();
            }
        }
    }

    public final int getLevel() {
        return level;
    }

    public Node createGuiNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cards/card.fxml"));
            Parent root = loader.load();

            CardController controller = loader.getController();
            controller.setModel(this);

            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Image drawGui() {
        String path;
        if(level == 1){
            path = "/image/card/covered_1.jpg";
        } else {
            path = "/image/card/covered_2.jpg";
        }
        Image img = new Image(getClass().getResource(path).toExternalForm());
        return img;
    }

    public static int getRowsToDraw() {
        return 10;
    }

    public static int getColsToDraw() {
        return 21;
    }

    @Override
    public String drawLineTui(int line){
        return switch(line) {
            case 0 -> Up;
            case 3 -> (level == 1) ? Covered1 : Covered5;
            case 4 -> (level == 1) ? Covered2 : Covered6;
            case 5 -> (level == 1) ? Covered3 : Covered7;
            case 6 -> (level == 1) ? Covered4 : Covered8;
            case 1, 2, 7, 8 -> Clear;
            case 9 -> Down;
            default -> null;
        };
    }

    public int getID() {
        return ID;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
        notifyObservers();
    }

    public abstract CardViewType getCardViewType();
}
