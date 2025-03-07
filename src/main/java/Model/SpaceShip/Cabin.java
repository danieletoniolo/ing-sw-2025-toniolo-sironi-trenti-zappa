package Model.SpaceShip;

public class Cabin extends Component {
    private int crewNumber;

    private boolean purpleLifeSupport;
    private boolean brownLifeSupport;

    private boolean purpleAlien;
    private boolean brownAlien;

    public Cabin(int row, int column, ConnectorType[] connectors) {
        super(row, column, connectors);
        crewNumber = 0;
        purpleLifeSupport = false;
        brownLifeSupport = false;
        purpleAlien = false;
        brownAlien = false;
    }

    /*
     @brief Get the crew number in the cabin
     @return crewNumber
     */
    public int getCrewNumber() {
        return crewNumber;
    }

    /*
     @brief Check if the cabin has purple life support
     @return true if the cabin has purple life support, false otherwise
     */
    public boolean hasPurpleLifeSupport() {
        return purpleLifeSupport;
    }

    /*
     @brief Check if the cabin has brown life support
     @return true if the cabin has brown life support, false otherwise
     */
    public boolean hasBrownLifeSupport() {
        return brownLifeSupport;
    }

    /*
     @brief Check if the cabin has purple alien
     @return true if the cabin has purple alien, false otherwise
     */
    public boolean hasPurpleAlien() {
        return purpleAlien;
    }

    /*
     @brief Check if the cabin has brown alien
     @return true if the cabin has brown alien, false otherwise
     */
    public boolean hasBrownAlien() {
        return brownAlien;
    }

    /*
     @brief Add two crew member to the cabin
     */
    public void addCrewMember() {
        crewNumber = 2;
    }

    /*
     @brief Add purple alien to the cabin
     @return true if the cabin has purple alien, false otherwise
     */
    public void addPurpleAlien() {
        purpleAlien = true;
        crewNumber = 1;
    }

    /*
     @brief Add brown alien to the cabin
     @return true if the cabin has brown alien, false otherwise
     */
    public void addBrownAlien() {
        brownAlien = true;
        crewNumber = 1;
    }

    /*
     @brief Remove crew member from the cabin
     @throws IllegalStateException if there is no crew member in the cabin
     */
    public void removeCrewMember() {
        if (crewNumber > 0) {
            crewNumber--;
        } else {
            throw new IllegalStateException("There is no crew member in the cabin");
        }
    }

    /*
     @brief Remove alien from the cabin
     */
    public void removeAlien() {
        purpleAlien = false;
        brownAlien = false;
        crewNumber = 0;
    }

    /*
     @brief Add purple life support to the cabin
     */
    public void addPurpleLifeSupport() {
        purpleLifeSupport = true;
    }

    /*
     @brief Add brown life support to the cabin
     */
    public void addBrownLifeSupport() {
        brownLifeSupport = true;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.CABIN;
    }
}
