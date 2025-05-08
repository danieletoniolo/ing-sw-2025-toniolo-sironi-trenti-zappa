package view.tui.tiles;

import Model.SpaceShip.*;
import view.structures.board.LevelView;
import view.structures.components.*;
import view.structures.spaceship.SpaceShipView;

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
                case BATTERY:
                    tiles.add(new BatteryView(tile.getID(), connectors));
                    break;
                case CABIN:
                    tiles.add(new CabinView(tile.getID(), connectors));
                    break;
                case STORAGE:
                    tiles.add(new StorageView(tile.getID(), connectors, ((Storage) tile).isDangerous(), ((Storage) tile).getGoodsCapacity()));
                    break;
                case BROWN_LIFE_SUPPORT:
                    tiles.add(new LifeSupportBrownView(tile.getID(), connectors));
                    break;
                case PURPLE_LIFE_SUPPORT:
                    tiles.add(new LifeSupportPurpleView(tile.getID(), connectors));
                    break;
                case SINGLE_CANNON, DOUBLE_CANNON:
                    tiles.add(new CannonView(tile.getID(), connectors, ((Cannon) tile).getCannonStrength()));
                    break;
                case SINGLE_ENGINE, DOUBLE_ENGINE:
                    tiles.add(new EngineView(tile.getID(), connectors, ((Engine) tile).getEngineStrength()));
                    break;
                case SHIELD:
                    boolean[] shields = new boolean[4];
                    for (int i = 0; i < 4; i++) shields[i] = ((Shield) tile).canShield(i);
                    tiles.add(new ShieldView(tile.getID(), connectors, shields));
                    break;
                case CONNECTORS:
                    tiles.add(new ConnectorsView(tile.getID(), connectors));
                    break;
            }
        }


        for (ComponentView tile : tiles) {
            tile.setCovered(false);
            tile.setClockwiseRotation(3);
        }
        drawTiles(tiles, 13);
        /*
        SpaceShipView spaceShipView = new SpaceShipView(LevelView.SECOND);
        spaceShipView.placeComponent(tiles.get(155), 6, 6);
        spaceShipView.addReservedComponent(tiles.get(123));
        spaceShipView.addReservedComponent(tiles.get(1));
        spaceShipView.addReservedComponent(tiles.get(56));

        for (int i = 0; i < SpaceShipView.getRowToDraw(); i++) {
            System.out.println(spaceShipView.drawTui(i));
        }*/
    }

    public static void drawTile(ComponentView tile) {
        for (int i = 0; i < 5; i++) {
            System.out.print(tile.drawLineTui(i));
            System.out.println();
        }
    }

    public static void drawTiles(ArrayList<ComponentView> tiles, int cols) {
        for (int h = 0; h < tiles.size() / cols; h++) {
            for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
                for (int k = 0; k < cols; k++) {
                    System.out.print(tiles.get(h * cols + k).drawLineTui(i));
                }
                System.out.println();
            }
        }

        for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
            for (int k = 0; k < tiles.size() % cols; k++) {
                tiles.get(tiles.size() / cols * cols + k).setCovered(false);
                System.out.print(tiles.get((tiles.size() / cols) * cols + k).drawLineTui(i));
            }
            System.out.println();
        }
    }
}
