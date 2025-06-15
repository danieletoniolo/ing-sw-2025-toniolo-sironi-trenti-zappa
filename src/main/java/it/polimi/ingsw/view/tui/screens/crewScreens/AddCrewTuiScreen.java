package it.polimi.ingsw.view.tui.screens.crewScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ManageCrewMember;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.CabinView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.ClosingProgram;
import it.polimi.ingsw.view.tui.screens.ModifyCrewTuiScreen;
import it.polimi.ingsw.view.tui.screens.PlayerTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.Map;

public class AddCrewTuiScreen extends ModifyCrewTuiScreen {
    private final TuiScreenView oldScreen;
    private final int value;

    public AddCrewTuiScreen(TuiScreenView oldScreen, int value) {
        super();
        options.clear();

        if (value == 3) {
            options.addAll(spaceShipView.getMapCabins().values().stream()
                    .filter(cabin -> cabin.getCrewNumber() != 0)
                    .map(cabin -> "(" + cabin.getRow() + " " + cabin.getCol() + ")")
                    .toList());
        }
        else  {
            options.addAll(spaceShipView.getMapCabins().values().stream()
                    .filter(cabin -> cabin.getCrewNumber() == 0)
                    .map(cabin -> "(" + cabin.getRow() + " " + cabin.getCol() + ")")
                    .toList());
        }
        options.add("Back");
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
        options.add("Close program");

        this.oldScreen = oldScreen;
        this.value = value;
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

        if (selected == spaceShipView.getMapCabins().size()) {
            return new ModifyCrewTuiScreen();
        }

        int ID;
        if (value == 3) {
            ID = spaceShipView.getMapCabins().entrySet().stream()
                    .filter(entry -> entry.getValue().getCrewNumber() != 0)
                    .skip(selected)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(-1);
        }
        else {
            ID = spaceShipView.getMapCabins().entrySet().stream()
                    .filter(entry -> entry.getValue().getCrewNumber() == 0)
                    .skip(selected)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(-1);
        }

        CabinView cabin = spaceShipView.getMapCabins().get(ID);

        int mode = 0;
        int type;
        if (value == 3) {
            mode = 1;
            type = cabin.hasBrownAlien() ? 1 : cabin.hasPurpleAlien() ? 2 : 0;
        }
        else{
            type = value;
        }

        Logger.getInstance().logError("type: " + type + ", mode: " + mode + ", ID: " + ID, false);
        StatusEvent status;
        status = ManageCrewMember.requester(Client.transceiver, new Object()).request(new ManageCrewMember(MiniModel.getInstance().getUserID(), mode, type, ID));
        if (status.get().equals("POTA")) {
            oldScreen.setMessage(((Pota) status).errorMessage());
            return oldScreen;
        }
        return oldScreen;
    }
}
