package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.tui.input.Parser;
import org.jline.terminal.Terminal;

import java.util.function.Supplier;

public class ClosingProgram implements TuiScreenView{
    @Override
    public TuiScreenView setNewScreen() {
        return null;
    }

    @Override
    public void printTui(Terminal terminal) {

    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {

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
