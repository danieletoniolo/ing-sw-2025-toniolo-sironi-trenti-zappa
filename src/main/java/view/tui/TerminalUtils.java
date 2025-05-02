package view.tui;

public class TerminalUtils {
    public static void clearTerminal() {
        /*AnsiConsole.systemInstall();
        System.out.print(Ansi.ansi().eraseScreen().cursor(0, 0));
        System.out.flush();
        AnsiConsole.systemUninstall();*/

        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }
}
