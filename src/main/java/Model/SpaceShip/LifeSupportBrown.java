package Model.SpaceShip;

public class LifeSupportBrown extends Component {
    public LifeSupportBrown(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    public LifeSupportBrown(){
        super();
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.BROWN_LIFE_SUPPORT;
    }
}
