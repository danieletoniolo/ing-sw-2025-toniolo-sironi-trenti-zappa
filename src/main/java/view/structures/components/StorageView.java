package view.structures.components;

import view.structures.good.GoodView;

public class StorageView extends ComponentView {
    private GoodView[] goods;
    private boolean dangerous;
    private final int capacity;

    public StorageView(int ID, int[] connectors, boolean dangerous, int capacity) {
        super(ID, connectors);
        this.dangerous = dangerous;
        this.goods = new GoodView[capacity];
        this.capacity = capacity;
    }

    /**
     * Set the good at a specific index
     * @param good the good to set
     * @param index the index to set the good at
     */
    public void setGood(GoodView good, int index) {
        if (index < 0 || index >= goods.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        this.goods[index] = good;
    }

    public GoodView[] getGoods() {
        return goods;
    }

    public void setDangerous(boolean dangerous) {
        this.dangerous = dangerous;
    }

    public boolean isDangerous() {
        return dangerous;
    }

    @Override
    public void drawComponentGui() {
        //TODO: Implement the GUI drawing logic for the Storage component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 4 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "  Storage  " + super.drawRight(line);
            case 2 -> super.drawLeft(line) + (" Toxic:" + (isDangerous() ? "Yes " : " No ")) + super.drawRight(line);
            case 3 -> super.drawLeft(line) + drawGoods() + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    private String drawGoods() {
        return switch (capacity) {
            case 1 -> "    |" + (goods[0] == null ? " " : goods[0].drawTui()) + "|    ";
            case 2 -> "   |" + (goods[0] == null ? " " : goods[0].drawTui()) + "|" + (goods[1] == null ? " " : goods[1].drawTui()) +  "|   ";
            case 3 -> "  |" + (goods[0] == null ? " " : goods[0].drawTui()) + "|" + (goods[1] == null ? " " : goods[1].drawTui()) + "|" + (goods[2] == null ? " " : goods[2].drawTui()) + "|  ";
            default -> throw new IllegalStateException("Unexpected value: " + capacity);
        };
    }
}
