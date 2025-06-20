package it.polimi.ingsw.view.miniModel;

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
