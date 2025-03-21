package Model.SpaceShip;

public class Cabin extends Component {
    private int crewNumber;

    private boolean purpleLifeSupport;
    private boolean brownLifeSupport;

    private boolean purpleAlien;
    private boolean brownAlien;

    public Cabin(int ID, int row, int column, ConnectorType[] connectors) {
        super(ID, row, column, connectors);
        crewNumber = 0;
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
        if (brownAlien || purpleAlien) {
            throw new IllegalStateException("Cannot add crew member to the cabin");
        }
        crewNumber = 2;
    }

    /**
     * Add purple alien to the cabin
     * @throws IllegalStateException if there is no purple life support or there is a brown alien in the cabin
     */
    public void addPurpleAlien() throws IllegalStateException {
        if (!purpleLifeSupport || brownAlien) {
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
        if (!brownLifeSupport || purpleAlien) {
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
            super.ship.addCrewMember(-num);
        } else {
            throw new IllegalStateException("There isn't enough crew member in the cabin");
        }
    }

    /**
     * Add purple life support to the cabin
     */
    public void addPurpleLifeSupport() {
        purpleLifeSupport = true;
    }

    /**
     * Add brown life support to the cabin
     */
    public void addBrownLifeSupport() {
        brownLifeSupport = true;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.CABIN;
    }
}