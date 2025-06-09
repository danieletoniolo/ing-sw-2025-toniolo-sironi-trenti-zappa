package it.polimi.ingsw.view.miniModel.logIn;

public class LogInView {

    public void drawMenuGui() {
        //TODO: Implement the GUI drawing logic
    }

    public static int getRowsToDraw() {
        return 7;
    }

    public String drawLineTui(int line) {
        return switch (line) {
            case 0 -> "  ___   __   __     __   _  _  _  _    ____  ____  _  _   ___  __ _  ____  ____  ";
            case 1 -> " / __) / _\\ (  )   / _\\ ( \\/ )( \\/ )  (_  _)(  _ \\/ )( \\ / __)(  / )(  __)(  _ \\ ";
            case 2 -> "( (_ \\/    \\/ (_/\\/    \\ )  (  )  /     )(   )   /) \\/ (( (__  )  (  ) _)  )   / ";
            case 3 -> " \\___/\\_/\\_/\\____/\\_/\\_/(_/\\_)(__/     (__) (__\\_)\\____/ \\___)(__\\_)(____)(__\\_) ";
            case 4, 6 -> "\n";
            case 5 -> "Welcome to Galaxy Trucker!!";
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }
}
