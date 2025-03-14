package Model.SpaceShip;

public class Connectors extends Component {
    public Connectors(int ID, int row, int column, ConnectorType[] connectors) {
        super(ID, row, column, connectors);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.CONNECTORS;
    }
}
