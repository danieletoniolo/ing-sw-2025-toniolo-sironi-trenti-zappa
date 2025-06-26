package it.polimi.ingsw.model.spaceship;

import java.util.ArrayList;

/**
 * Represents a cabin component of a spaceship that can house crew members or aliens.
 * The cabin requires appropriate life support systems to accommodate different types of occupants.
 * @author Daniele Toniolo
 */
public class Cabin extends Component {
    /** The number of crew members or aliens currently in the cabin */
    private int crewNumber;

    /** Flag indicating if purple life support is available from surrounding components */
    private boolean purpleLifeSupport;
    /** Flag indicating if brown life support is available from surrounding components */
    private boolean brownLifeSupport;

    /** Flag indicating if a purple alien is present in the cabin */
    private boolean purpleAlien;
    /** Flag indicating if a brown alien is present in the cabin */
    private boolean brownAlien;

    /**
     * Creates a new cabin component with the specified ID and connectors.
     * Initializes all life support flags and alien presence flags to false,
     * and sets the crew number to 0.
     *
     * @param ID the unique identifier for this cabin component
     * @param connectors the array of connector types that define how this cabin can connect to other components
     */
    public Cabin(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
        crewNumber = 0;
        purpleLifeSupport = false;
        brownLifeSupport = false;
        purpleAlien = false;
        brownAlien = false;
    }

    /**
     * Creates a new cabin component with default values.
     * Initializes all life support flags and alien presence flags to false,
     * and sets the crew number to 0 through the parent constructor.
     */
    public Cabin(){
        super();
        purpleLifeSupport = false;
        brownLifeSupport = false;
        purpleAlien = false;
        brownAlien = false;
    }

    /**
     * Get the crew number in the cabin
     * @return The number of crew available in the cabin
     */
    public int getCrewNumber() {
        return crewNumber;
    }

    /**
     * Check if the cabin has purple life support
     * @return true if the cabin has purple life support, false otherwise
     */
    public boolean hasPurpleLifeSupport() {
        return purpleLifeSupport;
    }

    /**
     * Check if the cabin has brown life support
     * @return true if the cabin has brown life support, false otherwise
     */
    public boolean hasBrownLifeSupport() {
        return brownLifeSupport;
    }

    /**
     * Check if the cabin has purple alien
     * @return true if the cabin has purple alien, false otherwise
     */
    public boolean hasPurpleAlien() {
        return purpleAlien;
    }

    /**
     * Check if the cabin has brown alien
     * @return true if the cabin has brown alien, false otherwise
     */
    public boolean hasBrownAlien() {
        return brownAlien;
    }

    /**
     * Add two crew member to the cabin
     * @throws IllegalStateException if there is purple alien or brown alien in the cabin
     */
    public void addCrewMember() throws IllegalStateException {
        if (brownAlien || purpleAlien || crewNumber > 0) {
            throw new IllegalStateException("Cannot add crew member to the cabin");
        }
        crewNumber = 2;
    }

    /**
     * Add purple alien to the cabin
     * @throws IllegalStateException if there is no purple life support or there is a brown alien in the cabin
     */
    public void addPurpleAlien() throws IllegalStateException {
        if (!purpleLifeSupport || brownAlien || crewNumber > 0) {
            throw new IllegalStateException("Cannot add purple alien to the cabin");
        }
        purpleAlien = true;
        crewNumber = 1;
    }

    /**
     * Add brown alien to the cabin
     * @throws IllegalStateException if there is no brown life support or there is a purple alien in the cabin
     */
    public void addBrownAlien() throws IllegalStateException {
        if (!brownLifeSupport || purpleAlien || crewNumber > 0) {
            throw new IllegalStateException("Cannot add brown alien to the cabin");
        }
        brownAlien = true;
        crewNumber = 1;
    }

    /**
     * Remove crew member or alien from the cabin
     * @throws IllegalStateException if there is no crew member in the cabin
     */
    public void removeCrewMember(int num) throws IllegalStateException {
        if (crewNumber > 0 && num <= crewNumber) {
            if (purpleAlien || brownAlien) {
                purpleAlien = false;
                brownAlien = false;
            }
            crewNumber -= num;
        } else {
            throw new IllegalStateException("There isn't enough crew member in the cabin");
        }
    }

    /**
     * Extend the isValid method from the Component class to lock for life support in the surrounding components
     * @return true if the cabin is valid, false otherwise
     */
    @Override
    public boolean isValid() {
        ArrayList<Component> components = ship.getSurroundingComponents(row, column);
        for (Component c : components) {
            if (c != null) {
                if (c.getComponentType() == ComponentType.BROWN_LIFE_SUPPORT) {
                    brownLifeSupport = true;
                } else if (c.getComponentType() == ComponentType.PURPLE_LIFE_SUPPORT) {
                    purpleLifeSupport = true;
                }
            }
        }
        return super.isValid();
    }

    /**
     * Returns the component type for this cabin.
     * @return ComponentType.CABIN indicating this is a cabin component
     */
    @Override
    public ComponentType getComponentType() {
        return ComponentType.CABIN;
    }
}