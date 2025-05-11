package view.structures.logIn;

public class LogInView {

    public void drawMenuGui() {
        //TODO: Implement the GUI drawing logic
    }

    public static int getRowsToDraw() {
        return 10;
    }

    public String drawLineTui(int line) {
        return switch (line) {
            case 0 -> " _______      ___       __          ___      ___   ___ ____    ____    .___________..______       __    __    ______  __  ___  _______ .______      ";
            case 1 -> " /  _____|    /   \\     |  |        /   \\     \\  \\ /  / \\   \\  /   /    |           ||   _  \\     |  |  |  |  /      ||  |/  / |   ____||   _  \\     ";
            case 2 -> "|  |  __     /  ^  \\    |  |       /  ^  \\     \\  V  /   \\   \\/   /     `---|  |----`|  |_)  |    |  |  |  | |  ,----'|  '  /  |  |__   |  |_)  |    ";
            case 3 -> "|  | |_ |   /  /_\\  \\   |  |      /  /_\\  \\     >   <     \\_    _/          |  |     |      /     |  |  |  | |  |     |    <   |   __|  |      /     ";
            case 4 -> "|  |__| |  /  _____  \\  |  `----./  _____  \\   /  .  \\      |  |            |  |     |  |\\  \\----.|  `--'  | |  `----.|  .  \\  |  |____ |  |\\  \\----.";
            case 5 -> " \\______| /__/     \\__\\ |_______/__/     \\__\\ /__/ \\__\\     |__|            |__|     | _| `._____| \\______/   \\______||__|\\__\\ |_______|| _| `._____|";
            case 6, 8 -> "\n";
            case 7 -> "Welcome to Galaxy Trucker!!";
            case 9 -> "Log in:";
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }
}
