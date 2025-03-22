package Model.SpaceShip;

public class LifeSupportPurple extends Component {
    public LifeSupportPurple(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    /**
     * @implNote Extends the isValid method of the Component class to add purple life support to the cabin next to the component
     */
    @Override
    public boolean isValid() {
        for (Component component : ship.getSurroundingComponents(row, column)) {
            if (component.getComponentType() == ComponentType.CABIN) {
                Cabin cabin = (Cabin) component;
                cabin.addPurpleLifeSupport();
            }
        }
        return super.isValid();
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.PURPLE_LIFE_SUPPORT;
    }
}
