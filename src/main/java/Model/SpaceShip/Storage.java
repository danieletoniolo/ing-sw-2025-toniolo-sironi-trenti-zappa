package Model.SpaceShip;

import Model.Good.Good;

import java.util.ArrayList;

public class Storage extends Component{
    private final boolean dangerous;
    private final int goodsCapacity;
    private int goodsValue;
    private ArrayList<Good> goods;

    public Storage(int row, int column, ConnectorType[] connectors, boolean dangerous, int goodsCapacity) {
        super(row, column, connectors);
        this.dangerous = dangerous;
        this.goodsCapacity = goodsCapacity;
        this.goodsValue = 0;
        this.goods = new ArrayList<>();
    }

    /*
     @brief Check if the storage is dangerous
     @return true if the storage is dangerous, false otherwise
     */
    public boolean isDangerous() {
        return dangerous;
    }

    /*
     @brief Add a good to the storage if there is enough space
     @param good must be a non-null Good
     @throws IllegalStateException if the storage is full
     */
    public void addGood(Good good) {
        if (goods.size() < goodsCapacity) {
            goods.add(good);
            goodsValue += good.getValue();
        } else {
            // TODO: understand what to do in this case
            throw new IllegalStateException("Storage is full");
        }
    }

    /*
     @brief Remove a good from the storage if it is present
     @param good must be a non-null Good
     @throws IllegalStateException if the good is not found in the storage
     */
    public void removeGood(Good good) {
        if (goods.contains(good)) {
            goods.remove(good);
            goodsValue -= good.getValue();
        } else {
            // TODO: understand what to do in this case
            throw new IllegalStateException("Good not found in storage");
        }
    }

    /*
     @brief Get the list of goods in the storage
     @return ArrayList of goods in the storage
     */
    public ArrayList<Good> getGoods() {
        return goods;
    }

    /*
     @brief Get the value of the goods in the storage
     @return goodsValue
     */
    public int getGoodsValue() {
        return goodsValue;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.STORAGE;
    }
}
