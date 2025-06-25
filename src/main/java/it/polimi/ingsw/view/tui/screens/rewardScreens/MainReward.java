package it.polimi.ingsw.view.tui.screens.rewardScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.Reward;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class MainReward extends Reward {

    public MainReward() {
        super(new ArrayList<>(){{
            String command = switch (MiniModel.getInstance().getRewardPhase()) {
                case 0 -> "Claim coins for finish position";
                case 1 -> "Claim coins for best looking ship";
                case 2 -> "Sell goods for coins";
                case 3 -> "Loose coins for losses";
                case 4 -> "Leave the game";
                default -> "";
            };
            add(command);
        }});

        if (MiniModel.getInstance().getRewardPhase() == 0) {
            setMessage("Game is over! Let's discover who is the winner.");
        }
        else {
            StringBuilder mess = new StringBuilder();
            int cont = 0;
            for (PlayerDataView p : sortedPlayers) {
                if (p.getCoins() > 0) {
                    if (cont == 0) {
                        mess.append(p.drawLineTui(0));
                        cont++;
                    }
                    else {
                        mess.append(", ").append(p.drawLineTui(0));
                    }
                }
            }
            mess.append(cont == 0 ? "No one is" : cont == 1 ? " is" : " are").append(" winning");
            setMessage(mess.toString());
        }
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 0) {
            // Send a request to change the reward phase
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            return nextScreen == null ? new WaitingReward() : nextScreen;
        }

        return this;
    }
}
