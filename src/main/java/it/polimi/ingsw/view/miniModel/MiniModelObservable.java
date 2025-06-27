package it.polimi.ingsw.view.miniModel;

/**
 * Interface for objects that can be observed by MiniModelObserver instances.
 * Provides methods to register, unregister, and notify observers.
 */
public interface MiniModelObservable {
    /**
     * Adds an observer to the list of observers.
     *
     * @param observer The observer to be added.
     */
    void registerObserver(MiniModelObserver observer);

    /**
     * Remove an observer from the list of observers.
     *
     * @param observer The observer to be removed.
     */
    void unregisterObserver(MiniModelObserver observer);

    /**
     * Notify all observers that the model has changed.
     */
    void notifyObservers();
}
