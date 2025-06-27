package it.polimi.ingsw.model.spaceship;

import it.polimi.ingsw.model.good.Good;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.good.GoodType;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Storage component that can hold goods with a specified capacity.
 * Can be configured as dangerous to allow storing red goods.
 * Goods are stored in a priority queue ordered by value (highest first).
 * @author Daniele Toniolo
 */
public class Storage extends Component{
    /** Flag indicating if this storage can hold dangerous (red) goods */
    private boolean dangerous;
    /** Maximum number of goods this storage can hold */
    @JsonProperty("goodsCapacity")
    private int goodsCapacity;
    /** Total value of all goods currently stored */
    private int goodsValue;
    /** Priority queue containing the goods, ordered by value (highest first) */
    private final Queue<Good> goods;

    /**
     * Comparator for sorting goods by value in descending order.
     * This comparator is used to maintain the priority queue of goods in the storage.
     * <p>
     * We use a static inner class to avoid serialization issues with the comparator.
     */
    private static class ValueDescendingComparator implements Comparator<Good>, Serializable {
        @Override
        public int compare(Good g1, Good g2) {
            return Integer.compare(g2.getValue(), g1.getValue());
        }
    }

    /**
     * Constructs a Storage component with specified parameters.
     * @param ID the unique identifier for this component
     * @param connectors the array of connector types for this component
     * @param dangerous true if this storage can hold dangerous (red) goods, false otherwise
     * @param goodsCapacity the maximum number of goods this storage can hold
     */
    public Storage(int ID, ConnectorType[] connectors, boolean dangerous, int goodsCapacity) {
        super(ID, connectors);
        this.dangerous = dangerous;
        this.goodsCapacity = goodsCapacity;
        this.goodsValue = 0;
        this.goods = new PriorityQueue<>(new ValueDescendingComparator());
    }

    /**
     * Default constructor for Storage component.
     * Creates a storage with default values and an empty priority queue for goods.
     */
    public Storage() {
        super();
        this.goodsValue = 0;
        this.goods = new PriorityQueue<>(new ValueDescendingComparator());
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
        if (goodsCapacity > 0) {
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

    /**
     * Gets the component type of this storage.
     * @return ComponentType.STORAGE indicating this is a storage component
     */
    @Override
    public ComponentType getComponentType() {
        return ComponentType.STORAGE;
    }
}
