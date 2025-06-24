package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.CombatZoneView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions.ChooseDoubleCannonsCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.engineActions.ChooseDoubleEngineCards;

import java.util.ArrayList;

public class CombatZoneCards extends CardsGame {
    private final int level = MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getLevel();

    public CombatZoneCards() {
        super(new ArrayList<>(){{
            if (MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getLevel() == 1) {
                switch (((CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek()).getCont()) {
                    case 0 -> add("Take the penalty");
                    case 1 -> add("Active engines");
                    case 2 -> add("Active cannons");
                }
            }
            else {
                switch (((CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek()).getCont()) {
                    case 0 -> add("Active cannons");
                    case 1 -> add("Active engines");
                    case 2 -> add("Take the penalty");
                }
            }
        }});

        switch (((CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek()).getCont()) {
            case 0 -> setMessage("Welcome to the Combat Zone! The player with the worst stats will get a penalty, so be careful!");
            case 1 -> setMessage("Another challenge awaits you in the Combat Zone! Choose wisely, the cost of failure is high!");
            case 2 -> setMessage("We are almost done! But be careful, the last is challenge could destroy your spaceship!");
        }

    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (level == 1) {
            if (selected == 0) {
                return switch (((CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek()).getCont()) {
                    case 0 -> {
                        StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                            setMessage(((Pota) status).errorMessage());
                            yield this;
                        }
                        yield nextScreen;
                    }
                    case 1 -> new ChooseDoubleEngineCards();
                    case 2 -> new ChooseDoubleCannonsCards();
                    default -> null;
                };
            }
        }

        if (level == 2) {
            if (selected == 0) {
                return switch (((CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek()).getCont()) {
                    case 0 -> new ChooseDoubleCannonsCards();
                    case 1 -> new ChooseDoubleEngineCards();
                    case 2 -> {
                        StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                            setMessage(((Pota) status).errorMessage());
                            yield this;
                        }
                        yield nextScreen;
                    }
                    default -> null;
                };
            }
        }

        return this;
    }

    @Override
    protected String lineBeforeInput() {
        boolean firstLevel = level == 1 && ((CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek()).getCont() == 0;
        boolean secondLevel = level == 2 && ((CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek()).getCont() == 2;
        if (firstLevel || secondLevel) {
            return "You have least crew members:";
        }

        return super.lineBeforeInput();
    }
}
