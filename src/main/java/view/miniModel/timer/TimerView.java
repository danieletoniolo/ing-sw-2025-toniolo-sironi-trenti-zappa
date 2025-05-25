package view.miniModel.timer;

import view.miniModel.Structure;
import view.miniModel.player.PlayerDataView;

public class TimerView implements Structure {
    private int secondsRemaining;
    private int minutes;
    private int seconds;
    private PlayerDataView flippedTimer;
    private int totalFlips;
    private int times;

    public TimerView(int totalFlips) {
        this.totalFlips = totalFlips;
    }

    public void setSecondsRemaining(int secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
        minutes = secondsRemaining / 60;
        seconds = secondsRemaining % 60;
        if (secondsRemaining == 90) {
            times++;
        }
    }

    public void setFlippedTimer(PlayerDataView flippedTimer) {
        this.flippedTimer = flippedTimer;
    }

    @Override
    public void drawGui() {

    }

    @Override
    public String drawLineTui(int line) {
        switch (line) {
            case 0:
                return "Timer flipped " + times + "/" + totalFlips;
            case 1:
                if (flippedTimer == null) {
                    return String.format("%02d:%02d remaining", minutes, seconds);
                }
                else {
                    return String.format(flippedTimer.drawLineTui(0) + " flipped the timer: %02d:%02d remaining", minutes, seconds);
                }
            default:
                return "";
        }
    }
}
