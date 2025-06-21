package it.polimi.ingsw.view.miniModel.timer;

import it.polimi.ingsw.view.gui.controllers.misc.TimerCountdownController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimerView implements Structure, MiniModelObservable {
    private int minutes;
    private int seconds;
    private PlayerDataView playerWhoFlipped;
    private int totalFlips;
    private int times;
    private final List<MiniModelObserver> observers;

    public TimerView() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        synchronized (observers) {
            for (MiniModelObserver observer : observers) {
                observer.react();
            }
        }
    }

    public void setNumberOfFlips(int numberOfFlips) {
        this.times = numberOfFlips;
    }

    public void setTotalFlips(int totalFlips) {
        this.totalFlips = totalFlips;
    }

    public int getNumberOfFlips() {
        return times;
    }

    public int getTotalFlips() {
        return totalFlips;
    }

    public void setSecondsRemaining(int secondsRemaining) {
        minutes = secondsRemaining / 60;
        seconds = secondsRemaining % 60;
    }

    public int getSecondsRemaining() {
        return minutes * 60 + seconds;
    }

    public void setFlippedTimer(PlayerDataView playerWhoFlipped) {
        this.playerWhoFlipped = playerWhoFlipped;
        this.notifyObservers();
    }

    public Node getNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/misc/timerCountdown.fxml"));
            Node root = loader.load();

            TimerCountdownController controller = loader.getController();
            controller.setModel(this);

            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

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
