package Model.SpaceShip;

import java.util.ArrayList;
import java.util.List;

public class SpaceShip {
    private Component[][] components;
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

    public SpaceShip() {
        components = new Component[12][12];

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
                        singleCannonsStrength += (float)0.5;
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
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2.getComponentType() == ComponentType.STORAGE) {
                    // TODO get the value of the goods
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
     */
    public boolean useEnergy(int row, int column) {
        // TODO: if I have enough battery in the given slot remove one from the block and the total in the ship
        return true;
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
        surroundingComponents.add(0, components[row+1][column]);
        surroundingComponents.add(1, components[row][column-1]);
        surroundingComponents.add(2, components[row-1][column]);
        surroundingComponents.add(3, components[row][column+1]);
        return surroundingComponents;
    }

    /*
     @brief Reserve a component to be placed in the reservedComponents ArrayList
     @param c the component to reserve
     */

    // TODO: Should we rise and exception if there is already two reserved components?
    public void reserveComponent(Component c) {
        // TODO: handle the case where there is already two reserved components
        if (reservedComponents.get(0) == null) {
            reservedComponents.add(0, c);
        } else {
            reservedComponents.add(1, c);
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
    public void destroyComponent(int row, int column) {
        Component destroyedComponent = components[row][column];
        components[row][column] = null;

        switch (destroyedComponent.getComponentType()) {
            case SINGLE_ENGINE:
                singleEnginesStrength--;
                break;
            case DOUBLE_ENGINE:
                doubleEnginesStrength--;
                break;
            // TODO: handle the other types of components
        }

        lostComponents.add(destroyedComponent);

        // TODO: check if the component is no longer connected to the center of the ship

    }
}
