package Model.SpaceShip;

public class LifeSupportBrown extends Component {
    public LifeSupportBrown(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.BROWN_LIFE_SUPPORT;
    }
}
