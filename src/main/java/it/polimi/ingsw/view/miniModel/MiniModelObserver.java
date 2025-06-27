package it.polimi.ingsw.view.miniModel;

/**
 * Observer interface for the MiniModel.
 * Implement this interface to receive notifications when the MiniModel changes.
 */
public interface MiniModelObserver {
    /**
     * Called when the MiniModel notifies its observers of a change.
     */
    void react();
}
