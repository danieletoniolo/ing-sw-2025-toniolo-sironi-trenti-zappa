package controller;

public class MatchController {
    // TODO: merge with RMI for the users

    private static MatchController instance;

    public MatchController() {}

    public static MatchController getInstance() {
        if (instance == null) {
            instance = new MatchController();
        }
        return instance;
    }

    public void createLobby() {

    }

}
