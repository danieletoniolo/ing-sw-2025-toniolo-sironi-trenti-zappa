package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.spaceship.Cabin;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.components.CabinView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.components.GenericComponentView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.IOException;

public class SpaceShipGui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ship_second.fxml"));
        AnchorPane root = loader.load();

        SpaceShipView ship = new SpaceShipView(LevelView.SECOND);
        Image img_s = ship.drawGui();
        CabinView c = new CabinView(155, null, 1);
        Image img_center = c.drawGui();
        GenericComponentView piece = new GenericComponentView();
        Image img_c = piece.drawGui();

        for (Node node : root.getChildren()) {
            if (node instanceof ImageView imageView) {
                if(imageView.getId().equals("1")) {
                    imageView.setImage(img_s);
                } else if(imageView.getId().equals("2")) {
                    imageView.setImage(img_center);
                } else {
                    imageView.setImage(img_c);
                }
            }
        }

        stage.setTitle("Ship iniziale");
        stage.setScene(new Scene(root));
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
