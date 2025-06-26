package it.polimi.ingsw.model.spaceship;

import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.player.PlayerColor;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.*;

public class SpaceShip {
    private final Level level;

    private static final float alienStrength = 2.0f;
    private static final int rows = 12;
    private static final int cols = 12;
    private final Component[][] components;
    private int numberOfComponents;
    private final boolean[][] validSpots;

    private final List<Component> lostComponents;
    private final ArrayList<Component> reservedComponents;

    private final Map<Integer, Storage> storages;
    private final Map<Integer, Battery> batteries;
    private final Map<Integer, Cabin> cabins;
    private final Map<Integer, Cannon> cannons;
    private final Map<Integer, Engine> engines;
    private final PriorityQueue<Good> goods;
    private Component lastPlacedComponent;

    private int singleEnginesStrength;
    private int doubleEnginesStrength;

    private float singleCannonsStrength;
    private float doubleCannonsStrength;
    private int doubleCannonsNumber;

    private int energyNumber;

    private int crewNumber;

    private boolean purpleAlien;

    private boolean brownAlien;

    private int goodsValue;

    /**
     * Comparator for sorting goods by value in descending order.
     * This comparator is used to maintain the priority queue of goods in the storage.
     * <p>
     * We use a static inner class to avoid serialization issues with the comparator.
     */
    private static class ValueDescendingComparator implements Comparator<Good>, Serializable {
        // TODO: We might need the serialVersionUID
        @Override
        public int compare(Good g1, Good g2) {
            return Integer.compare(g2.getValue(), g1.getValue());
        }
    }

    public SpaceShip(Level level, PlayerColor color) throws IllegalArgumentException{
        this.level = level;

        // Init valid spots of the spaceship
        switch (level){
            case LEARNING:
                validSpots = new boolean[][]{
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, true , false, false, false, false, false},
                        {false, false, false, false, false, true , true , true , false, false, false, false},
                        {false, false, false, false, true , true , true , true , true , false, false, false},
                        {false, false, false, false, true , true , true , true , true , false, false, false},
                        {false, false, false, false, true , true , false, true , true , false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false}
                };
                break;
            case SECOND:
                validSpots = new boolean[][]{
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, true , false, true , false, false, false, false},
                        {false, false, false, false, true , true , true , true , true , false, false, false},
                        {false, false, false, true , true , true , true , true , true , true , false, false},
                        {false, false, false, true , true , true , true , true , true , true , false, false},
                        {false, false, false, true , true , true , false, true , true , true , false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false},
                        {false, false, false, false, false, false, false, false, false, false, false, false}
                };
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + level);
        }

        lostComponents = new ArrayList<>();
        reservedComponents = new ArrayList<>();

        // Init stats
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

        storages = new HashMap<>();
        batteries = new HashMap<>();
        cabins = new HashMap<>();
        cannons = new HashMap<>();
        engines = new HashMap<>();
        goods = new PriorityQueue<>(new ValueDescendingComparator());
        lastPlacedComponent = null;

        int pos = 6;
        components = new Component[rows][cols];
        components[pos][pos] = TilesManager.getMainCabin(color);
        components[pos][pos].ship = this;
        components[pos][pos].setRow(pos);
        components[pos][pos].setColumn(pos);
        cabins.put(components[pos][pos].getID(), (Cabin) components[pos][pos]);
        numberOfComponents = 1;
    }

    /**
     * Get the number of components placed in the ship
     * @return number of components placed in the ship
     */
    public int getNumberOfComponents() {
        return numberOfComponents;
    }

    /**
     Get the strength of the single engines
     @return The strength of the single engines
     */
    public int getSingleEnginesStrength() {
        return singleEnginesStrength;
    }

    /**
     * Get the strength of the double engines
     * @return The strength of the double engines
     */
    public int getDoubleEnginesStrength() {
        return doubleEnginesStrength;
    }

    /**
     * Get the row of the ship matrix
     * @return number of rows
     */
    public static int getRows(){
        return rows;
    }

    /**
     * Get the column of the ship matrix
     * @return number of cols
     */
    public static int getCols(){
        return cols;
    }

    /**
     * Get the strength of the single cannons of the ship
     * @return Strength of the single cannons of the ship
     */
    public float getSingleCannonsStrength() {
        return singleCannonsStrength;
    }

    /**
     * Get the strength of the double cannons of the ship
     * @return Strength of the double cannons of the ship
     */
    public float getDoubleCannonsStrength() {
        return doubleCannonsStrength;
    }

    /**
     * Calculates and returns the total strength of cannons based on their IDs.
     *
     * @param IDs a list of integer IDs corresponding to the cannons whose strength is to be calculated
     * @return the total strength of the cannons as a float
     */
    public float getCannonsStrength(List<Integer> IDs) throws IllegalArgumentException {
        float strength = 0;
        Cannon cannon;
        for (int ID : IDs) {
            cannon = cannons.get(ID);
            if (cannon != null) {
                strength += cannon.getCannonStrength();
            } else {
                throw new IllegalArgumentException("Cannon with ID " + ID + " not found in the ship.");
            }
        }
        return strength;
    }

    /**
     * Calculates and returns the total strength of engines based on their IDs.
     * @param IDs a list of integer IDs corresponding to the engines whose strength is to be calculated
     * @return the total strength of the engines as a float
     */
    public float getEnginesStrength(List<Integer> IDs) throws IllegalArgumentException {
        float strength = 0;
        Engine engine;
        for (int ID : IDs) {
            engine = engines.get(ID);
            if (engine != null) {
                strength += engine.getEngineStrength();
            } else {
                throw new IllegalArgumentException("Engine with ID " + ID + " not found in the ship.");
            }
        }
        return strength;
    }

    /**
     * Get the number of double cannons of the ship
     * @return Number of double cannons of the ship
     */
    public int getDoubleCannonsNumber() {
        return doubleCannonsNumber;
    }

    /**
     * Get the number of energy blocks
     * @return number of energy blocks available in the ship
     */
    public int getEnergyNumber() {
        return energyNumber;
    }

    /**
     * Get the goods value of the ship
     * @return goodsValue
     */
    public int getGoodsValue() {
        return goodsValue;
    }

    /**
     * Get the number of crew members of the ship
     * @return the number of crew members of the ship
     */
    public int getCrewNumber() {
        return crewNumber;
    }

    /**
     * Get the number of human crew members in the ship
     * @return the number of human crew members in the ship
     */
    public int getHumanCrewNumber() {
        return crewNumber - (brownAlien ? 1 : 0) - (purpleAlien ? 1 : 0);
    }

    /**
     * Get if there is a purple alien in the ship
     * @return true if there is a purple alien in the ship, false otherwise
     */
    public boolean hasPurpleAlien(){
        return purpleAlien;
    }

    /**
     * Get if there is a brown alien in the ship
     * @return true if there is a brown alien in the ship, false otherwise
     */
    public boolean hasBrownAlien(){
        return brownAlien;
    }

    /**
     * Get how much the aliens add to the stats
     * @return return how much the aliens add to the stats
     */
    public float getAlienStrength(boolean forEngine) {
        if (forEngine) {
            return brownAlien && !engines.isEmpty() ? alienStrength : 0;
        } else {
            return purpleAlien && !cannons.isEmpty() ? alienStrength : 0;
        }
    }

    /**
     * Get the default strength of cannons of the ship
     * @return the default strength of cannons of the ship
     */
    public float getDefaultCannonsStrength() {
        return getSingleCannonsStrength() + getAlienStrength(false);
    }

    /**
     * Get the default strength of engines of the ship
     * @return the default strength of engines of the ship
     */
    public int getDefaultEnginesStrength() {
        return getSingleEnginesStrength() + (int) getAlienStrength(true);
    }

    /**
     * Get the maximum potential of cannons and engines of the ship
     * @return the maximum potential of cannons and engines of the ship
     */
    public float getMaxCannonsStrength() {
        return getSingleCannonsStrength() + getDoubleCannonsStrength() + getAlienStrength(false);
    }

    /**
     * Get the maximum potential of engines of the ship
     * @return the maximum potential of engines of the ship
     */
    public int getMaxEnginesStrength() {
        return getSingleEnginesStrength() + getDoubleEnginesStrength() + (int) getAlienStrength(true);
    }

    /**
     * Get the components of the ship
     * @return components of the ship
     * @apiNote Should only be used for testing
     * @implNote It passes a reference to the components list not a copy
     */
    public ArrayList<Component> getReservedComponents() {
        return reservedComponents;
    }

    /**
     * Get the components of the ship
     * @return components of the ship
     * @apiNote Should only be used for testing
     * @implNote It passes a reference to the components list not a copy
     */
    public List<Component> getLostComponents() {
        return lostComponents;
    }

    /**
     * Get the goods of the ship ordered by value
     * @return PriorityQueue of goods of the ship (The max value is at the top)
     */
    public PriorityQueue<Good> getGoods() {
        return new PriorityQueue<>(goods);
    }

    /**
     * Add goods to the ship and the storage
     * @param goodsToAdd goods to add to the storage
     * @param goodsToRemove goods to remove from the storage
     * @param storageID storage ID where to exchange the goods
     * @throws IllegalArgumentException if the storage with the given ID does not exist
     * @throws IllegalStateException if the storage is full or if the goods to remove are not in the storage
     * @apiNote The conditions that the goods to remove are in the storage and that we are not adding more than
     * the maximum capacity of the storage are checked in the Storage class. We assume that the goods to add had been
     * correctly checked before calling this method.
     */
    public void exchangeGood(List<Good> goodsToAdd, List<Good> goodsToRemove, int storageID) {
        Storage storage = storages.get(storageID);
        if (storage == null) {
            throw new IllegalArgumentException("The storage with the given ID does not exist");
        }
        // Remove the goods to leave from the storage and update the ships stats
        if (goodsToRemove != null) {
            for (Good good : goodsToRemove) {
                storage.removeGood(good);
                goods.remove(good);
                goodsValue -= good.getValue();
            }
        }
        // Add the goods to get to the storage and update the ships stats
        if (goodsToAdd != null) {
            for (Good good : goodsToAdd) {
                storage.addGood(good);
                goods.add(good);
                goodsValue += good.getValue();
            }
        }
    }

    /**
     * Poll the most valuable good of a storage and update the ship stats.
     * @param storageID ID of the storage to poll the good from
     * @return The most valuable good of the storage or null if the storage is empty.
     * @see Storage#pollGood()
     */
    public Good pollGood(int storageID) {
        Storage storage = storages.get(storageID);
        if (storage == null) {
            throw new IllegalArgumentException("The storage with the given ID does not exist");
        }
        goodsValue -= storage.peekGood().getValue();
        goods.remove(storage.peekGood());
        return storage.pollGood();
    }

    /**
     * Add crew members to the ship at the given cabin
     * @param cabinID ID of the cabin to add the crew members
     * @param brownAlien true if the crew member to add is a brown alien, false otherwise
     * @param purpleAlien true if the crew member to add is a purple alien, false otherwise
     * @throws IllegalArgumentException if the brownAlien and purpleAlien are both true or if the cabin with the given ID does not exist
     * @throws IllegalStateException if the cabin is full
     */
    public void addCrewMember(int cabinID, boolean brownAlien, boolean purpleAlien) throws IllegalArgumentException, IllegalStateException{
        if (brownAlien && purpleAlien) {
            throw new IllegalArgumentException("Cannot add both brown and purple alien to the cabin");
        }

        Cabin cabin = cabins.get(cabinID);
        if (cabin == null) {
            throw new IllegalArgumentException("The cabin with the given ID does not exist");
        }

        if (brownAlien) {
            if (hasBrownAlien()) {
                throw new IllegalStateException("The ship already has a brown alien");
            }
            cabin.addBrownAlien();
            this.brownAlien = true;
        } else if (purpleAlien) {
            if (hasPurpleAlien()) {
                throw new IllegalStateException("The ship already has a purple alien");
            }
            cabin.addPurpleAlien();
            this.purpleAlien = true;
        } else {
            cabin.addCrewMember();
        }
        this.crewNumber += (brownAlien || purpleAlien) ? 1 : 2;
    }

    /**
     * Remove crew members from the ship at the given cabin
     * @param cabinID ID of the cabin to remove the crew members from
     * @param num number of crew members to remove
     * @throws IllegalArgumentException if the cabin with the given ID does not exist
     * @throws IllegalStateException if the cabin does not have enough crew members to remove
     */
    public void removeCrewMember(int cabinID, int num) throws IllegalArgumentException, IllegalStateException {
        Cabin cabin = cabins.get(cabinID);
        if (cabin == null) {
            throw new IllegalArgumentException("The cabin with the given ID does not exist");
        }
        if (cabin.hasBrownAlien()) {
            brownAlien = false;
        } else if (cabin.hasPurpleAlien()) {
            purpleAlien = false;
        }
        cabin.removeCrewMember(num);
        crewNumber -= num;
    }

    /**
     * Check if the ship can shield from a hit
     * @param dice direction of the hit (Number picked by dice roll)
     * @param hit hit class containing the type of the hit and the direction (North, West, South, East) of the hit and Hit object
     * @return Pair of the component that can shield and the value of the shield. -1 if the ship can't shield, 0 if the ship can shield spending a battery, 1 if the ship can shield without spending a battery (in this case the component return is null)
     * @throws IllegalArgumentException if the direction or the type of the hit is not valid
     */
    public Pair<Component, Integer> canProtect(int dice, Hit hit) throws IllegalArgumentException {
        Component component = null;
        switch (hit.getDirection()) {
            case NORTH:
                for (int i = 0; i < rows && component == null; i++) {
                    component = components[i][dice];
                }
                break;
            case WEST:
                for (int i = 0; i < cols && component == null; i++) {
                    component = components[dice][i];
                }
                break;
            case SOUTH:
                for (int i = rows-1; i >= 0 && component == null; i--) {
                    component = components[i][dice];
                }
                break;
            case EAST:
                for (int i = cols-1; i >= 0 && component == null; i--) {
                    component = components[dice][i];
                }
                break;
            default:
                throw new IllegalArgumentException("The direction of the hit is not valid");
        }
        if (component == null) {
            return new Pair<>(null, 1);
        }

        int direction = hit.getDirection().getValue();
        switch (hit.getType()) {
            case SMALLMETEOR:
                // Check if the component has a smooth side (EMPTY) where the hit is coming from
                if (component.getConnection(direction) == ConnectorType.EMPTY) {
                    return new Pair<>(null, 1);
                }
            case LIGHTFIRE:
                for (int i = 0; i < 12; i++) {
                    for (int j = 0; j < 12; j++) {
                        if (components[i][j] != null && components[i][j].getComponentType() == ComponentType.SHIELD) {
                            Shield shield = (Shield) components[i][j];
                            if (shield.canShield(hit.getDirection().getValue())) {
                                return new Pair<>(component, 0);
                            }
                        }
                    }
                }
                return new Pair<>(component, -1);
            case LARGEMETEOR:
                int targetRotation = direction % 2 == 0 ? direction : (direction + 2) % 4;
                if (direction != 0 && level == Level.SECOND) {
                    for (int k = 0; k < 12; k++) {
                        Component componentAbove = direction % 2 != 0 ? components[dice - 1][k] : components[k][dice - 1];
                        Component componentBelow = direction % 2 != 0 ? components[dice + 1][k] : components[k][dice + 1];

                        if (componentAbove != null) {
                            if (componentAbove.getComponentType() == ComponentType.SINGLE_CANNON && componentAbove.getClockwiseRotation() == targetRotation) {
                                return new Pair<>(null, 1);
                            } else if (componentAbove.getComponentType() == ComponentType.DOUBLE_CANNON && componentAbove.getClockwiseRotation() == targetRotation) {
                                return new Pair<>(componentAbove, 0);
                            }
                        }

                        if (componentBelow != null) {
                            if (componentBelow.getComponentType() == ComponentType.SINGLE_CANNON && componentBelow.getClockwiseRotation() == targetRotation) {
                                return new Pair<>(null, 1);
                            } else if (componentBelow.getComponentType() == ComponentType.DOUBLE_CANNON && componentBelow.getClockwiseRotation() == targetRotation) {
                                return new Pair<>(componentBelow, 0);
                            }
                        }
                    }
                }

                for (int k = 0; k < 12; k++) {
                    Component centerComponent = direction % 2 != 0 ? components[dice][k] : components[k][dice];

                    if (centerComponent != null) {
                        if (centerComponent.getComponentType() == ComponentType.SINGLE_CANNON && centerComponent.getClockwiseRotation() == targetRotation) {
                            return new Pair<>(null, 1);
                        } else if (centerComponent.getComponentType() == ComponentType.DOUBLE_CANNON && centerComponent.getClockwiseRotation() == targetRotation) {
                            return new Pair<>(centerComponent, 0);
                        }
                    }
                }

                return new Pair<>(component, -1);
            case HEAVYFIRE:
                return new Pair<>(component, -1);
            default:
                throw new IllegalArgumentException("The type of the hit is not valid");
        }
    }

    /**
     * Use a battery and reduce the total number of energy
     * @param ID ID of the battery to use
     * @return true if the battery was used, false otherwise
     * @throws IllegalArgumentException if the component at the given row and column is not a battery
     */
    public boolean useEnergy(int ID) throws IllegalArgumentException {
        Battery battery = batteries.get(ID);
        if (battery == null) {
            throw new IllegalArgumentException("The battery with the given ID does not exist");
        }
        try {
            battery.removeEnergy();
            energyNumber--;
            return true;
        } catch (IllegalStateException e) {
            // TODO: decide if we want to throw an exception or return false
            return false;
        }
    }

    /**
     * Check if the ship is valid by checking the validity of every component in the matrix
     * @return The list of indexes (Pair of Integer) of the invalid components of the ship
     */
    public ArrayList<Pair<Integer, Integer>> getInvalidComponents() {
        ArrayList<Pair<Integer, Integer>> invalidComponents = new ArrayList<>();
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2 != null && !c2.isValid()) {
                    invalidComponents.add(new Pair<>(c2.getRow(), c2.getColumn()));
                }
            }
        }
        return invalidComponents;
    }

    /**
     * Get the total number of exposed connectors in the ship
     * @implNote The method iterates over all the components in the ship and calls the getExposedConnectors method of each component
     * @return The number of the exposed connectors
     */
    public int getExposedConnectors() {
        int exposedConnectors = 0;
        for (Component[] c1 : components) {
            for (Component c2 : c1) {
                if (c2 != null) {
                    exposedConnectors += c2.getExposedConnectors();
                }
            }
        }
        return exposedConnectors;
    }

    /**
     * Get the component at the given row and column
     * @param row row of the component to get
     * @param column column of the component to get
     * @return component at the given row and column
     * @throws IllegalArgumentException if the row and column are not valid
     */
    public Component getComponent(int row, int column) {
        if (row < 0 || row >= rows || column < 0 || column >= cols) {
            throw new IllegalArgumentException("The row and column are not valid");
        }
        return components[row][column];
    }

    /**
     * Get the surrounding components of a given component
     * @param row row of the component to get the surrounding components
     * @param column column of the component to get the surrounding components
     * @return ArrayList of surrounding components in the order North, West, South, East
     */
    public ArrayList<Component> getSurroundingComponents(int row, int column) {
        ArrayList<Component> surroundingComponents = new ArrayList<>();
        // North, West, South, East
        surroundingComponents.add(0, components[row - 1][column]);
        surroundingComponents.add(1, components[row][column - 1]);
        surroundingComponents.add(2, components[row + 1][column]);
        surroundingComponents.add(3, components[row][column + 1]);
        return surroundingComponents;
    }

    /**
     * Reserve a component to be placed in the reservedComponents ArrayList
     * @param c the component to reserve
     * @throws IllegalStateException if there are already two components reserved
     */
    public void putReserveComponent(Component c) throws IllegalStateException {
        if (reservedComponents.size() < 2) {
            if (reservedComponents.isEmpty() || reservedComponents.getFirst() == null) {
                reservedComponents.addFirst(c);
            } else {
                reservedComponents.add(1, c);
            }
        } else {
            throw new IllegalStateException("There are already two components reserved");
        }
    }

    /**
     * Unreserve a component in the reservedComponents ArrayList
     * @param tileID The ID of component to unreserve
     */
    public void removeReserveComponent(int tileID) {
        reservedComponents.removeIf(c -> c.getID() == tileID);
    }

    public Component peekReservedComponent(int tileID) {
        for (Component c : reservedComponents) {
            if (c.getID() == tileID) {
                return c;
            }
        }
        return null;
    }

    /**
     * Place a component at the given row and column to the ship
     * @param c the component to place
     * @param row the row of the component to place
     * @param column the column of the component to place
     * @throws IllegalStateException if the component is not connected
     */
    public void placeComponent(Component c, int row, int column) throws IllegalStateException {
        if (!validSpots[row][column]) {
            throw new IllegalStateException("The component cannot be placed in the given row and column");
        }
        if (components[row][column] != null) {
            throw new IllegalStateException("The component is already placed in the given row and column");
        }
        components[row][column] = c;
        components[row][column].ship = this;
        if (components[row][column].isConnected(row, column)) {
            reservedComponents.remove(c);
            components[row][column].setRow(row);
            components[row][column].setColumn(column);
            switch (components[row][column].getComponentType()) {
                case BATTERY:
                    energyNumber += ((Battery) components[row][column]).getEnergyNumber();
                    batteries.put(c.getID(), (Battery) components[row][column]);
                    break;
                case CABIN:
                    cabins.put(c.getID(), (Cabin) components[row][column]);
                    break;
                case STORAGE:
                    storages.put(c.getID(), (Storage) components[row][column]);
                    break;
                case SINGLE_CANNON:
                    singleCannonsStrength += ((Cannon) components[row][column]).getCannonStrength();
                    cannons.put(c.getID(), (Cannon) components[row][column]);
                    break;
                case DOUBLE_CANNON:
                    doubleCannonsStrength += ((Cannon) components[row][column]).getCannonStrength();
                    cannons.put(c.getID(), (Cannon) components[row][column]);
                    doubleCannonsNumber++;
                    break;
                case SINGLE_ENGINE:
                    singleEnginesStrength++;
                    engines.put(c.getID(), (Engine) components[row][column]);
                    break;
                case DOUBLE_ENGINE:
                    doubleEnginesStrength+=2;
                    engines.put(c.getID(), (Engine) components[row][column]);
                    break;
                default:
                    break;
            }
            // Mark the component as the lasted placed component
            lastPlacedComponent = components[row][column];
            // Increase the number of placed components
            numberOfComponents++;
        } else {
            components[row][column].ship = null;
            throw new IllegalStateException("The component at the given row and column are not connected");
        }
    }

    /**
     * Get the cabin in the ship by ID
     * @return cabin in the ship with the given ID
     * @throws IllegalArgumentException if there is no cabin with the given ID
     */
    public Cabin getCabin(int ID) throws IllegalArgumentException {
        if (cabins.get(ID) == null) {
            throw new IllegalArgumentException("The ID of the cabin is not valid");
        }
        return this.cabins.get(ID);
    }

    /**
     * Get the cabins in the ship
     * @return cabins in the ship
     */
    public List<Cabin> getCabins() {
        return new ArrayList<>(cabins.values());
    }

    /**
     * Get the storage in the ship by ID
     * @return storage in the ship with the given ID
     * @throws IllegalArgumentException if there is no storage with the given ID
     */
    public Storage getStorage(int ID) throws IllegalArgumentException {
        if (storages.get(ID) == null) {
            throw new IllegalArgumentException("The ID of the storage is not valid");
        }
        return this.storages.get(ID);
    }

    /**
     * Get the battery in the ship by ID
     * @return battery in the ship with the given ID
     * @throws IllegalArgumentException if there is no battery with the given ID
     */
    public Battery getBattery(int ID) throws IllegalArgumentException {
        if (batteries.get(ID) == null) {
            throw new IllegalArgumentException("The ID of the battery is not valid");
        }
        return this.batteries.get(ID);
    }

    /**
     * Get the cannon in the ship by ID
     * @return cannon in the ship with the given ID
     * @throws IllegalArgumentException if there is no cannon with the given ID
     */
    public Cannon getCannon(int ID) throws IllegalArgumentException {
        if (cannons.get(ID) == null) {
            throw new IllegalArgumentException("The ID of the cannon is not valid");
        }
        return this.cannons.get(ID);
    }

    /**
     * Get the engine in the ship by ID
     * @param ID ID of the engine to get
     * @return engine in the ship with the given ID
     * @throws IllegalArgumentException if there is no engine with the given ID
     */
    public Engine getEngine(int ID) throws IllegalArgumentException {
        if (engines.get(ID) == null) {
            throw new IllegalArgumentException("The ID of the engine is not valid");
        }
        return this.engines.get(ID);
    }

    /**
     * Get the last placed component
     * @return last placed component
     */
    public Component getLastPlacedComponent() {
        if (lastPlacedComponent == null) {
            throw new IllegalStateException("The last placed component is fixed");
        }
        return lastPlacedComponent;
    }

    /**
     * Clean the last placed component
     * @implNote This method is used to reset the last placed component after it has been fixed.
     */
    public void cleanLastPlacedComponent() {
        lastPlacedComponent = null;
    }

    /**
     * Destroy a component at the given row and column and update the stats of the ship
     * @param row row of the component to destroy
     * @param column column of the component to destroy
     */
    public void destroyComponent(int row, int column) throws IllegalArgumentException {
        Component destroyedComponent = components[row][column];
        if (destroyedComponent == null) {
            throw new IllegalArgumentException("The component at [" + row + "; " + column + "]" + " is null");
        }
        components[row][column] = null;

        switch (destroyedComponent.getComponentType()) {
            case SINGLE_ENGINE:
                singleEnginesStrength--;
                engines.remove(destroyedComponent.getID());
                break;
            case DOUBLE_ENGINE:
                doubleEnginesStrength-=2;
                engines.remove(destroyedComponent.getID());
                break;
            case SINGLE_CANNON:
                Cannon singlecannon = (Cannon) destroyedComponent;
                cannons.remove(singlecannon.getID());
                singleCannonsStrength -= singlecannon.getCannonStrength();
                break;
            case DOUBLE_CANNON:
                Cannon doublecannon = (Cannon) destroyedComponent;
                cannons.remove(doublecannon.getID());
                doubleCannonsStrength -= doublecannon.getCannonStrength();
                doubleCannonsNumber--;
                break;
            case CABIN:
                Cabin cabin = (Cabin) destroyedComponent;
                cabins.remove(cabin.getID());
                crewNumber -= cabin.getCrewNumber();
                break;
            case BROWN_LIFE_SUPPORT:
                for (Component c : getSurroundingComponents(row, column)) {
                    if (c != null && c.getComponentType() == ComponentType.CABIN) {
                        Cabin cabinBrown = (Cabin) c;
                        if (cabinBrown.hasBrownAlien()) {
                            brownAlien = false;
                            crewNumber -= cabinBrown.getCrewNumber();
                            cabinBrown.removeCrewMember(1);
                            break;
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
                            crewNumber -= cabinPurple.getCrewNumber();
                            cabinPurple.removeCrewMember(1);
                            break;
                        }
                    }
                }
                break;
            case STORAGE:
                Storage storage = (Storage) destroyedComponent;
                Good good = storage.peekGood();
                while (good != null) {
                    goods.remove(good);
                    storage.removeGood(good);
                    good = storage.peekGood();
                }
                goodsValue -= storage.getGoodsValue();
                storages.remove(storage.getID());
                break;
            case BATTERY:
                Battery battery = (Battery) destroyedComponent;
                batteries.remove(battery.getID());
                energyNumber -= battery.getEnergyNumber();
                break;
            default:
                break;
        }
        numberOfComponents--;
        lostComponents.add(destroyedComponent);
    }

    /**
     * Search if there is component that are no longer connected to the ship
     * @return List of List of Pair<Integer, Integer> representing the group of disconnected components
     */
    public List<List<Pair<Integer, Integer>>> getDisconnectedComponents() {
        List<List<Pair<Integer, Integer>>> disconnectedComponents = new ArrayList<>();
        boolean[][] visited = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                if (components[i][j] != null && !visited[i][j]) {
                    ArrayList<Pair<Integer, Integer>> disconnectedComponent = new ArrayList<>();
                    Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
                    queue.add(new Pair<>(i, j));
                    visited[i][j] = true;

                    while (!queue.isEmpty()) {
                        Pair<Integer, Integer> current = queue.poll();
                        int currentRow = current.getValue0();
                        int currentColumn = current.getValue1();
                        disconnectedComponent.add(current);

                        Component currentComponent = components[currentRow][currentColumn];

                        for (int face = 0; face < 4; face++) {
                            int newRow = currentRow + (face == 0 ? -1 : face == 2 ? 1 : 0);
                            int newColumn = currentColumn + (face == 1 ? -1 : face == 3 ? 1 : 0);

                            if (newRow >= 0 && newRow < 12 && newColumn >= 0 && newColumn < 12 && components[newRow][newColumn] != null && !visited[newRow][newColumn]) {
                                Component adjacentComponent = components[newRow][newColumn];
                                if ((currentComponent.getConnection(face) == ConnectorType.TRIPLE ||
                                        adjacentComponent.getConnection((face + 2) % 4) == ConnectorType.TRIPLE ||
                                        currentComponent.getConnection(face) == adjacentComponent.getConnection((face + 2) % 4)) &&
                                        currentComponent.getConnection(face) != ConnectorType.EMPTY) {
                                    visited[newRow][newColumn] = true;
                                    queue.add(new Pair<>(newRow, newColumn));
                                }
                            }
                        }
                    }
                    disconnectedComponents.add(disconnectedComponent);
                }
            }
        }
        // TODO: check which group of disconnected components are still valid
        return disconnectedComponents;
    }
}