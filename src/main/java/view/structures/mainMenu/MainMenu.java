package view.structures.mainMenu;

import view.tui.lobby.LobbyView;

import java.util.ArrayList;

public class MainMenu {
    private String nickname;
    private ArrayList<LobbyView> lobbies;

    public MainMenu(String nickname, ArrayList<LobbyView> lobbies) {
        this.nickname = nickname;
        this.lobbies = lobbies;
    }

    public void drawMenuGui() {
        //TODO: Implement the GUI drawing logic
    }

    public void drawLineTui(int line) {
        StringBuilder str = new StringBuilder();

        if (line == 0) {

        }


        System.out.println(str);
    }
}
