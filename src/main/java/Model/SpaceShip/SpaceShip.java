package Model.SpaceShip;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
    // TODO: merge with model-card and take as param Hit
    public int canShield(int direction) {
        // TODO
        return -1;
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
     @return true if the ship is valid, false otherwise
     */
    public boolean checkValidShip() {
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2 != null && !c2.isValid(this)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     @brief Refresh the exposed connectors of the ship by searching in the components matrix
     */
    public void refreshExposedConnectors() {
        exposedConnectors = 0;
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2 != null) {
                    exposedConnectors += c2.checkExposedConnectors();
                }
            }
        }
    }

    /*
     @brief Get the component at the given row and column
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

                        // Check North connection
                        if (currentRow + 1 < 12 && components[currentRow + 1][currentColumn] != null && !visited[currentRow + 1][currentColumn]) {
                            Component northComponent = components[currentRow + 1][currentColumn];
                            if ((currentComponent.getNorthConnection() == ConnectorType.TRIPLE ||
                                    northComponent.getSudConnection() == ConnectorType.TRIPLE ||
                                    currentComponent.getNorthConnection() == northComponent.getSudConnection()) &&
                                    currentComponent.getNorthConnection() != ConnectorType.EMPTY) {
                                visited[currentRow + 1][currentColumn] = true;
                                queue.add(new int[]{currentRow + 1, currentColumn});
                            }
                        }

                        // Check West connection
                        if (currentColumn - 1 >= 0 && components[currentRow][currentColumn - 1] != null && !visited[currentRow][currentColumn - 1]) {
                            Component westComponent = components[currentRow][currentColumn - 1];
                            if ((currentComponent.getWestConnection() == ConnectorType.TRIPLE ||
                                    westComponent.getEastConnection() == ConnectorType.TRIPLE ||
                                    currentComponent.getWestConnection() == westComponent.getEastConnection()) &&
                                    currentComponent.getWestConnection() != ConnectorType.EMPTY) {
                                visited[currentRow][currentColumn - 1] = true;
                                queue.add(new int[]{currentRow, currentColumn - 1});
                            }
                        }

                        // Check South connection
                        if (currentRow - 1 >= 0 && components[currentRow - 1][currentColumn] != null && !visited[currentRow - 1][currentColumn]) {
                            Component southComponent = components[currentRow - 1][currentColumn];
                            if ((currentComponent.getSudConnection() == ConnectorType.TRIPLE ||
                                    southComponent.getNorthConnection() == ConnectorType.TRIPLE ||
                                    currentComponent.getSudConnection() == southComponent.getNorthConnection()) &&
                                    currentComponent.getSudConnection() != ConnectorType.EMPTY) {
                                visited[currentRow - 1][currentColumn] = true;
                                queue.add(new int[]{currentRow - 1, currentColumn});
                            }
                        }

                        // Check East connection
                        if (currentColumn + 1 < 12 && components[currentRow][currentColumn + 1] != null && !visited[currentRow][currentColumn + 1]) {
                            Component eastComponent = components[currentRow][currentColumn + 1];
                            if ((currentComponent.getEastConnection() == ConnectorType.TRIPLE ||
                                    eastComponent.getWestConnection() == ConnectorType.TRIPLE ||
                                    currentComponent.getEastConnection() == eastComponent.getWestConnection()) &&
                                    currentComponent.getEastConnection() != ConnectorType.EMPTY) {
                                visited[currentRow][currentColumn + 1] = true;
                                queue.add(new int[]{currentRow, currentColumn + 1});
                            }
                        }

                    }
                    disconnectedComponents.add(disconnectedComponent);
                }
            }
        }
        return disconnectedComponents;
    }
}