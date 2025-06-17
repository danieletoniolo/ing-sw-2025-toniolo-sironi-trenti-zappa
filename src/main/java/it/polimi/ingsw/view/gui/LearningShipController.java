package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.miniModel.components.CabinView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;

public class LearningShipController {

    @FXML
    ImageView myImageView;
    Button myButton;

    CabinView cabin = new CabinView(155, new int[]{1, 1, 1, 1}, 0);
    //Image image = cabin.drawGui();

    public void displayImage(ActionEvent event) {
        //myImageView.setImage(image);
    }
}
