package Model.SpaceShip;

public class LifeSupportBrown extends Component {
    public LifeSupportBrown(int ID, int row, int column, ConnectorType[] connectors) {
        super(ID, row, column, connectors);
    }

    /**
     * @implNote Extends the isValid method of the Component class to add brown life support to the cabin next to the component.
     */
    @Override
    public boolean isValid(SpaceShip ship) {
        for (Component component : ship.getSurroundingComponents(row, column)) {
            if (component.getComponentType() == ComponentType.CABIN) {
                Cabin cabin = (Cabin) component;
                cabin.addBrownLifeSupport();
            }
        }
        return super.isValid(ship);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.BROWN_LIFE_SUPPORT;
    }
}
