package it.polimi.ingsw.view.miniModel.timer;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class TimerView implements Structure {
    private int minutes;
    private int seconds;
    private PlayerDataView playerWhoFlipped;
    private int totalFlips;
    private int times;

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

    public void setFlippedTimer(PlayerDataView playerWhoFlipped) {
        this.playerWhoFlipped = playerWhoFlipped;
    }

    @Override
    public Image drawGui() {
        return null;
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
