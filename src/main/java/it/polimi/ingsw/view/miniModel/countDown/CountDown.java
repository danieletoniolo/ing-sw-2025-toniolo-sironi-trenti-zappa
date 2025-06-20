package it.polimi.ingsw.view.miniModel.countDown;

import it.polimi.ingsw.view.miniModel.Structure;

public class CountDown implements Structure {
    private int secondsRemaining;

    public void setSecondsRemaining(int secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    @Override
    public String drawLineTui(int line) {
        return "Game starting in " + secondsRemaining + "...";
    }
}
