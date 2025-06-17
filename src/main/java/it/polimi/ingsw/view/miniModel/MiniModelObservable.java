package it.polimi.ingsw.view.miniModel;

public interface MiniModelObservable {
    /**
     * Adds an observer to the list of observers.
     *
     * @param observer The observer to be added.
     */
    public void registerObserver(MiniModelObserver observer);

    /**
     * Remove an observer from the list of observers.
     *
     * @param observer The observer to be removed.
     */
    public void unregisterObserver(MiniModelObserver observer);

    /**
     * Notify all observers that the model has changed.
     */
    public void notifyObservers();
}
