package view.tui.states.gameScreens;

import view.tui.states.GameScreenTui;

import java.util.List;

public class AbandonedStationScreenTui extends GameScreenTui {

    public AbandonedStationScreenTui() {
        super(List.of("Accept", "Refuse"));
    }

    @Override
    public void sendCommandToServer() {
        switch (selected) {
            case 0:
                break;
            case 1:
                break;
        }
    }

    @Override
    public String lineBeforeInput() {
        return "You have reached an abandoned station. Do you want to accept the offer?";
    }
}
