package Model.SpaceShip;

public class LifeSupportPurple extends Component {
    public LifeSupportPurple(int row, int column, ConnectorType[] connectors) {
        super(row, column, connectors);
    }

    @Override
    public boolean isValid(SpaceShip ship) {
        for (Component component : ship.getSurroundingComponents(row, column)) {
            if (component.getComponentType() == ComponentType.CABIN) {
                Cabin cabin = (Cabin) component;
                cabin.addPurpleLifeSupport();
            }
        }
        return super.isValid(ship);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.PURPLE_LIFE_SUPPORT;
    }
}
