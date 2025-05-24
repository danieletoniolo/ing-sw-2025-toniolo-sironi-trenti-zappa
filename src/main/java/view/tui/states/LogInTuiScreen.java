package view.tui.states;

import org.jline.terminal.Terminal;
import view.miniModel.MiniModel;
import view.miniModel.logIn.LogInView;
import view.tui.input.Parser;

public class LogInTuiScreen implements TuiScreenView {
    private String nickname;
    private int totalLines = LogInView.getRowsToDraw() + 2 + 1;
    private String message;

    public LogInTuiScreen() {
    }

    public TuiScreenView readInput() {


        return null;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        nickname = parser.readNickname("Enter your nickname: ", totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        return new MenuTuiScreen();
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();

        for (int i = 0; i < LogInView.getRowsToDraw(); i++) {
            writer.println(MiniModel.getInstance().logInView.drawLineTui(i));
        }

        writer.println(message == null ? "" : message);
        writer.println();

        writer.flush();
        writer.println();
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
