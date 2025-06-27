package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.BatteryController;
import it.polimi.ingsw.view.gui.controllers.components.CabinController;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.miniModel.components.crewmembers.CrewView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.javatuples.Pair;

import java.io.IOException;

public class CabinView extends ComponentView {
    private static final String lightBlue = "\033[94m";
    private static final String blue = "\033[34m";
    private static final String green = "\033[32m";
    private static final String yellow = "\033[33m";
    private static final String red = "\033[31m";
    private static final String reset = "\033[0m";
    private final String color;

    private int crewNumber;
    private CrewView crew;
    private Pair<Node, ComponentController> cabinPair;

    public CabinView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
        this.crewNumber = 0;
        switch (ID) {
            case 152 -> this.color = blue;
            case 153 -> this.color = green;
            case 154 -> this.color = red;
            case 155 -> this.color = yellow;
            default -> this.color = lightBlue;
        }
        crew = CrewView.HUMAN;
    }

    public int getCrewNumber() {
        return crewNumber;
    }

    public void setCrewNumber(int crewNumber) {
        this.crewNumber = crewNumber;
        notifyObservers();
    }

    public void setCrewType(CrewView crew) {
        this.crew = crew;
        notifyObservers();
    }

    public CrewView getCrewType() {
        return crew;
    }

    public boolean hasPurpleAlien() {
        return crew.equals(CrewView.PURPLEALIEN);
    }

    public boolean hasBrownAlien() {
        return crew.equals(CrewView.BROWALIEN);
    }

    @Override
    public Pair<Node, ComponentController> getNode() {
        try {
            if (cabinPair != null) return cabinPair;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/cabin.fxml"));
            Parent root = loader.load();

            CabinController controller = loader.getController();
            controller.setModel(this);

            cabinPair = new Pair<>(root, controller);
            return cabinPair;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + drawCrew() + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    private String drawCrew() {
        return switch (crewNumber) {
            case 0 -> color + "(" + reset + "   " + color + ")" + reset;
            case 1 -> color + "(" + reset + " " + crew.drawTui() + " " + color + ")" + reset;
            case 2 -> color + "(" + reset + crew.drawTui() + " " + crew.drawTui() + color + ")" + reset;
            default -> throw new IllegalStateException("Unexpected value: " + crewNumber);
        };
    }

    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.CABIN;
    }

    @Override
    public CabinView clone() {
        CabinView copy = new CabinView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setCrewType(this.crew);
        copy.setCrewNumber(this.crewNumber);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
