package it.polimi.ingsw.view.miniModel.timer;

import it.polimi.ingsw.view.gui.controllers.misc.TimerCountdownController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the timer view in the mini model.
 * Manages timer state, observers, and player actions related to the timer.
 */
public class TimerView implements Structure, MiniModelObservable {
    /**
     * Minutes remaining on the timer.
     */
    private int minutes;

    /**
     * Seconds remaining on the timer.
     */
    private int seconds;

    /**
     * The player who last flipped the timer.
     */
    private PlayerDataView playerWhoFlipped;

    /**
     * The total number of flips allowed.
     */
    private int totalFlips;

    /**
     * The current number of flips performed.
     */
    private int times;

    /**
     * List of observers registered to this timer view.
     */
    private final List<MiniModelObserver> observers;

    /**
     * Pair containing the timer node and its controller for the GUI.
     */
    private Pair<Node, TimerCountdownController> timerNode;

    /**
     * Flag indicating whether the timer is currently running.
     */
    private boolean isRunning = false;

    /**
     * Constructs a new TimerView and initializes the observer list.
     */
    public TimerView() {
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to this timer view.
     * @param observer the observer to register
     */
    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer from this timer view.
     * @param observer the observer to unregister
     */
    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Notifies all registered observers of a change in the timer view.
     */
    @Override
    public void notifyObservers() {
        synchronized (observers) {
            for (MiniModelObserver observer : observers) {
                observer.react();
            }
        }
    }

    /**
     * Checks if the timer is currently running.
     * @return true if the timer is running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Sets the running state of the timer.
     * @param running true if the timer should be running, false otherwise
     */
    public void setRunning(boolean running) {
        isRunning = running;
        notifyObservers();
    }

    /**
     * Sets the current number of flips performed.
     * @param numberOfFlips the number of flips
     */
    public void setNumberOfFlips(int numberOfFlips) {
        this.times = numberOfFlips;
    }

    /**
     * Sets the total number of flips allowed.
     * @param totalFlips the total number of flips
     */
    public void setTotalFlips(int totalFlips) {
        this.totalFlips = totalFlips;
    }

    /**
     * Gets the current number of flips performed.
     * @return the number of flips
     */
    public int getNumberOfFlips() {
        return times;
    }

    /**
     * Gets the total number of flips allowed.
     * @return the total number of flips
     */
    public int getTotalFlips() {
        return totalFlips;
    }

    /**
     * Sets the remaining time on the timer in seconds.
     * Updates both minutes and seconds fields accordingly.
     *
     * @param secondsRemaining the total seconds remaining to set
     */
    public void setSecondsRemaining(int secondsRemaining) {
        minutes = secondsRemaining / 60;
        seconds = secondsRemaining % 60;
    }

    /**
     * Gets the total remaining time on the timer in seconds.
     *
     * @return the total seconds remaining
     */
    public int getSecondsRemaining() {
        return minutes * 60 + seconds;
    }

    /**
     * Sets the player who flipped the timer and notifies all observers.
     *
     * @param playerWhoFlipped the player who flipped the timer
     */
    public void setFlippedTimer(PlayerDataView playerWhoFlipped) {
        this.playerWhoFlipped = playerWhoFlipped;
    }

    /**
     * Loads and returns the timer node and its controller for the GUI.
     * If already loaded, returns the cached pair.
     *
     * @return a Pair containing the Node and its TimerCountdownController, or null if loading fails
     */
    public Pair<Node, TimerCountdownController> getNode() {
        try {
            if (timerNode != null) return timerNode;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/misc/timerCountdown.fxml"));
            Node root = loader.load();

            TimerCountdownController controller = loader.getController();
            controller.setModel(this);

            timerNode = new Pair<>(root, controller);
            return timerNode;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Draws a line for the text-based user interface (TUI) representation of the timer.
     *
     * @param line the line number to draw
     * @return the string representation for the specified line
     */
    @Override
    public String drawLineTui(int line) {
        switch (line) {
            case 0:
                return "Timer flipped " + times + "/" + totalFlips;
            case 1:
                if (playerWhoFlipped == null) {
                    return String.format("%02d:%02d remaining", minutes, seconds);
                }
                else {
                    return String.format(playerWhoFlipped.drawLineTui(0) + " flipped the timer: %02d:%02d remaining", minutes, seconds);
                }
            default:
                return "";
        }
    }
}