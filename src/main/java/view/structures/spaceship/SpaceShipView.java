package view.structures.spaceship;

import view.structures.Structure;
import view.structures.board.LevelView;
import view.structures.components.ComponentView;
import view.structures.components.GenericComponentView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Map<Integer, ComponentView> mapDoubleCannons = new HashMap<>();
    private Map<Integer, ComponentView> mapDoubleEngines = new HashMap<>();
    private Map<Integer, ComponentView> mapCabins = new HashMap<>();
    private Map<Integer, ComponentView> mapShield = new HashMap<>();
    private Map<Integer, ComponentView> mapStorages = new HashMap<>();
    private Map<Integer, ComponentView> mapBatteries = new HashMap<>();

    private final int converterRow = 5;
    private final int converterCol = 4;

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
        spaceShip[row-converterRow][col-converterCol] = component;
        spaceShip[row-converterRow][col-converterCol].setCovered(false);

        switch (component.getType()) {
            case DOUBLE_CANNON -> mapDoubleCannons.put(component.getID(), component);
            case DOUBLE_ENGINE -> mapDoubleEngines.put(component.getID(), component);
            case CABIN -> mapCabins.put(component.getID(), component);
            case SHIELD -> mapShield.put(component.getID(), component);
            case STORAGE -> mapStorages.put(component.getID(), component);
            case BATTERY -> mapBatteries.put(component.getID(), component);
        }

        spaceShip[row-converterRow][col-converterCol].setRow(row);
        spaceShip[row-converterRow][col-converterCol].setCol(col);
    }

    public void removeComponent(int row, int col) {
        ComponentView component = spaceShip[row-converterRow][col-converterCol];
        switch (component.getType()) {
            case DOUBLE_CANNON -> mapDoubleCannons.remove(component.getID());
            case DOUBLE_ENGINE -> mapDoubleEngines.remove(component.getID());
            case CABIN -> mapCabins.remove(component.getID());
            case SHIELD -> mapShield.remove(component.getID());
            case STORAGE -> mapStorages.remove(component.getID());
            case BATTERY -> mapBatteries.remove(component.getID());
        }

        spaceShip[row-converterRow][col-converterCol] = new GenericComponentView();
    }

    public Map<Integer, ComponentView> getMapDoubleCannons() {
        return mapDoubleCannons;
    }

    public Map<Integer, ComponentView> getMapDoubleEngines() {
        return mapDoubleEngines;
    }

    public Map<Integer, ComponentView> getMapCabins() {
        return mapCabins;
    }

    public Map<Integer, ComponentView> getMapShield() {
        return mapShield;
    }

    public Map<Integer, ComponentView> getMapStorages() {
        return mapStorages;
    }

    public Map<Integer, ComponentView> getMapBatteries() {
        return mapBatteries;
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

    public int getRowsToDraw() {
        return 5 * ComponentView.getRowsToDraw() + 2;
    }

    @Override
    public String drawLineTui(int line) {
        StringBuilder str = new StringBuilder();
        String space = "  ";

        if (line == 0 || line == getRowsToDraw() - 1) {
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
