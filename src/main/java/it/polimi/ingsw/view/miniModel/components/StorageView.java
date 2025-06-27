package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.CabinController;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.gui.controllers.components.StorageController;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.javatuples.Pair;

import java.io.IOException;

/**
 * Represents a storage component in the mini model view.
 * Stores an array of goods, and tracks whether the storage is dangerous and its capacity.
 * Provides color codes for TUI representation.
 */
public class StorageView extends ComponentView {

    /** Array of goods currently stored. */
    private GoodView[] goods;

    /** Indicates if the storage is dangerous. */
    private final boolean dangerous;

    /** Maximum capacity of the storage. */
    private final int capacity;

    /** ANSI escape code for red color (used for dangerous storage). */
    private static final String red = "\033[31m";

    /** ANSI escape code for light blue color (used for non-dangerous storage). */
    private static final String lightBlue = "\033[94m";

    /** ANSI escape code to reset color. */
    private static final String reset = "\033[0m";
    private Pair<Node, ComponentController> storagePair;

    /**
     * Constructs a StorageView with the specified parameters.
     *
     * @param ID         the unique identifier of the storage
     * @param connectors the connectors of the storage
     * @param clockWise  the clockwise orientation
     * @param dangerous  whether the storage is dangerous
     * @param capacity   the maximum number of goods the storage can hold
     */
    public StorageView(int ID, int[] connectors, int clockWise, boolean dangerous, int capacity) {
        super(ID, connectors, clockWise);
        this.dangerous = dangerous;
        this.goods = new GoodView[capacity];
        this.capacity = capacity;
    }

    /**
     * Set the good at a specific index
     * @param good the good to set
     */
    public void addGood(GoodView good) {
        for (int i = 0; i < goods.length; i++) {
            if (goods[i] == null) {
                goods[i] = good;
                break;
            }
        }
    }

    /**
     * Removes the specified good from the storage.
     * If the good is found, it is set to null and the method exits.
     *
     * @param good the good to remove
     */
    public void removeGood(GoodView good) {
        for (int i = 0; i < goods.length; i++) {
            if (goods[i] != null && goods[i].equals(good)) {
                goods[i] = null;
                break;
            }
        }
    }

    /**
     * Removes and returns the first non-null good found in the storage.
     *
     * @return the removed good, or null if storage is empty
     */
    public GoodView removeOneGood() {
        int i;
        for (i = 0; i < goods.length; i++) {
            if (goods[i] != null) {
                break;
            }
        }

        GoodView good = goods[i];
        goods[i] = null;
        return good;
    }

    /**
     * Replaces the current goods array with a new one.
     *
     * @param newGoods the new array of goods
     */
    public void changeGoods(GoodView[] newGoods) {
        this.goods = newGoods;
    }

    /**
     * Returns the array of goods currently stored.
     *
     * @return the array of goods
     */
    public GoodView[] getGoods() {
        return goods;
    }

    /**
     * Indicates whether the storage is dangerous.
     *
     * @return true if dangerous, false otherwise
     */
    public boolean isDangerous() {
        return dangerous;
    }

    /**
     * Returns the maximum capacity of the storage.
     *
     * @return the storage capacity
     */
    public int getCapacity() {
        return capacity;
    }

    @Override
    public Pair<Node, ComponentController> getNode() {
        try {
            if (storagePair != null) return storagePair;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/storage.fxml"));
            Parent root = loader.load();

            StorageController controller = loader.getController();
            controller.setModel(this);

            storagePair = new Pair<>(root, controller);
            return storagePair;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // TUI methods
    /**
     * Draws a specific line of the storage for the TUI.
     * If the storage is covered, delegates to the superclass.
     * Otherwise, draws the line with appropriate color and goods representation.
     *
     * @param line the line number to draw (0, 1, or 2)
     * @return the string representation of the line for the TUI
     */
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + (isDangerous() ? drawGoods(red) : drawGoods(lightBlue)) + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    /**
     * Draws the goods in the storage with the specified color for the TUI.
     *
     * @param color the ANSI color code to use
     * @return the string representation of the goods for the TUI
     */
    private String drawGoods(String color) {
        return switch (capacity) {
            case 1 -> " " + color + "|" + reset + singleGood(goods[0]) + color + "|" + reset + " ";
            case 2 -> color + "|" + reset + singleGood(goods[0]) + color + "|" + reset + singleGood(goods[1]) + color + "|" + reset;
            case 3 -> color + "|" + reset + singleGood(goods[0]) + singleGood(goods[1]) + singleGood(goods[2]) + color + "|" + reset;
            default -> throw new IllegalStateException("Unexpected value: " + capacity);
        };
    }

    /**
     * Returns the TUI representation of a single good.
     *
     * @param good the good to represent
     * @return the string representation of the good, or a space if null
     */
    private String singleGood(GoodView good) {
        return good == null ? " " : good.drawTui();
    }

    /**
     * Returns the type of this component.
     *
     * @return the component type (STORAGE)
     */
    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.STORAGE;
    }

    /**
     * Creates and returns a deep copy of this StorageView.
     *
     * @return a clone of this StorageView
     */
    @Override
    public StorageView clone() {
        StorageView copy = new StorageView(this.getID(), this.getConnectors(), this.getClockWise(), this.dangerous, this.capacity);
        for (GoodView good : this.goods) {
            if (good != null) {
                copy.addGood(GoodView.fromValue(good.getValue()));
            }
        }
        copy.setCovered(this.isCovered());
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
