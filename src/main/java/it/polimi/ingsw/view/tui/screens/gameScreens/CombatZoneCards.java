package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class CombatZoneCards extends CardsGame {
    private static int cont = 0;

    public CombatZoneCards() {
        super(new ArrayList<>(){{
            if (MiniModel.getInstance().getBoardView().getLevel() == LevelView.LEARNING) {
                switch (cont) {
                    case 0 -> add("Take the penalty");
                    case 1 -> add("Active engines");
                    case 2 -> add("Active cannons");
                }
            }
            else {
                switch (cont) {
                    case 0 -> add("Active cannons");
                    case 1 -> add("Active engines");
                    case 2 -> add("Take the penalty");
                }
            }
        }});
        setMessage("Welcome to the Combat Zone! The player with the worst stats will get a penalty, so be careful!");
        cont++;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        super.setNextScreen(nextScreen);
    }

    public static void resetCont() {
        cont = 0;
    }

    @Override
    protected String lineBeforeInput() {
        if (MiniModel.getInstance().getBoardView().getLevel() == LevelView.LEARNING) {
            return "You have least crew members:";
        }

        return super.lineBeforeInput();
    }
}
