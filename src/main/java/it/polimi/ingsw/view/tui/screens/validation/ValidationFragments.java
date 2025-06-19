package it.polimi.ingsw.view.tui.screens.validation;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ChooseFragment;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.ValidationTuiScreen;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ValidationFragments extends ValidationTuiScreen {

    public ValidationFragments() {
        super(new ArrayList<>(){{
            int i = 1;
            for (List<Pair<Integer, Integer>> group : MiniModel.getInstance().getClientPlayer().getShip().getFragments()) {
                StringBuilder line = new StringBuilder();
                line.append("Group ").append(i).append(": ");
                for (Pair<Integer, Integer> tile : group) {
                    line.append("(").append(tile.getValue0() + 1).append(" ").append(tile.getValue1() + 1).append(") ");
                }
                add(line.toString());
                i++;
            }
        }});
        setMessage("Your ship is fragmented, you have to choose which group of components to keep");
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        if (selected >= 0 && selected < MiniModel.getInstance().getClientPlayer().getShip().getFragments().size()) {
            status = ChooseFragment.requester(Client.transceiver, new Object()).request(new ChooseFragment(MiniModel.getInstance().getUserID(), selected));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }
            return new WaitingValidation();
        }

        return this;
    }

    @Override
    protected String lineBeforeInput() {
        return "Fragments:";
    }
}
