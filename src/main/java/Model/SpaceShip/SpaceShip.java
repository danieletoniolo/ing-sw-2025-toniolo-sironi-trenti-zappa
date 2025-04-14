package Model.SpaceShip;

import java.util.*;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Game.Board.Level;
import Model.Good.Good;
import org.javatuples.Pair;

public class SpaceShip {
    private final Level level;

    private static final float alienStrength = 2.0f;
    private static final int rows = 12;
    private static final int cols = 12;
    private Component[][] components;
    private int numberOfComponents;
    private final boolean[][] validSpots;

    private List<Component> lostComponents;
    private ArrayList<Component> reservedComponents;

    private Map<Integer, Storage> storages;
    private Map<Integer, Battery> batteries;
    private Map<Integer, Cabin> cabins;
    private Map<Integer, Cannon> cannons;
    private PriorityQueue<Good> goods;
    private Component lastPlacedComponent;

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

    public SpaceShip(Level level, boolean[][] validSpots) {
        this.level = level;

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

        storages = new HashMap<>();
        batteries = new HashMap<>();
        cabins = new HashMap<>();
        cannons = new HashMap<>();
        goods = new PriorityQueue<>(Comparator.comparingInt(Good::getValue).reversed());
        lastPlacedComponent = null;

        // TODO: The center cabin should be 6 6 and not 7 7 (because of the 0 index)
        components = new Component[rows][cols];
        components[7][7] = new Cabin(1, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        components[7][7].ship = this;
        components[7][7].setRow(7);
        components[7][7].setColumn(7);
        cabins.put(components[7][7].getID(), (Cabin) components[7][7]);
        numberOfComponents = 1;
    }

    /**
     * Find the component in the ship that is being hit from the given direction
     * @param dice direction of the hit (Number picked by dice roll)
     * @param direction direction of the hit (North, West, South, East)
     * @return The component that is being hit from the given direction
     */
    private Component findHitComponent(int dice, Direction direction) {
        switch (direction) {
            case NORTH:
                for (int i = 0; i < rows; i++) {
                    if (components[i][dice] != null) return components[i][dice];
                }
                break;
            case WEST:
                for (int i = 0; i < cols; i++) {
                    if (components[dice][i] != null) return components[dice][i];
                }
                break;
            case SOUTH:
                for (int i = rows - 1; i >= 0; i--) {
                    if (components[i][dice] != null) return components[i][dice];
                }
                break;
            case EAST:
                for (int i = cols - 1; i >= 0; i--) {
                    if (components[dice][i] != null) return components[dice][i];
                }
                break;
            default:
                throw new IllegalArgumentException("The direction of the hit is not valid");
        }
        return null;
    }

    /**
     * Check if the ship can shield from a hit
     * @param direction direction of the hit (North, West, South, East)
     * @return true if the ship can shield from the hit, false otherwise
     */
    private boolean canShield(int direction) {
        for (Component[] row : components) {
            for (Component comp : row) {
                if (comp != null && comp.getComponentType() == ComponentType.SHIELD) {
                    Shield shield = (Shield) comp;
                    if (shield.canShield(direction)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if the ship can protect from a large meteor
     * @param dice direction of the hit (Number picked by dice roll)
     * @param direction direction of the hit (North, West, South, East)
     * @return true if the ship can protect from the large meteor, false otherwise
     */
    private boolean canProtectFromLargeMeteor(int dice, int direction) {
        if (level == Level.SECOND && direction % 2 != 0) {
            for (int j = 0; j < 12; j++) {
                if (isCannonProtecting(dice - 1, j, direction) || isCannonProtecting(dice + 1, j, direction)) {
                    return true;
                }
            }
        }
        for (int j = 0; j < 12; j++) {
            if (isCannonProtecting(direction % 2 == 0 ? j : dice, direction % 2 == 0 ? dice : j, direction)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the cannon is protecting from the given direction
     * @param row row of the cannon
     * @param col column of the cannon
     * @param direction direction of the hit (North, West, South, East)
     * @return true if the cannon is protecting from the given direction, false otherwise
     */
    private boolean isCannonProtecting(int row, int col, int direction) {
        Component comp = components[row][col];
        if (comp != null) {
            if (comp.getComponentType() == ComponentType.SINGLE_CANNON && comp.getClockwiseRotation() == direction) {
                return true;
            } else if (comp.getComponentType() == ComponentType.DOUBLE_CANNON && comp.getClockwiseRotation() == direction) {
                return true;
            }
        }
        return false;
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
    public int getDoubleCannonsStrength() {
        return doubleCannonsStrength;
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
    public static float getAlienStrength() {
        return alienStrength;
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
        return goods;
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
    public void exchangeGood(ArrayList<Good> goodsToAdd, ArrayList<Good> goodsToRemove, int storageID) {
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
            if (cabin.hasBrownAlien()) {
                throw new IllegalStateException("The cabin already has a brown alien");
            }
            this.brownAlien = true;
            cabin.addBrownAlien();
        } else if (purpleAlien) {
            if (cabin.hasPurpleAlien()) {
                throw new IllegalStateException("The cabin already has a purple alien");
            }
            this.purpleAlien = true;
            cabin.addPurpleAlien();
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
        Component component = findHitComponent(dice, hit.getDirection());
        if (component == null) {
            return new Pair<>(null, 1);
        }

        switch (hit.getType()) {
            case SMALLMETEOR:
                if (component.getConnection(hit.getDirection().getValue()) == ConnectorType.EMPTY) {
                    return new Pair<>(null, 1);
                }
            case LIGHTFIRE:
                if (canShield(hit.getDirection().getValue())) {
                    return new Pair<>(component, 0);
                }
                break;
            case LARGEMETEOR:
                if (canProtectFromLargeMeteor(dice, hit.getDirection().getValue())) {
                    return new Pair<>(null, 1);
                }
                break;
            case HEAVYFIRE:
                break;
            default:
                throw new IllegalArgumentException("The type of the hit is not valid");
        }
        return new Pair<>(component, -1);
    }

    /**
     * Use a battery and reduce the total number of energy
     * @param ID ID of the battery to use
     * @return true if the battery was used, false otherwise
     * @throws IllegalArgumentException if the component at the given row and column is not a battery
     */
    public boolean useEnergy(int ID) throws IllegalArgumentException {
        Battery battery = batteries.get(ID);
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
        exposedConnectors = 0;
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
     */
    public Component getComponent(int row, int column) {
        return components[row][column];
    }

    /**
     * Get the components of the ship
     * @return components of the ship
     */
    public Component[][] getComponents() {
        return components;
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
    public void reserveComponent(Component c) throws IllegalStateException {
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
     * @apiNote Should only be called when the component is placed
     * @param c the component to unreserve
     */
    public void unreserveComponent(Component c) {
        reservedComponents.remove(c);
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
                    break;
                case DOUBLE_ENGINE:
                    doubleEnginesStrength+=2;
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
     * @return cabin in the ship
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
    public Map<Integer, Cabin> getCabins() {
        return cabins;
    }

    /**
     * Get the storage in the ship by ID
     * @return storage in the ship
     */
    public Storage getStorage(int ID) throws IllegalArgumentException {
        if (storages.get(ID) == null) {
            throw new IllegalArgumentException("The ID of the storage is not valid");
        }
        return this.storages.get(ID);
    }

    /**
     * Get the storages in the ship
     * @return storages in the ship
     */
    public Map<Integer, Storage> getStorages() {
        return storages;
    }

    /**
     * Get the battery in the ship by ID
     * @return battery in the ship
     */
    public Battery getBattery(int ID) throws IllegalArgumentException {
        if (batteries.get(ID) == null) {
            throw new IllegalArgumentException("The ID of the battery is not valid");
        }
        return this.batteries.get(ID);
    }

    /**
     * Get the batteries in the ship
     * @return get batteries in the ship
     */
    public Map<Integer, Battery> getBatteries() {
        return batteries;
    }

    /**
     * Get the cannon in the ship by ID
     * @return cannon in the ship
     */
    public Cannon getCannon(int ID) throws IllegalArgumentException {
        if (cannons.get(ID) == null) {
            throw new IllegalArgumentException("The ID of the cannon is not valid");
        }
        return this.cannons.get(ID);
    }

    /**
     * Get the cannons in the ship
     * @return cannons in the ship
     */
    public Map<Integer, Cannon> getCannons() {
        return cannons;
    }

    /**
     * Get the last placed component
     * @return last placed component
     */
    public Component getLastPlacedComponent() {
        return lastPlacedComponent;
    }

    /**
     * Set the last placed component
     * @param component the component to set as last placed
     */
    public void setLastPlacedComponent(Component component) {
        lastPlacedComponent = component;
    }

    /**
     * Fix a component at the given row and column
     * @param row row of the component to fix
     * @param column column of the component to fix
     * @throws IllegalStateException if the component is null or already fixed
     */
    public void fixComponent(int row, int column) throws IllegalStateException{
        Component component = components[row][column];
        if (component != null && !component.isFixed()) {
            component.fix();
            lastPlacedComponent = null;
        } else {
            throw new IllegalStateException("The component at the given row and column is null or already fixed");
        }
    }

    /**
     * Destroy a component at the given row and column and update the stats of the ship
     * @param row row of the component to destroy
     * @param column column of the component to destroy
     */
    public void destroyComponent(int row, int column) throws IllegalArgumentException {
        Component destroyedComponent = components[row][column];
        if (destroyedComponent == null) {
            throw new IllegalArgumentException("The component at the given row and column is null");
        }
        components[row][column] = null;

        switch (destroyedComponent.getComponentType()) {
            case SINGLE_ENGINE:
                singleEnginesStrength--;
                break;
            case DOUBLE_ENGINE:
                doubleEnginesStrength-=2;
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
                            cabinBrown.removeCrewMember(1);;
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
                for (Good good : storage.getGoods()) {
                    goods.remove(good);
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
        lastPlacedComponent = null;
        numberOfComponents--;
        lostComponents.add(destroyedComponent);
    }

    /**
     * Search if there is component that are no longer connected to the ship
     * @return ArrayList of ArrayList of Pair<Integer, Integer> representing the group of disconnected components
     */
    public ArrayList<ArrayList<Pair<Integer, Integer>>> getDisconnectedComponents() {
        ArrayList<ArrayList<Pair<Integer, Integer>>> disconnectedComponents = new ArrayList<>();
        boolean[][] visited = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                if (components[i][j] != null && !visited[i][j]) {
                    ArrayList<Pair<Integer, Integer>> disconnectedComponent = new ArrayList<>();
                    Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
                    queue.add(new Pair<Integer, Integer>(i, j));
                    visited[i][j] = true;

                    while (!queue.isEmpty()) {
                        Pair<Integer, Integer> current = queue.poll();
                        int currentRow = current.getValue0();
                        int currentColumn = current.getValue1();
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
                                    queue.add(new Pair<Integer, Integer>(newRow, newColumn));
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