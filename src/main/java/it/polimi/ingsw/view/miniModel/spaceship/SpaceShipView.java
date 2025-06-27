package it.polimi.ingsw.view.miniModel.spaceship;

import it.polimi.ingsw.view.gui.controllers.ship.SpaceShipController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import org.javatuples.Pair;
import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.board.LevelView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpaceShipView implements Structure, MiniModelObservable {
    private final LevelView level;
    private ComponentView[][] spaceShip;
    private final DiscardReservedPileView discardReservedPile;

    private final Map<Integer, CannonView> mapDoubleCannons = new LinkedHashMap<>();
    private final Map<Integer, EngineView> mapDoubleEngines = new LinkedHashMap<>();
    private final Map<Integer, CabinView> mapCabins = new LinkedHashMap<>();
    private final Map<Integer, ShieldView> mapShield = new LinkedHashMap<>();
    private final Map<Integer, StorageView> mapStorages = new LinkedHashMap<>();
    private final Map<Integer, BatteryView> mapBatteries = new LinkedHashMap<>();
    private ComponentView last;
    private List<List<Pair<Integer, Integer>>> fragments;
    private Pair<Node, SpaceShipController> spaceShipNode;

    // Converter model spaceship to view -> row 6 -> 2, col 6 -> 3
    public final static int ROW_OFFSET = 4;
    public final static int COL_OFFSET = 3;
    private float totalPower;

    private final List<MiniModelObserver> observers;

    public SpaceShipView(LevelView level) {
        this.level = level;
        switch (level) {
            case LEARNING:
                spaceShip = new ComponentView[][] {
                        {null, null, null, new GenericComponentView(4, 6) , null, null, null},
                        {null, null, new GenericComponentView(5, 5) , new GenericComponentView(5, 6) , new GenericComponentView(5, 7) , null, null},
                        {null, new GenericComponentView(6, 4) , new GenericComponentView(6, 5) , new GenericComponentView(6, 6) , new GenericComponentView(6, 7) , new GenericComponentView(6, 8) , null},
                        {null, new GenericComponentView(7, 4) , new GenericComponentView(7, 5) , new GenericComponentView(7, 6) , new GenericComponentView(7, 7) , new GenericComponentView(7, 8) , null},
                        {null, new GenericComponentView(8, 4) , new GenericComponentView(8, 5) , null, new GenericComponentView(8, 7) , new GenericComponentView(8, 8) , null}
                };
                break;
            case SECOND:
                spaceShip = new ComponentView[][] {
                        {null, null, new GenericComponentView(4, 5) , null, new GenericComponentView(4,7) , null, null},
                        {null, new GenericComponentView(5, 4) , new GenericComponentView(5, 5) , new GenericComponentView(5, 6) , new GenericComponentView(5, 7) , new GenericComponentView(5, 8) , null},
                        {new GenericComponentView(6, 3) , new GenericComponentView(6, 4) , new GenericComponentView(6, 5) , new GenericComponentView(6, 6) , new GenericComponentView(6, 7) , new GenericComponentView(6, 8) , new GenericComponentView(6, 9) },
                        {new GenericComponentView(7, 3) , new GenericComponentView(7, 4) , new GenericComponentView(7, 5) , new GenericComponentView(7, 6) , new GenericComponentView(7, 7) , new GenericComponentView(7, 8) , new GenericComponentView(7, 9) },
                        {new GenericComponentView(8, 3) , new GenericComponentView(8, 4) , new GenericComponentView(8, 5) , null, new GenericComponentView(8, 7) , new GenericComponentView(8, 8) , new GenericComponentView(8, 9) }
                };
                break;
        }
        this.discardReservedPile = new DiscardReservedPileView();
        this.observers = new ArrayList<>();
    }

    public Pair<Node, SpaceShipController> getNode() {
        try {
            if (spaceShipNode != null) return spaceShipNode;

            String path;
            if (level == LevelView.SECOND) {
                path = "/fxml/ship/secondShip.fxml";
            } else {
                path = "/fxml/ship/learningShip.fxml";
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            SpaceShipController controller = loader.getController();
            controller.setModel(this);

            spaceShipNode = new Pair<>(root, controller);
            return spaceShipNode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        synchronized (observers) {
            for (MiniModelObserver observer : observers) {
                observer.react();
            }
        }
    }

    public void placeComponent(ComponentView component, int row, int col) {
        switch (component.getType()) {
            case DOUBLE_CANNON -> mapDoubleCannons.put(component.getID(), (CannonView) component);
            case DOUBLE_ENGINE -> mapDoubleEngines.put(component.getID(), (EngineView) component);
            case CABIN -> mapCabins.put(component.getID(), (CabinView) component);
            case SHIELD -> mapShield.put(component.getID(), (ShieldView) component);
            case STORAGE -> mapStorages.put(component.getID(), (StorageView) component);
            case BATTERY -> mapBatteries.put(component.getID(), (BatteryView) component);
            case GENERIC -> removeComponent(row, col);
        }

        spaceShip[row- ROW_OFFSET][col- COL_OFFSET] = component;
        spaceShip[row- ROW_OFFSET][col- COL_OFFSET].setCovered(false);

        spaceShip[row- ROW_OFFSET][col- COL_OFFSET].setRow(row);
        spaceShip[row- ROW_OFFSET][col- COL_OFFSET].setCol(col);

        last = component;

        notifyObservers();
    }

    public ComponentView removeLast() {
        return removeComponent(last.getRow() - 1, last.getCol() - 1);
    }

    public ComponentView peekLast() {
        return spaceShip[last.getRow() - 1 - ROW_OFFSET][last.getCol() - 1 - COL_OFFSET];
    }

    public ComponentView removeComponent(int row, int col) {
        ComponentView component = spaceShip[row- ROW_OFFSET][col- COL_OFFSET];
        switch (component.getType()) {
            case DOUBLE_CANNON -> mapDoubleCannons.remove(component.getID());
            case DOUBLE_ENGINE -> mapDoubleEngines.remove(component.getID());
            case CABIN -> mapCabins.remove(component.getID());
            case SHIELD -> mapShield.remove(component.getID());
            case STORAGE -> mapStorages.remove(component.getID());
            case BATTERY -> mapBatteries.remove(component.getID());
        }

        spaceShip[row- ROW_OFFSET][col- COL_OFFSET] = new GenericComponentView(component.getRow() - 1, component.getCol() - 1);
        notifyObservers();
        return component;
    }

    public LevelView getLevel() {
        return level;
    }

    public Map<Integer, CannonView> getMapDoubleCannons() {
        return mapDoubleCannons;
    }

    public Map<Integer, EngineView> getMapDoubleEngines() {
        return mapDoubleEngines;
    }

    public Map<Integer, CabinView> getMapCabins() {
        return mapCabins;
    }

    public Map<Integer, ShieldView> getMapShield() {
        return mapShield;
    }

    public Map<Integer, StorageView> getMapStorages() {
        return mapStorages;
    }

    public Map<Integer, BatteryView> getMapBatteries() {
        return mapBatteries;
    }

    public DiscardReservedPileView getDiscardReservedPile() {
        return discardReservedPile;
    }

    public void addDiscardReserved(ComponentView component) {
        discardReservedPile.addDiscardReserved(component);
        notifyObservers();
    }

    public ComponentView getComponent(int row, int col) {
        return spaceShip[row - ROW_OFFSET][col - COL_OFFSET];
    }

    public int getRows() {
        return spaceShip.length;
    }

    public int getCols() {
        return spaceShip[0].length;
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

    public SpaceShipView clone() {
        SpaceShipView copy = new SpaceShipView(this.getLevel());
        for (int i = 0; i < this.spaceShip.length; i++) {
            for (int j = 0; j < this.spaceShip[i].length; j++) {
                if (this.spaceShip[i][j] != null) {
                    copy.placeComponent(this.spaceShip[i][j].clone(), i + ROW_OFFSET, j + COL_OFFSET);
                }
            }
        }
        for (ComponentView component : this.discardReservedPile.getReserved()) {
            copy.addDiscardReserved(component.clone());
        }
        copy.getDiscardReservedPile().setIsDiscarded();
        return copy;
    }
}
