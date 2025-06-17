package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.CabinController;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.crewmembers.CrewView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CabinView extends ComponentView {
    private final List<MiniModelObserver> listeners = new ArrayList<>();
    private final String lightBlue = "\033[94m";
    private final String blue = "\033[34m";
    private final String green = "\033[32m";
    private final String yellow = "\033[33m";
    private final String red = "\033[31m";
    private final String reset = "\033[0m";
    private final String color;

    private int crewNumber;
    private CrewView crew;

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
    }

    public void setCrewType(CrewView crew) {
        this.crew = crew;
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


    public void addListener(MiniModelObserver listener) {
        listeners.add(listener);
    }

    public void removeListener(MiniModelObserver listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (MiniModelObserver listener : listeners) {
            listener.onModelChanged();
        }
    }

    public Node createGuiNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/cabin.fxml"));
            Node root = loader.load();

            CabinController controller = loader.getController();
            controller.setCabinModel(this);

            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Draws the component GUI.
     * This method is called to draw the component GUI.
     *
     * @return an Image representing the image of the component
     */
    @Override
    public Image drawGui() {
        String path = "/image/tiles/" + this.getID() + ".jpg";
        Image img = new Image(getClass().getResource(path).toExternalForm());
        return img;
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
    public TilesTypeView getType() {
        return TilesTypeView.CABIN;
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
