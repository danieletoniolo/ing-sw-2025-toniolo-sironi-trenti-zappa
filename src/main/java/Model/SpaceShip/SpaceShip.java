package Model.SpaceShip;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;

public class SpaceShip {
    private Component[][] components;
    private final boolean[][] validSpots;

    private List<Component> lostComponents;
    private ArrayList<Component> reservedComponents;

    private int singleEnginesStrength;
    private int doubleEnginesStrength;

    private float singleCannonsStrength;
    private int doubleCannonsStrength;
    private int doubleCannonsNumber;

    private int energyNumber;

    private int crewNumber;

    private boolean purpleAlien;

    private boolean brownAlien;

    private int goodsValue;
    private int exposedConnectors;

    public SpaceShip(boolean[][] validSpots) {
        components = new Component[12][12];
        this.validSpots = validSpots;
        lostComponents = new ArrayList<>();
        reservedComponents = new ArrayList<>();

        singleEnginesStrength = 0;
        doubleEnginesStrength = 0;
        singleCannonsStrength = 0;

        doubleCannonsStrength = 0;
        doubleCannonsNumber = 0;

        energyNumber = 0;

        crewNumber = 0;

        purpleAlien = false;
        brownAlien = false;

        goodsValue = 0;
        exposedConnectors = 0;
    }

    /*
     @brief Get the strength of the single engines
     @return singleEnginesStrength
     */
    public int getSingleEnginesStrength() {
        return singleEnginesStrength;
    }

    /*
     @brief Get the strength of the double engines
     @return doubleEnginesStrength
     */
    public int getDoubleEnginesStrength() {
        return doubleEnginesStrength;
    }

    /*
     @brief Refresh the strength stats of the engines by searching in the components matrix
     */
    public void refreshEngineStrength() {
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2.getComponentType() == ComponentType.SINGLE_ENGINE) {
                    singleEnginesStrength++;
                } else if (c2.getComponentType() == ComponentType.DOUBLE_ENGINE) {
                    doubleEnginesStrength++;
                }
            }
        }
    }

    /*
     @brief Get the strength of the single cannons
     @return singleCannonsStrength
     */
    public float getSingleCannonsStrength() {
        return singleCannonsStrength;
    }

    /*
     @brief Get the strength of the double cannons
     @return doubleCannonsStrength
     */
    public int getDoubleCannonsStrength() {
        return doubleCannonsStrength;
    }

    /*
     @brief Get the number of double cannons
     @return doubleCannonsNumber
     */
    public int getDoubleCannonsNumber() {
        return doubleCannonsNumber;
    }

    /*
     @brief Refresh the strength stats of the cannons by searching in the components matrix
     */
    public void refreshCannonsStrength() {
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2.getComponentType() == ComponentType.SINGLE_CANNON) {
                    if (c2.getClockwiseRotation() == 0) {
                        singleCannonsStrength++;
                    } else {
                        singleCannonsStrength += (float) 0.5;
                    }
                }
                if (c2.getComponentType() == ComponentType.DOUBLE_CANNON) {
                    if (c2.getClockwiseRotation() == 0) {
                        doubleCannonsStrength += 2;
                    } else {
                        doubleCannonsStrength += 1;
                    }
                    doubleCannonsNumber++;
                }
            }
        }
    }

    /*
     @brief Get the number of energy blocks
     @return energyNumber
     */
    public int getEnergyNumber() {
        return energyNumber;
    }

    /*
     @brief Get the goods value of the ship
     @return goodsValue
     */
    public int getGoodsValue() {
        return goodsValue;
    }

    /*
     @brief Get the value of the goods of the ship by searching in the components matrix
     */
    public void refreshGoodsValue() {
        goodsValue = 0;
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2.getComponentType() == ComponentType.STORAGE) {
                    Storage storage = (Storage) c2;
                    goodsValue += storage.getGoodsValue();
                }
            }
        }
    }

    /*
     @brief check if the ship can shield from a hit
     @param direction of the hit and Hit object
     @return -1 if the ship can't shield, 0 if the ship can shield spending a battery, 1 if the ship can shield without spending a battery
     */
    public int canProtect(int direction, Hit hit) {
        Component component = null;
        switch (hit.getDirection()) {
            case NORTH:
                for (int i = 0; i < 12 && component == null; i++) {
                    component = components[direction][i];
                }
                break;
            case WEST:
                for (int i = 0; i < 12 && component == null; i++) {
                    component = components[i][direction];
                }
                break;
            case SOUTH:
                for (int i = 11; i >= 0 && component == null; i--) {
                    component = components[direction][i];
                }
                break;
            case EAST:
                for (int i = 11; i >= 0 && component == null; i--) {
                    component = components[i][direction];
                }
                break;
            default:
                throw new IllegalArgumentException("The direction of the hit is not valid");
        }
        if (component == null) {
            return 1;
        }

        switch (hit.getType()) {
            case SMALLMETEOR:
                if (component.getExposedConnectors() == 0) {
                    return 1;
                }
            case LIGHTFIRE:
                for (int i = 0; i < 12; i++) {
                    for (int j = 0; j < 12; j++) {
                        if (components[i][j].getComponentType() == ComponentType.SHIELD) {
                            Shield shield = (Shield) components[i][j];
                            if (shield.canShield(hit.getDirection().getValue())) {
                                return 0;
                            }
                        }
                    }
                }
                return -1;
            case LARGEMETEOR:
                if (component.getComponentType() == ComponentType.SINGLE_CANNON &&
                        component.getClockwiseRotation() == 4 - hit.getDirection().getValue()) {
                    return 1;
                } else if (component.getComponentType() == ComponentType.DOUBLE_CANNON &&
                        component.getClockwiseRotation() == 4 - hit.getDirection().getValue()) {
                    return 0;
                } else {
                    return -1;
                }
            case HEAVYFIRE:
                return -1;
            default:
                throw new IllegalArgumentException("The type of the hit is not valid");
        }
    }

    /*
     @brief use a battery and reduce the total number of energy
     @param row and column of the battery cell component to use
     @return true if the battery was used, false otherwise
     @throws IllegalArgumentException if the component at the given row and column is not a battery
     */
    public boolean useEnergy(int row, int column) {
        if (components[row][column].getComponentType() == ComponentType.BATTERY) {
            Battery battery = (Battery) components[row][column];
            try {
                battery.removeEnergy();
                energyNumber--;
                return true;
            } catch (IllegalStateException e) {
                // TODO: decide if we want to throw an exception or return false
                return false;
            }
        } else {
            throw new IllegalArgumentException("The component at the given row and column is not a battery");
        }
    }

    /*
     @brief Check if the ship is valid by checking the validity of every component in the matrix
     @return The list of indexes of the invalid components
     */
    public List<int[]> getInvalidComponents() {
        List<int[]> invalidComponents = new ArrayList<>();
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2 != null && !c2.isValid(this)) {
                    invalidComponents.add(new int[]{c2.getRow(), c2.getColumn()});
                }
            }
        }
        return invalidComponents;
    }

    /*
     @brief Refresh the exposed connectors of the ship by searching in the components matrix
     */
    public void refreshExposedConnectors() {
        exposedConnectors = 0;
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2 != null) {
                    exposedConnectors += c2.getExposedConnectors();
                }
            }
        }
    }

    /*
     @brief Get the component at the given row and column
     @param row and column of the component
     @return component at the given row and column
     */
    public Component getComponent(int row, int column) {
        return components[row][column];
    }

    /*
     @brief Get the surrounding components of a given component
     @return ArrayList of surrounding components in the order North, West, South, East
     */
    public ArrayList<Component> getSurroundingComponents(int row, int column) {
        ArrayList<Component> surroundingComponents = new ArrayList<Component>();
        // North, West, South, East
        surroundingComponents.add(0, components[row + 1][column]);
        surroundingComponents.add(1, components[row][column - 1]);
        surroundingComponents.add(2, components[row - 1][column]);
        surroundingComponents.add(3, components[row][column + 1]);
        return surroundingComponents;
    }

    /*
     @brief Reserve a component to be placed in the reservedComponents ArrayList
     @param c the component to reserve
     @throws IllegalStateException if there are already two components reserved
     */
    public void reserveComponent(Component c) {
        if (reservedComponents.size() < 2) {
            if (reservedComponents.getFirst() == null) {
                reservedComponents.addFirst(c);
            } else {
                reservedComponents.add(1, c);
            }
        } else {
            throw new IllegalStateException("There are already two components reserved");
        }
    }

    /*
     @brief Unreserve a component in the reservedComponents ArrayList; should be called when the component is placed
     @param c the component to unreserve
     */
    public void unreserveComponent(Component c) {
        reservedComponents.remove(c);
    }

    /*
     @brief Destroy a component at the given row and column, update the stats of the ship and search if there
     is component that are no longer connected
     @return List of List of int[] representing the group of disconnected components
     */
    public List<List<int[]>> destroyComponent(int row, int column) {
        Component destroyedComponent = components[row][column];
        components[row][column] = null;

        switch (destroyedComponent.getComponentType()) {
            case SINGLE_ENGINE:
                singleEnginesStrength--;
                break;
            case DOUBLE_ENGINE:
                doubleEnginesStrength--;
                break;
            case SINGLE_CANNON:
                Cannon singlecannon = (Cannon) destroyedComponent;
                singleCannonsStrength -= singlecannon.getCannonStrength();
                break;
            case DOUBLE_CANNON:
                Cannon doublecannon = (Cannon) destroyedComponent;
                doubleCannonsStrength -= doublecannon.getCannonStrength();
                doubleCannonsNumber--;
                break;
            case CABIN:
                Cabin cabin = (Cabin) destroyedComponent;
                crewNumber -= cabin.getCrewNumber();
                break;
            case BROWN_LIFE_SUPPORT:
                for (Component c : getSurroundingComponents(row, column)) {
                    if (c != null && c.getComponentType() == ComponentType.CABIN) {
                        Cabin cabinBrown = (Cabin) c;
                        if (cabinBrown.hasBrownAlien()) {
                            brownAlien = false;
                            cabinBrown.removeAlien();
                            crewNumber -= cabinBrown.getCrewNumber();
                        }
                    }
                }
                break;
            case PURPLE_LIFE_SUPPORT:
                for (Component c : getSurroundingComponents(row, column)) {
                    if (c != null && c.getComponentType() == ComponentType.CABIN) {
                        Cabin cabinPurple = (Cabin) c;
                        if (cabinPurple.hasPurpleAlien()) {
                            purpleAlien = false;
                            cabinPurple.removeAlien();
                            crewNumber -= cabinPurple.getCrewNumber();
                        }
                    }
                }
                break;
            case STORAGE:
                Storage storage = (Storage) destroyedComponent;
                goodsValue -= storage.getGoodsValue();
                break;
            case BATTERY:
                Battery battery = (Battery) destroyedComponent;
                energyNumber -= battery.getEnergyNumber();
                break;
            default:
                break;
        }

        lostComponents.add(destroyedComponent);

        List<List<int[]>> disconnectedComponents = new ArrayList<>();
        boolean[][] visited = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                if (components[i][j] != null && !visited[i][j]) {
                    List<int[]> disconnectedComponent = new ArrayList<>();
                    Queue<int[]> queue = new LinkedList<>();
                    queue.add(new int[]{i, j});
                    visited[i][j] = true;

                    while (!queue.isEmpty()) {
                        int[] current = queue.poll();
                        int currentRow = current[0];
                        int currentColumn = current[1];
                        disconnectedComponent.add(current);

                        Component currentComponent = components[currentRow][currentColumn];

                        for (int face = 0; face < 4; face++) {
                            int newRow = currentRow + (face == 0 ? 1 : face == 2 ? -1 : 0);
                            int newColumn = currentColumn + (face == 1 ? -1 : face == 3 ? 1 : 0);

                            if (newRow >= 0 && newRow < 12 && newColumn >= 0 && newColumn < 12 && components[newRow][newColumn] != null && !visited[newRow][newColumn]) {
                                Component adjacentComponent = components[newRow][newColumn];
                                if ((currentComponent.getConnection(face) == ConnectorType.TRIPLE ||
                                        adjacentComponent.getConnection((face + 2) % 4) == ConnectorType.TRIPLE ||
                                        currentComponent.getConnection(face) == adjacentComponent.getConnection((face + 2) % 4)) &&
                                        currentComponent.getConnection(face) != ConnectorType.EMPTY) {
                                    visited[newRow][newColumn] = true;
                                    queue.add(new int[]{newRow, newColumn});
                                }
                            }
                        }
                    }
                    disconnectedComponents.add(disconnectedComponent);
                }
            }
        }
        // TODO: check witch group of disconnected components are still valid
        return disconnectedComponents;
    }
}