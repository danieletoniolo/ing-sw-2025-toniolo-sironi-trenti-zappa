package it.polimi.ingsw.view.miniModel.spaceship;

import org.javatuples.Pair;
import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.components.GenericComponentView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpaceShipView implements Structure {
    private LevelView level;
    private ComponentView[][] spaceShip;
    private DiscardReservedPileView discardReservedPile;
    private Map<Integer, ComponentView> mapDoubleCannons = new LinkedHashMap<>();
    private Map<Integer, ComponentView> mapDoubleEngines = new LinkedHashMap<>();
    private Map<Integer, ComponentView> mapCabins = new LinkedHashMap<>();
    private Map<Integer, ComponentView> mapShield = new LinkedHashMap<>();
    private Map<Integer, ComponentView> mapStorages = new LinkedHashMap<>();
    private Map<Integer, ComponentView> mapBatteries = new LinkedHashMap<>();
    private List<List<Pair<Integer, Integer>>> fragments;

    private final int converterRow = 5;
    private final int converterCol = 4;
    private float totalPower;

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
        discardReservedPile = new DiscardReservedPileView();
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

    public ComponentView removeComponent(int row, int col) {
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
        return component;
    }

    public LevelView getLevel() {
        return level;
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

    public DiscardReservedPileView getDiscardReservedPile() {
        return discardReservedPile;
    }

    public ComponentView[][] getSpaceShip() {
        return spaceShip;
    }

    public void setFragments(List<List<Pair<Integer, Integer>>> fragments) {
        this.fragments = fragments;
    }

    public List<List<Pair<Integer, Integer>>> getFragments() {
        return fragments;
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
            str.append("    ");
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
        str.append(space).append(number).append(space);

        return str.toString();
    }
}
