package Model.SpaceShip;

public class LifeSupportBrown extends Component {
    public LifeSupportBrown(int row, int column, ConnectorType[] connectors) {
        super(row, column, connectors);
    }

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
