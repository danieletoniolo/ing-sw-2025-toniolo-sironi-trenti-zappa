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

/**
 * Represents the view model of a spaceship in the game.
 * Manages the components, their positions, and the state of the spaceship for the GUI.
 * Implements Structure and MiniModelObservable for integration with the mini model and observer pattern.
 */
public class SpaceShipView implements Structure, MiniModelObservable {
    /** The level of the spaceship (e.g., LEARNING, SECOND). */
    private final LevelView level;

    /** 2D array representing the spaceship grid and its components. */
    private ComponentView[][] spaceShip;

    /** The pile for discarded or reserved components. */
    private final DiscardReservedPileView discardReservedPile;

    /** Map of double cannons by their ID. */
    private final Map<Integer, CannonView> mapDoubleCannons = new LinkedHashMap<>();
    /** Map of double engines by their ID. */
    private final Map<Integer, EngineView> mapDoubleEngines = new LinkedHashMap<>();
    /** Map of cabins by their ID. */
    private final Map<Integer, CabinView> mapCabins = new LinkedHashMap<>();
    /** Map of shields by their ID. */
    private final Map<Integer, ShieldView> mapShield = new LinkedHashMap<>();
    /** Map of storages by their ID. */
    private final Map<Integer, StorageView> mapStorages = new LinkedHashMap<>();
    /** Map of batteries by their ID. */
    private final Map<Integer, BatteryView> mapBatteries = new LinkedHashMap<>();

    /** The last component placed on the spaceship. */
    private ComponentView last;

    /** List of fragments representing disconnected parts of the spaceship. */
    private List<List<Pair<Integer, Integer>>> fragments;

    /** Pair containing the JavaFX node and its controller for the spaceship view. */
    private Pair<Node, SpaceShipController> spaceShipNode;

    // Converter model spaceship to view -> row 6 -> 2, col 6 -> 3
    /** Offset used to convert model spaceship row indices to view indices. */
    public final static int ROW_OFFSET = 4;
    /** Offset used to convert model spaceship column indices to view indices. */
    public final static int COL_OFFSET = 3;
    /** Total power of the spaceship, used for calculations in the view. */
    private float totalPower;

    /** List of observers registered to receive updates from this view model. */
    private final List<MiniModelObserver> observers;

    /**
     * Constructs a SpaceShipView for the specified level.
     * Initializes the spaceship grid and supporting structures based on the level.
     *
     * @param level the level of the spaceship (e.g., LEARNING, SECOND)
     */
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

    /**
     * Returns the JavaFX node and its controller for the spaceship view.
     * Loads the appropriate FXML file based on the spaceship level.
     * Caches the result for future calls.
     *
     * @return a Pair containing the JavaFX Node and its SpaceShipController, or null if loading fails
     */
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

    /**
     * Registers an observer to receive updates from this view model.
     *
     * @param observer the observer to register
     */
    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer so it no longer receives updates.
     *
     * @param observer the observer to unregister
     */
    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Notifies all registered observers of a change in the view model.
     */
    @Override
    public void notifyObservers() {
        synchronized (observers) {
            for (MiniModelObserver observer : observers) {
                observer.react();
            }
        }
    }

    /**
     * Places a component on the spaceship at the specified row and column.
     * Updates the relevant component map and notifies observers.
     *
     * @param component the component to place
     * @param row the row index in the model
     * @param col the column index in the model
     */
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

    /**
     * Removes the last placed component from the spaceship.
     * The last component is determined by the {@code last} field.
     *
     * @return the removed {@link ComponentView}
     */
    public ComponentView removeLast() {
        return removeComponent(last.getRow() - 1, last.getCol() - 1);
    }

    /**
     * Returns the last placed component on the spaceship without removing it.
     *
     * @return the last {@link ComponentView} placed
     */
    public ComponentView peekLast() {
        return spaceShip[last.getRow() - 1 - ROW_OFFSET][last.getCol() - 1 - COL_OFFSET];
    }

    /**
     * Removes a component from the spaceship at the specified row and column.
     * Updates the relevant component map and replaces the removed component with a generic component.
     * Notifies observers after removal.
     *
     * @param row the row index in the model
     * @param col the column index in the model
     * @return the removed {@link ComponentView}
     */
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

    /**
     * Returns the level of the spaceship.
     *
     * @return the {@link LevelView} of the spaceship
     */
    public LevelView getLevel() {
        return level;
    }

    /**
     * Returns the map of double cannons by their ID.
     *
     * @return a map of double cannons
     */
    public Map<Integer, CannonView> getMapDoubleCannons() {
        return mapDoubleCannons;
    }

    /**
     * Returns the map of double engines by their ID.
     *
     * @return a map of double engines
     */
    public Map<Integer, EngineView> getMapDoubleEngines() {
        return mapDoubleEngines;
    }

    /**
     * Returns the map of cabins by their ID.
     *
     * @return a map of cabins
     */
    public Map<Integer, CabinView> getMapCabins() {
        return mapCabins;
    }

    /**
     * Returns the map of shields by their ID.
     *
     * @return a map of shields
     */
    public Map<Integer, ShieldView> getMapShield() {
        return mapShield;
    }

    /**
     * Returns the map of storages by their ID.
     *
     * @return a map of storages
     */
    public Map<Integer, StorageView> getMapStorages() {
        return mapStorages;
    }

    /**
     * Returns the map of batteries by their ID.
     *
     * @return a map of batteries
     */
    public Map<Integer, BatteryView> getMapBatteries() {
        return mapBatteries;
    }

    /**
     * Returns the discard/reserved pile view.
     *
     * @return the {@link DiscardReservedPileView}
     */
    public DiscardReservedPileView getDiscardReservedPile() {
        return discardReservedPile;
    }

    /**
     * Adds a component to the discard/reserved pile and notifies observers.
     *
     * @param component the component to add
     */
    public void addDiscardReserved(ComponentView component) {
        discardReservedPile.addDiscardReserved(component);
        notifyObservers();
    }

    /**
     * Returns the component at the specified row and column.
     *
     * @param row the row index in the model
     * @param col the column index in the model
     * @return the {@link ComponentView} at the specified position
     */
    public ComponentView getComponent(int row, int col) {
        return spaceShip[row - ROW_OFFSET][col - COL_OFFSET];
    }

    /**
     * Returns the number of rows in the spaceship grid.
     *
     * @return the number of rows
     */
    public int getRows() {
        return spaceShip.length;
    }

    /**
     * Returns the number of columns in the spaceship grid.
     *
     * @return the number of columns
     */
    public int getCols() {
        return spaceShip[0].length;
    }

    /**
     * Returns the 2D array representing the spaceship grid and its components.
     *
     * @return the spaceship grid
     */
    public ComponentView[][] getSpaceShip() {
        return spaceShip;
    }

    /**
     * Sets the list of fragments representing disconnected parts of the spaceship.
     *
     * @param fragments the list of fragments to set
     */
    public void setFragments(List<List<Pair<Integer, Integer>>> fragments) {
        this.fragments = fragments;
    }

    /**
     * Returns the list of fragments representing disconnected parts of the spaceship.
     *
     * @return the list of fragments
     */
    public List<List<Pair<Integer, Integer>>> getFragments() {
        return fragments;
    }

    /**
     * Returns the number of rows to draw in the TUI representation of the spaceship.
     *
     * @return the number of rows to draw
     */
    public int getRowsToDraw() {
        return 5 * ComponentView.getRowsToDraw() + 2;
    }

    /**
     * Draws a specific line of the spaceship for the TUI (text user interface).
     *
     * @param line the line number to draw
     * @return the string representation of the line
     */
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

    /**
     * Creates and returns a deep copy of this SpaceShipView.
     * The clone includes all components and the discard/reserved pile.
     *
     * @return a deep copy of this SpaceShipView
     */
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
