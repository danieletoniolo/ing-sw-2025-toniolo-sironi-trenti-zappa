package it.polimi.ingsw.view.gui;

import javafx.stage.Stage;
import javafx.application.Application;

import java.io.IOException;

public class SpaceShipGui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        /*
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
                } else if(imageView.getId().equals("77")) {
                    imageView.setImage(img_center);
                } else {
                    imageView.setImage(img_c);
                }
            }
        }

        stage.setTitle("Ship iniziale");
        stage.setScene(new Scene(root));
        stage.show();
        */
    }

    public static void main(String[] args) {
        launch(args);
    }
}
