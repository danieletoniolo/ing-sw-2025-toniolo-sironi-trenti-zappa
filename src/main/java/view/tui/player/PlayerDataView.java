package view.tui.player;

import Model.Player.PlayerColor;
import Model.Player.PlayerData;

public class PlayerDataView {
    public void drawPlayer(PlayerData player) {
        System.out.println("Color: " + player.getColor().toString());
        System.out.println("Username: " + player.getUsername());
        System.out.println("UUID: " + player.getUUID());
        System.out.println("Position: " + player.getPosition());
        System.out.println("Step: " + player.getStep());
        //System.out.println("SpaceShip: " + player.getSpaceShip());
        System.out.println("Coins: " + player.getCoins());
        System.out.println("Has given up: " + player.hasGivenUp());
        System.out.println("Is disconnected: " + player.isDisconnected());
        System.out.println("Is leader: " + player.isLeader());
    }

    public static void main(String[] args) {
        PlayerData player1 = new PlayerData("d290f1ee-6c54-4b01-90e6-d701748f0851", PlayerColor.RED, null);
        PlayerDataView playerDataView = new PlayerDataView();
        playerDataView.drawPlayer(player1);
    }
}
