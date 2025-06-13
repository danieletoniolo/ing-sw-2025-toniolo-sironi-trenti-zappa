package it.polimi.ingsw.view.miniModel.countDown;

import it.polimi.ingsw.view.miniModel.Structure;
import javafx.scene.image.Image;

public class CountDown implements Structure {
    private int secondsRemaining;

    public void setSecondsRemaining(int secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    @Override
    public Image drawGui() {
        return null;
    }

    @Override
    public String drawLineTui(int line) {
        return "Game starting in " + secondsRemaining + "...";
    }
}
