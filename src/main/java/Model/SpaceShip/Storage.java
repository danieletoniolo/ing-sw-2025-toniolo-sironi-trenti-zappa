package Model.SpaceShip;

import Model.Good.Good;
import Model.Good.GoodType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Storage extends Component{
    private boolean dangerous;
    @JsonProperty("goodsCapacity")
    private int goodsCapacity;
    private int goodsValue;
    private ArrayList<Good> goods;

    public Storage(int ID, ConnectorType[] connectors, boolean dangerous, int goodsCapacity) {
        super(ID, connectors);
        this.dangerous = dangerous;
        this.goodsCapacity = goodsCapacity;
        this.goodsValue = 0;
        this.goods = new ArrayList<>();
    }

    public Storage(){
        super();
    }
    /**
     * Check if the storage is dangerous
     * @return true if the storage is dangerous, false otherwise
     */
    public boolean isDangerous() {
        return dangerous;
    }

    /**
     * Get the capacity of the storage
     * @return The capacity of the storage
     */
    public int getGoodsCapacity() {
        return goodsCapacity;
    }

    /**
     * Add a good to the storage if there is enough space
     * @param good the Good to add
     * @throws IllegalStateException if the storage is full or if the good is red and the storage is not dangerous
     */
    public void addGood(Good good) throws IllegalStateException {
        if (good.getColor() == GoodType.RED && !dangerous) {
            throw new IllegalStateException("Cannot add a red good to a non-dangerous storage");
        }
        if (goods.size() < goodsCapacity) {
            goods.add(good);
            goodsValue += good.getValue();
        } else {
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
            throw new IllegalStateException("Good not found in storage");
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
