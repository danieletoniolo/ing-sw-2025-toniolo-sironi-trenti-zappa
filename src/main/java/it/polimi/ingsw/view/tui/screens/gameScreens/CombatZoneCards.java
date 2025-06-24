package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions.ChooseDoubleCannonsCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.engineActions.ChooseDoubleEngineCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens.LooseCrewCards;

import java.util.ArrayList;

public class CombatZoneCards extends CardsGame {
    private static int cont = -1;
    private final LevelView level = MiniModel.getInstance().getBoardView().getLevel();

    public CombatZoneCards() {
        super(new ArrayList<>(){{
            cont++;
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
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (level == LevelView.LEARNING) {
            if (selected == 0) {
                return switch (cont) {
                    case 0 -> new LooseCrewCards();
                    case 1 -> new ChooseDoubleEngineCards();
                    case 2 -> new ChooseDoubleCannonsCards();
                    default -> null;
                };
            }
        }

        if (level == LevelView.SECOND) {
            if (selected == 0) {
                return switch (cont) {
                    case 0 -> new ChooseDoubleCannonsCards();
                    case 1 -> new ChooseDoubleEngineCards();
                    case 2 -> new LooseCrewCards();
                    default -> null;
                };
            }
        }

        return this;
    }

    public static void resetCont() {
        cont = -1;
    }

    @Override
    protected String lineBeforeInput() {
        boolean firstLevel = level == LevelView.LEARNING && cont == 0;
        boolean secondLevel = level == LevelView.SECOND && cont == 2;
        if (firstLevel || secondLevel) {
            return "You have least crew members:";
        }

        return super.lineBeforeInput();
    }
}
