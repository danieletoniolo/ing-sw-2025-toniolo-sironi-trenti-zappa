package it.polimi.ingsw.view.tui.screens.crewScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.tui.screens.ModifyCrew;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

/**
 * MainCrew is a screen in the TUI that allows the user to modify their crew members.
 * It extends ModifyCrew and provides options to add or remove crew members based on the current board level.
 */
public class MainCrew extends ModifyCrew {

    /**
     * Constructs the MainCrew screen with options depending on the current board level.
     * Sets the appropriate message for the user.
     */
    public MainCrew() {
        super(new ArrayList<>(){{
            if (MiniModel.getInstance().getBoardView().getLevel() == LevelView.SECOND) {
                add("Add humans");
                add("Add brown alien");
                add("Add purple alien");
                add("Remove crew from cabin");
            }
            add("Done");
        }});
        if (MiniModel.getInstance().getBoardView().getLevel() == LevelView.SECOND) {
            setMessage("Modify your crew members");
        }
        if (MiniModel.getInstance().getBoardView().getLevel() == LevelView.LEARNING) {
            setMessage("Human crew members have boarded your spaceship!");
        }
    }

    /**
     * Sets the new screen based on the user's selection.
     * If the user selects "Done", sends an end turn request and handles the response.
     * Otherwise, returns a new AddCrew screen for the selected option.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == options.size() - 2 - MiniModel.getInstance().getOtherPlayers().size()) {
            // Send end turn request
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            if (nextScreen == null) {
                return new WaitingCrew();
            }
            return nextScreen;
        }

        return new AddCrew(selected);
    }
}
