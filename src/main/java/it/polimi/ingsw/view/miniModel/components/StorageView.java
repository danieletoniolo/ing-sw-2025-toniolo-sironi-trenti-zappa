package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.miniModel.good.GoodView;

public class StorageView extends ComponentView {

    private GoodView[] goods;
    private final boolean dangerous;
    private final int capacity;
    private static final String red = "\033[31m";
    private static final String lightBlue = "\033[94m";
    private static final String reset = "\033[0m";

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

    public void removeGood(GoodView good) {
        for (int i = 0; i < goods.length; i++) {
            if (goods[i] != null && goods[i].equals(good)) {
                goods[i] = null;
                break;
            }
        }
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
        return good;
    }

    public void changeGoods(GoodView[] newGoods) {
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
