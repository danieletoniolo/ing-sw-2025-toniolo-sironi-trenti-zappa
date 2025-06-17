package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.StorageController;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageView extends ComponentView {
    private final List<MiniModelObserver> listeners = new ArrayList<>();

    private GoodView[] goods;
    private final boolean dangerous;
    private final int capacity;
    private final String red = "\033[31m";
    private final String lightBlue = "\033[94m";
    private final String reset = "\033[0m";

    public StorageView(int ID, int[] connectors, int clockWise, boolean dangerous, int capacity) {
        super(ID, connectors, clockWise);
        this.dangerous = dangerous;
        this.goods = new GoodView[capacity];
        this.capacity = capacity;
    }

    public void addListener(MiniModelObserver listener) {
        listeners.add(listener);
    }

    public void removeListener(MiniModelObserver listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (MiniModelObserver listener : listeners) {
            listener.onModelChanged();
        }
    }

    public Node createGuiNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/storage.fxml"));
            Node root = loader.load();

            StorageController controller = loader.getController();
            controller.setStorageModel(this);

            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
        notifyListeners();
    }

    public void removeGood(GoodView good) {
        for (int i = 0; i < goods.length; i++) {
            if (goods[i] != null && goods[i].equals(good)) {
                goods[i] = null;
                break;
            }
        }
        notifyListeners();
    }

    public GoodView removeOneGood() {
        int i;
        for (i = 0; i < goods.length; i++) {
            if (goods[i] != null) {
                break;
            }
        }

        GoodView good = goods[i];
        goods[i] = null;
        notifyListeners();
        return good;
    }

    public void changeGoods(GoodView[] newGoods) {
        notifyListeners();
        this.goods = newGoods;
    }

    public GoodView[] getGoods() {
        return goods;
    }

    public boolean isDangerous() {
        return dangerous;
    }

    public int getCapacity() {
        return capacity;
    }

    // GUI methods
    /**
     * Draws the component GUI.
     * This method is called to draw the component GUI.
     *
     * @return an Image representing the image of the component
     */
    @Override
    public Image drawGui() {
        String path = "/image/tiles/" + this.getID() + ".jpg";
        Image img = new Image(getClass().getResource(path).toExternalForm());
        return img;
    }


    // TUI methods
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + (isDangerous() ? drawGoods(red) : drawGoods(lightBlue)) + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    private String drawGoods(String color) {
        return switch (capacity) {
            case 1 -> " " + color + "|" + reset + singleGood(goods[0]) + color + "|" + reset + " ";
            case 2 -> color + "|" + reset + singleGood(goods[0]) + color + "|" + reset + singleGood(goods[1]) + color + "|" + reset;
            case 3 -> color + "|" + reset + singleGood(goods[0]) + singleGood(goods[1]) + singleGood(goods[2]) + color + "|" + reset;
            default -> throw new IllegalStateException("Unexpected value: " + capacity);
        };
    }

    private String singleGood(GoodView good) {
        return good == null ? " " : good.drawTui();
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.STORAGE;
    }

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
