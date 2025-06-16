package it.polimi.ingsw.view.gui.controllers.ship;


import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class SpaceshipController {

    @FXML
    private ImageView spaceshipImage;

    @FXML
    private GridPane modulesGrid;

    private SpaceShipView spaceShipModel;

    public void setSpaceshipModel(SpaceShipView spaceshipModel) {
        this.spaceShipModel = spaceshipModel;
        updateSpaceship();
    }

    private void updateSpaceship() {
        // Carica immagine (opzionale)
        String path = "/image/cardboard/ship_II.jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        spaceshipImage.setImage(image);

        // Esempio: popola la griglia
        modulesGrid.getChildren().clear();
        int rows = 5;
        int cols = 5;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Semplice esempio: metti un cerchio per ogni modulo
                javafx.scene.shape.Circle moduleView = new javafx.scene.shape.Circle(10);
                modulesGrid.add(moduleView, c, r);
            }
        }
    }
}
