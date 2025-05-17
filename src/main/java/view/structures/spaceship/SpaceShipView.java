package view.structures.spaceship;

import view.structures.Structure;
import view.structures.board.LevelView;
import view.structures.components.ComponentView;

import java.util.ArrayList;

public class SpaceShipView implements Structure {
    public static String UpReserved1 =     "╭────────";
    public static String LeftReserved2 =   "│   Disca";
    public static String DownReserved1 =   "╰────────";

    public static String UpReserved2 =     "────────╮";
    public static String RightReserved2 =  "d pile  │";
    public static String DownReserved2 =   "────────╯";


    private LevelView level;
    private boolean[][] viewable;
    private ComponentView[][] spaceShip;
    private ArrayList<ComponentView> reserved;

    public SpaceShipView(LevelView level) {
        this.level = level;
        switch (level) {
            case LEARNING:
                viewable = new boolean[][] {
                        {false, false, false, true , false, false, false},
                        {false, false, true , true , true , false, false},
                        {false, true , true , true , true , true , false},
                        {false, true , true , true , true , true , false},
                        {false, true , true , false, true , true , false}
                };
                break;
            case SECOND:
                viewable = new boolean[][] {
                        {false, false, true , false, true , false, false},
                        {false, true , true , true , true , true , false},
                        {true , true , true , true , true , true , true },
                        {true , true , true , true , true , true , true },
                        {true , true , true , false, true , true , true }
                };
                break;
        }
        spaceShip = new ComponentView[viewable.length][viewable[0].length];
        reserved = new ArrayList<>();
    }

    public void placeComponent(ComponentView component, int row, int col) {
        spaceShip[row-4][col-3] = component;
        spaceShip[row-4][col-3].setCovered(false);
    }

    public void removeComponent(int row, int col) {
        spaceShip[row-4][col-3] = null;
    }

    public void addReservedComponent(ComponentView component) {
        reserved.add(component);
    }

    public void removeReservedComponent(ComponentView component) {
        reserved.remove(component);
    }

    @Override
    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the spaceship here
    }

    public static int getRowToDraw() {
        return 5 * ComponentView.getRowsToDraw() + 2;
    }

    @Override
    public String drawLineTui(int line) {
        StringBuilder str = new StringBuilder();
        String space = "  ";

        if (line == 0 || line == getRowToDraw() - 1) {
            str.append("   ");
            for (int i = 0; i < viewable[0].length; i++) {
                str.append("    ").append(i + 4).append("    ");
            }
            return str.toString();
        }

        line = line - 1;
        boolean[] row = viewable[line / ComponentView.getRowsToDraw()];
        String number = line % ComponentView.getRowsToDraw() == 1 ? String.valueOf(line / ComponentView.getRowsToDraw() + 5) : " ";
        str.append(number).append(space);
        for (int i = 0; i < row.length; i++) {
            ComponentView tile = spaceShip[line / ComponentView.getRowsToDraw()][i];
            if (!row[i]) {
                str.append("         ");
            }
            else if (tile != null) {
                str.append(tile.drawLineTui(line % ComponentView.getRowsToDraw()));
            }
            else {
                switch (line % ComponentView.getRowsToDraw()) {
                    case 0 -> str.append(ComponentView.Up0);
                    case 1 -> str.append(ComponentView.clean);
                    case 2 -> str.append(ComponentView.Down0);
                }
            }
        }
        str.append(space).append(number);

        if (line / ComponentView.getRowsToDraw() == 0) {
            str.append(space);
            switch (line % ComponentView.getRowsToDraw()) {
                case 0:
                    if (reserved.isEmpty()) str.append(UpReserved1);
                    else str.append(reserved.getFirst().drawLineTui(0));
                    if (reserved.isEmpty() || reserved.size() == 1) str.append(UpReserved2);
                    else str.append(reserved.getLast().drawLineTui(0));
                    break;
                case 1:
                    if (reserved.isEmpty()) str.append(LeftReserved2);
                    else str.append(reserved.getFirst().drawLineTui(line % ComponentView.getRowsToDraw()));
                    if (reserved.isEmpty() || reserved.size() == 1) str.append(RightReserved2);
                    else str.append(reserved.getLast().drawLineTui(line % ComponentView.getRowsToDraw()));
                    break;
                case 2:
                    if (reserved.isEmpty()) str.append(DownReserved1);
                    else str.append(reserved.getFirst().drawLineTui(ComponentView.getRowsToDraw() - 1));
                    if (reserved.isEmpty() || reserved.size() == 1) str.append(DownReserved2);
                    else str.append(reserved.getLast().drawLineTui(ComponentView.getRowsToDraw() - 1));
                    break;
            }
        }

        return str.toString();
    }
}
