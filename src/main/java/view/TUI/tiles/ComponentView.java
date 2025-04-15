package view.TUI.tiles;

import Model.Good.Good;
import Model.SpaceShip.*;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class ComponentView {
    private final String covered = "|  Covered  |";
    private final String clean = "|           |";
    private final String line = ". _ _ _ _ _ .";
    private final String spaces = "             ";

    /**
     * Draw one line with tot tiles. If component == null draw spaces, if Boolean of component is set false draw a covered tile
     * @param components ArrayList of Pair with the component to print and Boolean that say if the tile is covered
     */
    public void drawTilesOnOneLine(ArrayList<Pair<Component, Boolean>> components) {
        for (int i = 0; i < 7; i++) {
            for (int k = 0; k < components.size(); k++) {
                Component tile = components.get(k).getValue0();
                Boolean check = components.get(k).getValue1();
                for (int j = 0; j < 7; j++) {
                    switch (i) {
                        case 0, 6:
                            if (j == 0) {
                                System.out.print(tile != null ? line : spaces);
                            }
                            break;
                        case 1:
                            if (j == 0) {
                                System.out.print(tile != null ? check ? firstLine(tile) : clean : spaces);
                            }
                            break;
                        case 2:
                            if (j == 0) {
                                System.out.print(tile != null ? check ? secondLine(tile) : clean : spaces);
                            }
                            break;
                        case 3:
                            if (j == 0) {
                                System.out.print(tile != null ? check ? middleLine(tile) : covered : spaces);
                            }
                            break;
                        case 4:
                            if (j == 0) {
                                System.out.print(tile != null ? check ? fourthLine(tile) : clean : spaces);
                            }
                            break;
                        case 5:
                            if (j == 0) {
                                System.out.print(tile != null ? check ? fifthLine(tile) : clean : spaces);
                            }
                            break;
                    }
                }
            }
            System.out.println();
        }
    }

    private String firstLine(Component c) {
        if (c instanceof Engine && c.getClockwiseRotation() == 2) {
            return "|     " + "v" + "     |";
        }
        if (c instanceof Cannon && c.getClockwiseRotation() == 0) {
            return "|     " + "^" + "     |";
        }
        return "|     " + getConnectionString(c, 0) + "     |";
    }

    private String secondLine(Component c) {
        //length = 11 for the String
        return switch (c.getComponentType()) {
            case SINGLE_ENGINE, DOUBLE_ENGINE -> "|  Engine   |";
            case SINGLE_CANNON, DOUBLE_CANNON -> "|  Cannon   |";
            case BROWN_LIFE_SUPPORT, PURPLE_LIFE_SUPPORT -> "|   Life    |";
            case CABIN -> "|   Cabin   |";
            case SHIELD -> "|  Shield   |";
            case BATTERY -> "|  Battery  |";
            case STORAGE -> "|  Storage  |";
            case CONNECTORS -> "|   Conne-  |";
            default -> clean;
        };
    }

    private String middleLine(Component c) {
        // length = 9
        String middle = switch (c.getComponentType()) {
            case BATTERY -> "  Num: " + ((Battery) c).getEnergyNumber() + " ";
            case CABIN -> " Crew: " + ((Cabin) c).getCrewNumber() + " ";
            case DOUBLE_CANNON, SINGLE_CANNON -> " Pow:" + ((Cannon) c).getCannonStrength() + " ";
            case CONNECTORS -> "  ctors  ";
            case SINGLE_ENGINE, DOUBLE_ENGINE -> " Power:" + ((Engine) c).getEngineStrength() + " ";
            case BROWN_LIFE_SUPPORT, PURPLE_LIFE_SUPPORT -> " Support ";
            case SHIELD -> {
                String line = " ";
                line += (((Shield) c).canShield(0) ? "^" : " ");
                line += " ";
                line += (((Shield) c).canShield(1) ? "<" : " ");
                line += " ";
                line += (((Shield) c).canShield(2) ? "v" : " ");
                line += " ";
                line += (((Shield) c).canShield(3) ? ">" : " ");
                line += " ";
                yield line;
            }
            case STORAGE -> " Cap:" + ((Storage) c).getGoodsCapacity() + " " + (((Storage) c).isDangerous() ? "R " : "B ");
            default -> "Unknown";
        };
        // 1 + 9 + 1
        String left = getConnectionString(c, 1);
        String right = getConnectionString(c, 3);
        if (c instanceof Engine) {
            if (c.getClockwiseRotation() == 1) {
                left = ">";
            }
            if (c.getClockwiseRotation() == 3) {
                right = "<";
            }
        }
        if (c instanceof Cannon) {
            if (c.getClockwiseRotation() == 3) {
                left = "<";
            }
            if (c.getClockwiseRotation() == 1) {
                right = ">";
            }
        }
        return "|" + left + middle + right + "|";
    }

    private String fourthLine(Component c) {
        // length = 11
        return switch (c.getComponentType()) {
            case BROWN_LIFE_SUPPORT -> "|   Brown   |";
            case PURPLE_LIFE_SUPPORT -> "|  Purple   |";
            case STORAGE -> {
                if (((Storage) c).getGoods() == null) {
                    yield clean;
                }
                String line = "  G:";
                for (Good g : ((Storage) c).getGoods()) {
                    line += g.getValue() + " ";
                }
                while(line.length() < 11) {
                    line += " ";
                }
                yield "|" + line + "|";
            }
            case CABIN -> "|  Alien:" + (((Cabin) c).hasPurpleAlien() ? "P  " : ((Cabin) c).hasBrownAlien() ? "B  " : "X  " + "|");
            default -> clean;
        };
    }

    private String fifthLine(Component c) {
        if (c instanceof Engine && c.getClockwiseRotation() == 0) {
            return "|     " + "^" + "     |";
        }
        if (c instanceof Cannon && c.getClockwiseRotation() == 2) {
            return "|     " + "v" + "     |";
        }
        return "|     " + getConnectionString(c, 2) + "     |";
    }

    private String getConnectionString(Component c, int face) {
        return switch (c.getConnection(face)) {
            case EMPTY -> " ";
            case SINGLE -> "1";
            case DOUBLE -> "2";
            case TRIPLE -> "3";
        };
    }
}
