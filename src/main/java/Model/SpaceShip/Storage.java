package Model.SpaceShip;

import Model.Good.Good;

import java.util.ArrayList;

public class Storage extends Component{
    private final boolean dangerous;
    private final int goodsCapacity;
    private int goodsValue;
    private ArrayList<Good> goods;

    public Storage(int ID, int row, int column, ConnectorType[] connectors, boolean dangerous, int goodsCapacity) {
        super(ID, row, column, connectors);
        this.dangerous = dangerous;
        this.goodsCapacity = goodsCapacity;
        this.goodsValue = 0;
        this.goods = new ArrayList<>();
    }

    /**
     * Check if the storage is dangerous
     * @return true if the storage is dangerous, false otherwise
     */
    public boolean isDangerous() {
        return dangerous;
    }

    /**
     * Add a good to the storage if there is enough space
     * @param good the Good to add
     * @throws IllegalStateException if the storage is full
     */
    public void addGood(Good good) throws IllegalStateException {
        if (goods.size() < goodsCapacity) {
            goods.add(good);
            goodsValue += good.getValue();
        } else {
            // TODO: understand what to do in this case
            throw new IllegalStateException("Storage is full");
        }
    }

    /**
     * Remove a good from the storage if it is present
     * @param good the Good to remove
     * @throws IllegalStateException if the good is not found in the storage
     */
    public void removeGood(Good good) throws IllegalStateException {
        if (goods.contains(good)) {
            goods.remove(good);
            goodsValue -= good.getValue();
        } else {
            // TODO: understand what to do in this case
            throw new IllegalStateException("Good not found in storage");
        }
    }

    /**
     * Exchange goods in the storage with the given goods
     * @param goodsToAdd Goods to add in the storage
     * @param goodsToRemove Goods to remove from the storage
     * @throws IllegalStateException if the storage is full or the good to remove is not found
     */
    public void exchangeGood(ArrayList<Good> goodsToAdd, ArrayList<Good> goodsToRemove) throws IllegalStateException {
        for (Good good : goodsToAdd) {
            removeGood(good);
        }
        for (Good good : goodsToRemove) {
            addGood(good);
        }
    }

    /**
     * Get the list of goods in the storage
     * @return ArrayList of goods in the storage
     */
    public ArrayList<Good> getGoods() {
        return goods;
    }

    /**
     * Get the value of the goods in the storage
     * @return The value of the goods in the storage
     */
    public int getGoodsValue() {
        return goodsValue;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.STORAGE;
    }
}
