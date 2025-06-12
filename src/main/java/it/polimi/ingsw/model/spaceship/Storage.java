package it.polimi.ingsw.model.spaceship;

import it.polimi.ingsw.model.good.Good;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.good.GoodType;

import java.util.*;

public class Storage extends Component{
    private boolean dangerous;
    @JsonProperty("goodsCapacity")
    private int goodsCapacity;
    private int goodsValue;
    private Queue<Good> goods;

    public Storage(int ID, ConnectorType[] connectors, boolean dangerous, int goodsCapacity) {
        super(ID, connectors);
        this.dangerous = dangerous;
        this.goodsCapacity = goodsCapacity;
        this.goodsValue = 0;
        this.goods = new PriorityQueue<>(Comparator.comparingInt(Good::getValue).reversed());
    }

    public Storage() {
        super();
        this.goodsValue = 0;
        // this.goods = new PriorityQueue<>(Comparator.comparingInt(Good::getValue).reversed());
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
     * Get the list of goods in the storage
     * @return A list of goods in the storage
     */
    public List<Good> getGoods() {
        return goods.stream().toList();
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
            goodsCapacity -= 1;
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
            goodsCapacity += 1;
        } else {
            throw new IllegalStateException("Good not found in storage");
        }
        goods.remove();
    }

    /**
     * Peek at the most valuable good in the storage at the given depth.
     * @return A copy of the most valuable good in the storage.
     */
    public Good peekGood(int depth) {
        if (goods.isEmpty()) {
            return null;
        }
        if (depth < 0 || depth > goods.size()) {
            throw new IllegalArgumentException("Depth is out of bounds");
        }
        return (Good) goods.toArray()[depth];
    }

    /**
     * Peek at the most valuable good in the storage.
     * @return A copy of the most valuable good in the storage.
     */
    public Good peekGood() {
        return goods.peek();
    }

    /**
     * Check if the storage has a good
     * @param good the Good to check
     * @return true if the storage has the good, false otherwise
     */
    public boolean hasGood(Good good) {
        return goods.contains(good);
    }


    /**
     * Poll the most valuable good in the storage.
     * @return The most valuable good in the storage.
     */
    public Good pollGood() {
        if (goods.isEmpty()) {
            return null;
        }
        goodsValue -= goods.peek().getValue();
        goodsCapacity += 1;
        return goods.poll();
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
