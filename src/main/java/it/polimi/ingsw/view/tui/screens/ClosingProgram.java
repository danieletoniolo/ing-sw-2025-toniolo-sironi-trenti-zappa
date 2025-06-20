package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.tui.input.Parser;
import org.jline.terminal.Terminal;

public class ClosingProgram implements TuiScreenView{
    @Override
    public TuiScreenView setNewScreen() {
        return this;
    }

    @Override
    public void printTui(Terminal terminal) {

    }

    @Override
    public void readCommand(Parser parser) {

    }

    @Override
    public void setMessage(String message) {

    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Ending;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
