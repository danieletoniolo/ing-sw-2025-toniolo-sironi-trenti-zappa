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

/**
 * EnemyCards is a screen in the TUI that displays enemy card actions to the player.
 * It extends CardsGame and allows the player to choose between activating cannons or not engaging.
 * The screen shows information about the incoming enemy type and handles the player's selection.
 */
public class EnemyCards extends CardsGame {

    /**
     * Constructs the EnemyCards screen, initializing the options and displaying
     * information about the top enemy card in the deck.
     */
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

    /**
     * Handles the transition to a new screen based on the player's selection.
     * If the player chooses to activate cannons, transitions to the ChooseDoubleCannonsCards screen.
     * If the player chooses not to engage, sends an EndTurn request and handles any errors.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        switch (selected) {
            case 0:
                spaceShipView = clientPlayer.getShip().clone();
                return new ChooseDoubleCannonsCards();
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
