package view.TUI.tiles;

import Model.Good.Good;
import Model.SpaceShip.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ComponentView {
    private final int COLS = 12;
    private final String covered = "  Covered  ";
    private final String clean = "           ";
    private Boolean[] checked;
    private ArrayList<Component> components;

    public void setCompnentsToVisualize(ArrayList<Component> components) {
        this.components = components;
        checked = new Boolean[components.size()];
        Arrays.fill(checked, false);
    }

    public void setViewedTile(Component tile, Boolean status) {
        if (!components.contains(tile)) {
            return;
        }
        this.checked[components.indexOf(tile)] = status;
        //viewSingleTile(tile);
    }

    public void viewSingleTile(Component tile) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                switch (i) {
                    case 0, 6:
                        switch (j) {
                            case 0, 6 -> System.out.print(". ");
                            default -> System.out.print("_ ");
                        }
                        break;
                    case 1:
                        switch (j) {
                            case 0, 6 -> System.out.print("|");
                            case 1 -> System.out.print(firstLine(tile));
                        }
                        break;
                    case 2:
                        switch (j) {
                            case 0, 6 -> System.out.print("|");
                            case 1 -> System.out.print(secondLine(tile));
                        }
                        break;
                    case 3:
                        switch (j) {
                            case 0, 6 -> System.out.print("|");
                            case 1 -> System.out.print(middleLine(tile));
                        }
                        break;
                    case 4:
                        switch (j) {
                            case 0, 6 -> System.out.print("|");
                            case 1 -> System.out.print(fourthLine(tile));
                        }
                        break;
                    case 5:
                        switch (j) {
                            case 0, 6 -> System.out.print("|");
                            case 1 -> System.out.print(fifthLine(tile));
                        }
                        break;
                }
            }
            System.out.println();
        }
    }

    public void drawOnBoardComponents() {
        int row;
        for (row = 0; row < components.size()/COLS; row++) {
            drawLineTiles(row, COLS);
        }
        drawLineTiles(row, components.size() % COLS);
    }

    private void drawLineTiles(int row, int cols) {
        for (int i = 0; i < 7; i++) {
            for (int k = 0; k < cols; k++) {
                Component tile = components.get(row * COLS + k);
                Boolean checked = this.checked[row * COLS + k];
                for (int j = 0; j < 7; j++) {
                    switch (i) {
                        case 0, 6:
                            switch (j) {
                                case 0, 6 -> System.out.print(checked == null ? "  " : ". ");
                                default -> System.out.print(checked == null ? "  " : "_ ");
                            }
                            break;
                        case 1:
                            switch (j) {
                                case 0 -> System.out.print(checked == null ? " " : "|");
                                case 1 -> System.out.print(Boolean.TRUE.equals(checked) ? firstLine(tile) : clean);
                                case 6 -> System.out.print(checked == null ? "  " : "| ");
                            }
                            break;
                        case 2:
                            switch (j) {
                                case 0 -> System.out.print(checked == null ? " " : "|");
                                case 1 -> System.out.print(Boolean.TRUE.equals(checked) ? secondLine(tile) : clean);
                                case 6 -> System.out.print(checked == null ? "  " : "| ");
                            }
                            break;
                        case 3:
                            switch (j) {
                                case 0 -> System.out.print(checked == null ? " " : "|");
                                case 1 -> System.out.print(Boolean.TRUE.equals(checked) ? middleLine(tile) : Boolean.FALSE.equals(checked) ? covered : clean);
                                case 6 -> System.out.print(checked == null ? "  " : "| ");
                            }
                            break;
                        case 4:
                            switch (j){
                                case 0 -> System.out.print(checked == null ? " " : "|");
                                case 1 -> System.out.print(Boolean.TRUE.equals(checked) ? fourthLine(tile) : clean);
                                case 6 -> System.out.print(checked == null ? "  " : "| ");
                            }
                            break;
                        case 5:
                            switch (j){
                                case 0 -> System.out.print(checked == null ? " " : "|");
                                case 1 -> System.out.print(Boolean.TRUE.equals(checked) ? fifthLine(tile) : clean);
                                case 6 -> System.out.print(checked == null ? "  " : "| ");
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
            return "     " + "v" + "     ";
        }
        if (c instanceof Cannon && c.getClockwiseRotation() == 0) {
            return "     " + "^" + "     ";
        }
        return "     " + getConnectionString(c, 0) + "     ";
    }

    private String secondLine(Component c) {
        //length = 11 for the String
        return switch (c.getComponentType()) {
            case SINGLE_ENGINE, DOUBLE_ENGINE -> "  Engine   ";
            case SINGLE_CANNON, DOUBLE_CANNON -> "  Cannon   ";
            case BROWN_LIFE_SUPPORT, PURPLE_LIFE_SUPPORT -> "LifeSupport";
            case CABIN -> "   Cabin   ";
            case SHIELD -> "   Shield  ";
            case BATTERY -> "  Battery  ";
            case STORAGE -> "  Storage  ";
            case CONNECTORS -> "   Conne-  ";
            default -> "           ";
        };
    }

    private String middleLine(Component c) {
        // length = 9
        String middle = switch (c.getComponentType()) {
            case BATTERY -> "  Num: " + ((Battery) c).getEnergyNumber() + " ";
            case CABIN -> " Crew: " + ((Cabin) c).getCrewNumber() + " ";
            case DOUBLE_CANNON, SINGLE_CANNON -> " Power:" + ((Cannon) c).getCannonStrength();
            case CONNECTORS -> "  ctors  ";
            case SINGLE_ENGINE, DOUBLE_ENGINE -> " Power:" + ((Engine) c).getEngineStrength() + " ";
            case BROWN_LIFE_SUPPORT, PURPLE_LIFE_SUPPORT -> " Support ";
            case SHIELD -> " Shield  ";
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
            if (c.getClockwiseRotation() == 1) {
                left = "<";
            }
            if (c.getClockwiseRotation() == 3) {
                right = ">";
            }
        }
        return left + middle + right;
    }

    private String fourthLine(Component c) {
        // length = 11
        return switch (c.getComponentType()) {
            case BROWN_LIFE_SUPPORT -> "   Brown   ";
            case PURPLE_LIFE_SUPPORT -> "  Purple   ";
            case STORAGE -> {
                if (((Storage) c).getGoods() == null) {
                    yield "           ";
                }
                String line = "  G:";
                for (Good g : ((Storage) c).getGoods()) {
                    line += g.getValue() + " ";
                }
                while(line.length() < 11) {
                    line += " ";
                }
                yield line;
            }
            case CABIN -> "  Alien:" + (((Cabin) c).hasPurpleAlien() ? "P  " : ((Cabin) c).hasBrownAlien() ? "B  " : "X  ");
            default -> "           ";
        };
    }

    private String fifthLine(Component c) {
        if (c instanceof Engine && c.getClockwiseRotation() == 0) {
            return "     " + "^" + "     ";
        }
        if (c instanceof Cannon && c.getClockwiseRotation() == 2) {
            return "     " + "v" + "     ";
        }
        return "     " + getConnectionString(c, 2) + "     ";
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
