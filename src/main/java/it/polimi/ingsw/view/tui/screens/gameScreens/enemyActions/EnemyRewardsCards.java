package it.polimi.ingsw.view.tui.screens.gameScreens.enemyActions;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.Play;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.CardViewType;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsCards;

import java.util.List;

/**
 * Represents the screen where the player can choose to claim or refuse enemy rewards cards.
 * Extends {@link CardsGame} to provide options for claiming or refusing rewards.
 */
public class EnemyRewardsCards extends CardsGame {

    /**
     * Constructs the EnemyRewardsCards screen with two options:
     * "Claim rewards" and "Refuse rewards".
     */
    public EnemyRewardsCards() {
        super(List.of("Claim rewards", "Refuse rewards"));
    }

    /**
     * Handles the logic for setting the new screen based on the player's selection.
     * If the player claims rewards, it processes the claim and may transition to the goods menu
     * or end the turn. If the player refuses, it ends the turn directly.
     * Displays error messages if any server-side error occurs.
     *
     * @return the next {@link TuiScreenView} to display, or this screen if an error occurs.
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        switch (selected) {
            case 0:
                // Claim rewards and end the turn
                status = Play.requester(Client.transceiver, new Object()).request(new Play(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }

                if (MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getCardViewType().equals(CardViewType.SMUGGLERS)) {
                    return new MenuGoodsCards();
                }

                // Request to end the turn after claiming rewards
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }
                return nextScreen;
            case 1:
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
