package Model.SpaceShip;

public class Connectors extends Component {
    public Connectors(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.CONNECTORS;
    }
}
