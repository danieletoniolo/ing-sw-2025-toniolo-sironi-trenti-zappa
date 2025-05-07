package view.tui.tiles;

import Model.SpaceShip.*;
import view.structures.components.*;

import java.util.ArrayList;

public class TilesMain {
    public static void main(String[] args) {
        ArrayList<ComponentView> tiles = new ArrayList<>();
        Component[] modelTiles = TilesManager.getAllTiles();

        for (Component tile : modelTiles) {
            int[] connectors = new int[4];
            for (int j = 0; j < 4; j++) {
                switch (tile.getConnection(j)) {
                    case EMPTY -> connectors[j] = 0;
                    case SINGLE -> connectors[j] = 1;
                    case DOUBLE -> connectors[j] = 2;
                    case TRIPLE -> connectors[j] = 3;
                }
            }

            switch (tile.getComponentType()) {
                case BATTERY -> tiles.add(new BatteryView(tile.getID(), connectors));
                case CABIN -> tiles.add(new CabinView(tile.getID(), connectors));
                case STORAGE ->
                        tiles.add(new StorageView(tile.getID(), connectors, ((Storage) tile).isDangerous(), ((Storage) tile).getGoodsCapacity()));
                case BROWN_LIFE_SUPPORT -> tiles.add(new LifeSupportBrownView(tile.getID(), connectors));
                case PURPLE_LIFE_SUPPORT -> tiles.add(new LifeSupportPurpleView(tile.getID(), connectors));
                case SINGLE_CANNON, DOUBLE_CANNON ->
                        tiles.add(new CannonView(tile.getID(), connectors, ((Cannon) tile).getCannonStrength()));
                case SINGLE_ENGINE, DOUBLE_ENGINE ->
                        tiles.add(new EngineView(tile.getID(), connectors, ((Engine) tile).getEngineStrength()));
                case SHIELD -> tiles.add(new ShieldView(tile.getID(), connectors));
                case CONNECTORS -> tiles.add(new ConnectorsView(tile.getID(), connectors));
            }
        }

        for (ComponentView tile : tiles) {
            tile.setCovered(false);
            tile.setClockwiseRotation(2);
        }
        drawTiles(tiles, 13);
    }

    public static void drawTile(ComponentView tile) {
        for (int i = 0; i < 5; i++) {
            System.out.print(tile.drawLineTui(i));
            System.out.println();
        }
    }
    public static void drawTiles(ArrayList<ComponentView> tiles, int cols) {
        for (int h = 0; h < tiles.size() / cols; h++) {
            for (int i = 0; i < 5; i++) {
                for (int k = 0; k < cols; k++) {
                    System.out.print(tiles.get(h * cols + k).drawLineTui(i));
                }
                System.out.println();
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int k = 0; k < tiles.size() % cols; k++) {
                tiles.get(tiles.size() / cols * cols + k).setCovered(false);
                System.out.print(tiles.get((tiles.size() / cols) * cols + k).drawLineTui(i));
            }
            System.out.println();
        }
    }
}
