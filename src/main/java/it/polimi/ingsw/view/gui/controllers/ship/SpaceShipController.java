package it.polimi.ingsw.view.gui.controllers.ship;

import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SpaceShipController implements MiniModelObserver, Initializable {

    /**
     * The StackPane that serves as the parent container for the ship area.
     * It is set in the FXML file and is used to display the ship components.
     */
    @FXML StackPane parent;

    /**
     * The ImageView that displays the background image of the ship (the cardboard background).
     * The image is set in the FXML file.
     */
    @FXML ImageView backgroundImage;

    /**
     * The Group that contains the ship components.
     * It is set in the FXML file and is used to group the ship components together.
     */
    @FXML Group shipGroup;

    /**
     * The GridPane that contains the ship components.
     * It is set in the FXML file and is used to display the components of the ship.
     */
    @FXML GridPane shipGrid;

    /**
     * The secondary GridPane for additional UI elements.
     */
    @FXML GridPane reserveLostGrid;

    /**
     * The SpaceShipView model associated with this controller.
     * It is set via the setModel method after the FXML has been loaded.
     */
    private SpaceShipView spaceShipModel;

    // Original dimensions of the background image
    private static int GRID_COLS;
    private static int GRID_ROWS;

    // Original dimensions of the background image
    private static double ORIGINAL_IMAGE_WIDTH;
    private static double ORIGINAL_IMAGE_HEIGHT;

    // Original dimensions and positions for the ship
    private static double ORIGINAL_GRID_X;
    private static double ORIGINAL_GRID_Y;
    private static double ORIGINAL_GRID_WIDTH;
    private static double ORIGINAL_GRID_HEIGHT;

    // Original dimensions and positions for the reserve/lost grid
    private static double ORIGINAL_SECONDARY_X;
    private static double ORIGINAL_SECONDARY_Y;
    private static double ORIGINAL_SECONDARY_WIDTH;
    private static double ORIGINAL_SECONDARY_HEIGHT;


    /**
     * Initializes the layout and bindings of the graphical elements in the ship view.
     * This method prepares the UI components for proper scaling, positioning, and responsiveness
     * when the parent container is resized. It also sets the default values for the components
     * and clears any previous bindings to avoid conflicts.
     * The initialization process includes:
     * <ul>
     *     <li>Setting default dimensions and positions for the components.</li>
     *     <li>Creating and applying scale and translation bindings to the ship group and
     *     background image, ensuring they resize proportionally to the parent container. </li>
     *     <li>Binding dimensions and positions of the ship grid and reserve grid to maintain
     *     cohesion with the scaled background image.</li>
     *     <li>Centering the ship group within the parent container.</li>
     *     <li>Configuring constraints to ensure a uniform and responsive layout for the ship
     *     grid and reserve grid.</li>
     * </ul>
     * Utility methods such as `setDefaultValue()` and `setupShipGridConstraints()` are invoked to
     * aid in initializing specific detailed aspects of the components.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set the default value from the FXML
        setDefaultValue();

        shipGroup.setManaged(false);

        // Remove any existing bindings to prevent conflicts
        backgroundImage.fitWidthProperty().unbind();
        backgroundImage.fitHeightProperty().unbind();
        shipGroup.scaleXProperty().unbind();
        shipGroup.scaleYProperty().unbind();
        shipGroup.translateXProperty().unbind();
        shipGroup.translateYProperty().unbind();

        // Reset the scale and translation of the ship group
        shipGroup.setScaleX(1.0);
        shipGroup.setScaleY(1.0);
        shipGroup.setTranslateX(0.0);
        shipGroup.setTranslateY(0.0);

        // Binding for the scale factor based on the parent StackPane size
        DoubleBinding scaleFactorBinding = Bindings.createDoubleBinding(() -> {
            double parentWidth = parent.getWidth();
            double parentHeight = parent.getHeight();

            if (parentWidth <= 0 || parentHeight <= 0) return 1.0;

            double scaleX = parentWidth / ORIGINAL_IMAGE_WIDTH;
            double scaleY = parentHeight / ORIGINAL_IMAGE_HEIGHT;

            return Math.min(scaleX, scaleY);
        }, parent.widthProperty(), parent.heightProperty());

        // Binding for the scale of the background image
        backgroundImage.fitWidthProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_IMAGE_WIDTH)
        );
        backgroundImage.fitHeightProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_IMAGE_HEIGHT)
        );

        // Binding for the scale of the ship grid
        shipGrid.layoutXProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_GRID_X)
        );
        shipGrid.layoutYProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_GRID_Y)
        );
        shipGrid.prefWidthProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_GRID_WIDTH)
        );
        shipGrid.prefHeightProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_GRID_HEIGHT)
        );

        // Binding for the scale of the gap in the ship grid
        // TODO: If you want to scale the gaps, uncomment the following lines
        //       To do so, you must add the gaps in the FXML file
        //shipGrid.hgapProperty().bind(scaleFactorBinding.multiply(10.0));
        //shipGrid.vgapProperty().bind(scaleFactorBinding.multiply(10.0));
        //reserveLostGrid.hgapProperty().bind(scaleFactorBinding.multiply(10.0));


        // Binding for the scale of the reserve grid
        reserveLostGrid.layoutXProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_SECONDARY_X)
        );
        reserveLostGrid.layoutYProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_SECONDARY_Y)
        );
        reserveLostGrid.prefWidthProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_SECONDARY_WIDTH)
        );
        reserveLostGrid.prefHeightProperty().bind(
                scaleFactorBinding.multiply(ORIGINAL_SECONDARY_HEIGHT)
        );


        // Center the ship group within the parent StackPane
        DoubleBinding centerXBinding = Bindings.createDoubleBinding(() -> {
            double scaledWidth = scaleFactorBinding.get() * ORIGINAL_IMAGE_WIDTH;
            double freeSpace = parent.getWidth() - scaledWidth;
            return Math.max(0, freeSpace / 2.0);
        }, parent.widthProperty(), scaleFactorBinding);

        DoubleBinding centerYBinding = Bindings.createDoubleBinding(() -> {
            double scaledHeight = scaleFactorBinding.get() * ORIGINAL_IMAGE_HEIGHT;
            double freeSpace = parent.getHeight() - scaledHeight;
            return Math.max(0, freeSpace / 2.0);
        }, parent.heightProperty(), scaleFactorBinding);

        shipGroup.translateXProperty().bind(centerXBinding);
        shipGroup.translateYProperty().bind(centerYBinding);

        // Initialize the grid constraints to maintain a square layout
        setupShipGridConstraints();
    }

    /**
     * Configures the constraints of the ship grid to ensure consistent layout
     * behavior. The method dynamically calculates the percentage width and height
     * for each column and row in the grid based on the total number of columns
     * and rows. It then applies these constraints to ensure that columns and rows
     * scale proportionally.
     * <p>
     * The column constraints are set to allow horizontal growth, and the row
     * constraints are set to allow vertical growth, ensuring flexibility in
     * resizing. All existing constraints in the grid are cleared before applying
     * new ones to avoid conflicts.
     * <p>
     * The method is designed to maintain a uniform and responsive layout for the
     * ship grid by distributing available space evenly among its rows and columns.
     */
    private void setupShipGridConstraints() {
        double percW = 100.0 / GRID_COLS;
        double percH = 100.0 / GRID_ROWS;

        shipGrid.getColumnConstraints().clear();
        shipGrid.getRowConstraints().clear();

        for (int c = 0; c < GRID_COLS; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(percW);
            cc.setHgrow(Priority.ALWAYS);
            shipGrid.getColumnConstraints().add(cc);
        }

        for (int r = 0; r < GRID_ROWS; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(percH);
            rc.setVgrow(Priority.ALWAYS);
            shipGrid.getRowConstraints().add(rc);
        }
    }

    /**
     * Sets the default values for various properties related to the ship grid,
     * background image, and reserve grid. The method captures the initial dimensions
     * and positions of UI elements to ensure consistent behavior and layout during
     * resizing or scaling operations. Specifically, it:
     * <ul>
     *   <li>Records the column and row counts of the ship grid.</li>
     *   <li>Stores the original dimensions of the background image.</li>
     *   <li>Captures the initial layout and preferred dimensions of the ship grid.</li>
     *   <li>Records the initial layout and preferred dimensions of the reserve grid.</li>
     * </ul>
     * <p>
     * This method is intended to act as a baseline setup to maintain responsive
     * UI behavior by referencing these default values when recalculating dimensions
     * or positions of the elements.
     */
    private void setDefaultValue() {
        GRID_COLS = shipGrid.getColumnCount();
        GRID_ROWS = shipGrid.getRowCount();

        ORIGINAL_IMAGE_WIDTH = backgroundImage.getFitWidth();
        ORIGINAL_IMAGE_HEIGHT = backgroundImage.getFitHeight();

        ORIGINAL_GRID_X = shipGrid.getLayoutX();
        ORIGINAL_GRID_Y = shipGrid.getLayoutY();
        ORIGINAL_GRID_WIDTH = shipGrid.getPrefWidth();
        ORIGINAL_GRID_HEIGHT = shipGrid.getPrefHeight();

        ORIGINAL_SECONDARY_X = reserveLostGrid.getLayoutX();
        ORIGINAL_SECONDARY_Y = reserveLostGrid.getLayoutY();
        ORIGINAL_SECONDARY_WIDTH = reserveLostGrid.getPrefWidth();
        ORIGINAL_SECONDARY_HEIGHT = reserveLostGrid.getPrefHeight();
    }

    /**
     * Sets the current model for the controller and registers the controller as an observer
     * to the specified model. This method ensures that the controller reacts to changes
     * in the model by invoking the appropriate reactions.
     *
     * @param model the model object to be set, which is expected to be an instance of SpaceShipView.
     */
    public void setModel(Object model) {
        this.spaceShipModel = (SpaceShipView) model;
        this.spaceShipModel.registerObserver(this);
        this.react();
    }

    /**
     * Reacts to changes in the {@code SpaceShipView} model by updating the user interface.
     * <p>
     * This method is invoked when the model notifies its observers of a state change.
     * It begins by clearing the {@code shipGrid} of all existing components. Subsequently, it iterates
     * through the grid cells, retrieving the corresponding {@code ComponentView} from the
     * {@code spaceShipModel}. For each non-null component, its graphical node is configured
     * to expand and fill the cell (by setting the Hrow, Vrow, setFillWidth, and
     * setFillHeight properties) and is centered within it. The node is then added to the
     * {@code shipGrid}.
     * <p>
     * Finally, the method updates the {@code reserveLostGrid} with the last two components
     * from the model's reserved card stack, ensuring that the visual representation
     * of the ship and its components remains synchronized with the model's data.
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            shipGrid.getChildren().clear();

            int rowOffset = SpaceShipView.ROW_OFFSET;
            int colOffset = spaceShipModel.getLevel() == LevelView.SECOND ? SpaceShipView.COL_OFFSET : SpaceShipView.COL_OFFSET + 1;

            for (int i = 0; i < GRID_COLS; i++) {
                for (int j = 0; j < GRID_ROWS; j++) {
                    ComponentView component = spaceShipModel.getComponent(j + rowOffset, i + colOffset);
                    if (component != null) {
                        Node node = component.getNode().getValue0();

                        GridPane.setHgrow(node, Priority.ALWAYS);
                        GridPane.setVgrow(node, Priority.ALWAYS);
                        GridPane.setFillWidth(node, true);
                        GridPane.setFillHeight(node, true);
                        GridPane.setHalignment(node, HPos.CENTER);
                        GridPane.setValignment(node, VPos.CENTER);

                        shipGrid.add(node, i, j);
                    }
                }
            }

            ArrayList<ComponentView> reservedDiscardedList = spaceShipModel.getDiscardReservedPile().getReserved();
            int size = reservedDiscardedList.size();
            if (size > 0 && reservedDiscardedList.get(size - 1) != null) {
                reserveLostGrid.add(reservedDiscardedList.get(size - 1).getNode().getValue0(), 0, 0);
            }
            if (size > 1 && reservedDiscardedList.get(size - 2) != null) {
                reserveLostGrid.add(reservedDiscardedList.get(size - 2).getNode().getValue0(), 1, 0);
            }
        });
    }

    public List<ComponentController> getShipComponentControllers() {
        List<ComponentController> controllers = new ArrayList<>();
        for (ComponentView[] row : spaceShipModel.getSpaceShip()) {
            for (ComponentView component : row) {
                if (component != null) {
                    controllers.add(component.getNode().getValue1());
                }
            }
        }
        return controllers;
    }

    public List<ComponentController> getReservedComponentControllers() {
        List<ComponentController> controllers = new ArrayList<>();
        for (ComponentView component : spaceShipModel.getDiscardReservedPile().getReserved()) {
            if (component != null) {
                controllers.add(component.getNode().getValue1());
            }
        }
        return controllers;
    }

    public GridPane getReserveLostGrid() {
        return reserveLostGrid;
    }
}