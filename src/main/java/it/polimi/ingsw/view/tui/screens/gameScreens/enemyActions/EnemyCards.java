package it.polimi.ingsw.view.tui.screens.gameScreens.enemyActions;

import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.Play;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions.ChooseDoubleCannonsCards;

import java.util.List;

public class EnemyCards extends CardsGame {

    public EnemyCards() {
        super(List.of("Active cannons", "Do not engage cannon systems"));
        String info = switch (MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getCardViewType()) {
            case SMUGGLERS -> "Smuggler convoy inbound - prepare for contact!";
            case PIRATES -> "Hostile ships detected – Space pirates approaching!";
            case SLAVERS -> "Alert! Slaver raiders incoming!";
            default -> "Error";
        };
        setMessage(info);
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        switch (selected) {
            case 0:
                spaceShipView = clientPlayer.getShip().clone();
                return new ChooseDoubleCannonsCards(this);
            case 1:
                // Surrender and end the turn
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }
                return nextScreen;
        }

        return this;
    }
}
