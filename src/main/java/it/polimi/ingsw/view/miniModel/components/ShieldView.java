package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.ShieldController;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShieldView extends ComponentView {
    private final List<MiniModelObserver> listeners = new ArrayList<>();
    public static String UpShield = "∩";
    public static String DownShield = "∪";
    public static String LeftShield = "(";
    public static String RightShield = ")";
    private String lightGreen = "\033[92m";
    private String reset = "\033[0m";
    private boolean[] shields;

    public ShieldView(int ID, int[] connectors, int clockWise, boolean[] shields) {
        super(ID, connectors, clockWise);
        this.shields = shields;
    }

    public boolean[] getShields() {
        return shields;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/shield.fxml"));
            Node root = loader.load();

            ShieldController controller = loader.getController();
            controller.setShieldModel(this);

            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setShields(boolean[] shields) {
        this.shields = shields;
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
            case 1 -> super.drawLeft(line) + drawShield() + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    private String drawShield() {
        StringBuilder str = new StringBuilder();
        str.append(" ");
        if (shields[1]) {
            str.append(lightGreen).append(LeftShield).append(reset);
            if (shields[0]) str.append(lightGreen).append(" ").append(UpShield).append(reset);
            if (shields[2]) str.append(lightGreen).append(" ").append(DownShield).append(reset);
        }
        if (shields[3]) {
            if (shields[0]) str.append(lightGreen).append(UpShield).append(reset);
            if (shields[2]) str.append(lightGreen).append(DownShield).append(reset);
            str.append(" ").append(lightGreen).append(RightShield).append(reset);
        }
        str.append(" ");
        return str.toString();
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.SHIELD;
    }

    @Override
    public ShieldView clone() {
        ShieldView copy = new ShieldView(this.getID(), this.getConnectors(), this.getClockWise(), this.shields);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
