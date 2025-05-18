package view.structures.spaceship;

import view.structures.Structure;
import view.structures.board.LevelView;
import view.structures.components.ComponentView;
import view.structures.components.GenericComponentView;

import java.util.ArrayList;

public class SpaceShipView implements Structure {
    public static String UpReserved1 =     "╭──────";
    public static String LeftReserved2 =   "│      ";
    public static String DownReserved1 =   "╰──────";

    public static String UpReserved2 =     "──────╮";
    public static String RightReserved2 =  "      │";
    public static String DownReserved2 =   "──────╯";


    private LevelView level;
    private ComponentView[][] spaceShip;
    private ArrayList<ComponentView> reserved;

    public SpaceShipView(LevelView level) {
        this.level = level;
        switch (level) {
            case LEARNING:
                spaceShip = new ComponentView[][] {
                        {null, null, null, new GenericComponentView() , null, null, null},
                        {null, null, new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , null, null},
                        {null, new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , null},
                        {null, new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , null},
                        {null, new GenericComponentView() , new GenericComponentView() , null, new GenericComponentView() , new GenericComponentView() , null}
                };
                break;
            case SECOND:
                spaceShip = new ComponentView[][] {
                        {null, null, new GenericComponentView() , null, new GenericComponentView() , null, null},
                        {null, new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , null},
                        {new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() },
                        {new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , new GenericComponentView() },
                        {new GenericComponentView() , new GenericComponentView() , new GenericComponentView() , null, new GenericComponentView() , new GenericComponentView() , new GenericComponentView() }
                };
                break;
        }
        for (int i = 0; i < spaceShip.length; i++) {
            for (int j = 0; j < spaceShip[i].length; j++) {
                if (spaceShip[i][j] != null) {
                    spaceShip[i][j].setCovered(false);
                }
            }
        }
        reserved = new ArrayList<>();
    }

    public void placeComponent(ComponentView component, int row, int col) {
        spaceShip[row-4][col-3] = component;
        spaceShip[row-4][col-3].setCovered(false);
    }

    public void removeComponent(int row, int col) {
        spaceShip[row-4][col-3] = new GenericComponentView();
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
            for (int i = 0; i < spaceShip[0].length; i++) {
                str.append("   ").append(i + 4).append("   ");
            }
            if (line == 0) str.append("     ").append("Discard pile:");
            return str.toString();
        }

        line = line - 1;
        ComponentView[] row = spaceShip[line / ComponentView.getRowsToDraw()];
        String number = line % ComponentView.getRowsToDraw() == 1 ? String.valueOf(line / ComponentView.getRowsToDraw() + 5) : " ";
        str.append(number).append(space);
        for (ComponentView tile : row) {
            if (tile == null) {
                str.append("       ");
            }
            else {
                str.append(tile.drawLineTui(line % ComponentView.getRowsToDraw()));
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
