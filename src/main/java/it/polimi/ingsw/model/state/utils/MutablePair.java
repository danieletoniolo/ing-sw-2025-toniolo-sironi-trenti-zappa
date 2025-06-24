package it.polimi.ingsw.model.state.utils;

import org.javatuples.Pair;

/**
 * This class represents a mutable pair of objects.
 * @param <T> the type of the first object
 * @param <R> the type of the second object
 * @author Daniele Toniolo
 */
public class MutablePair<T, R> {

    /**
     * The first object of the pair.
     */
    private T first;

    /**
     * The second object of the pair.
     */
    private R second;

    /**
     * A lock object to synchronize access to the pair.
     */
    private final Object lock = new Object();

    /**
     * Constructs a new MutablePair with the given first and second objects.
     * @param first the first object
     * @param second the second object
     */
    public MutablePair(T first, R second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Constructs a new MutablePair from an existing Pair.
     * @param pair the Pair to copy
     */
    public MutablePair(Pair<T, R> pair) {
        this.first = pair.getValue0();
        this.second = pair.getValue1();
    }

    /**
     * Gets the first object of the pair.
     * @return the first object
     */
    public T getFirst() {
        synchronized (lock) {
            return first;
        }
    }

    /**
     * Sets the first object of the pair.
     * @param first the new first object
     */
    public void setFirst(T first) {
        synchronized (lock) {
            this.first = first;
        }
    }

    /**
     * Gets the second object of the pair.
     * @return the second object
     */
    public R getSecond() {
        synchronized (lock) {
            return second;
        }
    }

    /**
     * Sets the second object of the pair.
     * @param second the new second object
     */
    public void setSecond(R second) {
        synchronized (lock) {
            this.second = second;
        }
    }
}
