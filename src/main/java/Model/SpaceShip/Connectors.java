package Model.SpaceShip;

public class Connectors extends Component {
    public Connectors(int row, int column, ConnectorType[] connectors) {
        super(row, column, connectors);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.CONNECTORS;
    }
}
