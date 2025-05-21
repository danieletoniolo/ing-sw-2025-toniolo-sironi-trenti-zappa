package view.tui.states;

import org.jline.terminal.Terminal;
import view.miniModel.MiniModel;
import view.miniModel.logIn.LogInView;
import view.tui.input.Parser;

public class LogInScreenTui implements ScreenTuiView {
    private String nickname;
    private int totalLines = LogInView.getRowsToDraw() + 1;

    public LogInScreenTui() {
    }

    public ScreenTuiView readInput() {


        return null;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        nickname = parser.readNickname("Enter your nickname: ", totalLines);
    }

    @Override
    public ScreenTuiView isViewCommand() {
        return null;
    }

    @Override
    public void sendCommandToServer() {
        //TODO: Implement this method to send the command to the server
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();

        for (int i = 0; i < LogInView.getRowsToDraw(); i++) {
            writer.println(MiniModel.getInstance().logInView.drawLineTui(i));
        }

        writer.flush();
        writer.println();
    }
}
