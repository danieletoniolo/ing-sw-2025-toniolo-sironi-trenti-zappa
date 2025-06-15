package it.polimi.ingsw.view.tui.screens.validationScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ChooseFragment;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.ClosingProgram;
import it.polimi.ingsw.view.tui.screens.PlayerTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.ValidationTuiScreen;
import org.javatuples.Pair;

import java.util.List;

public class ValidationFragments extends ValidationTuiScreen {

    public ValidationFragments() {
        super();
        options.clear();
        setMessage("Your ship is fragmented, you have to choose which group of components to keep");

        int i = 1;
        for (List<Pair<Integer, Integer>> group : MiniModel.getInstance().getClientPlayer().getShip().getFragments()) {
            StringBuilder line = new StringBuilder();
            line.append("Group ").append(i).append(": ");
            for (Pair<Integer, Integer> tile : group) {
                line.append("(").append(tile.getValue0() + 1).append(" ").append(tile.getValue1() + 1).append(") ");
            }
            options.add(line.toString());
            i++;
        }

        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
        options.add("Close program");
    }

    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);
            return new PlayerTuiScreen(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

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
            return new WaitingValidationTuiScreen();
        }

        return this;
    }

    @Override
    protected String lineBeforeInput() {
        return "Fragments:";
    }
}
