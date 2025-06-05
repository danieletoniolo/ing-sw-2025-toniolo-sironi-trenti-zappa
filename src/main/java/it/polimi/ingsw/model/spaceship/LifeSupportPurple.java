package it.polimi.ingsw.model.spaceship;

public class LifeSupportPurple extends Component {
    public LifeSupportPurple(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    public LifeSupportPurple() {
        super();
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.PURPLE_LIFE_SUPPORT;
    }
}
