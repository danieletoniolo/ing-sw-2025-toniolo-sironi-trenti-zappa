package Model.SpaceShip;

public class LifeSupportPurple extends Component {
    public LifeSupportPurple(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.PURPLE_LIFE_SUPPORT;
    }
}
