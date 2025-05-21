package view.tui.states.gameScreens.goodActionState;

import view.tui.states.GameScreenTui;

public class SelectPlanetScreenTui extends GameScreenTui {

    public SelectPlanetScreenTui() {
        super(null);
    }


    @Override
    protected String lineBeforeInput() {
        return "Select a planet from the card:";
    }
}
