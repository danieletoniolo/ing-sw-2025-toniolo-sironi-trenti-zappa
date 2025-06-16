package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import it.polimi.ingsw.view.miniModel.components.LifeSupportBrownView;
import it.polimi.ingsw.view.miniModel.components.LifeSupportPurpleView;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class LifeSupportPurpleController implements MiniModelListener {
    private LifeSupportPurpleView lifeSupportPurpleModel;

    @FXML
    private ImageView lifeSupportPurpleImage;

    public void setLifeSupportPurpleModel(LifeSupportPurpleView lifeSupportPurpleModel) {
        this.lifeSupportPurpleModel = lifeSupportPurpleModel;
        lifeSupportPurpleModel.addListener(this);
        // Update the image based on the storage model

        lifeSupportPurpleImage.setOnDragDetected(event -> {
            Dragboard db = lifeSupportPurpleImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(lifeSupportPurpleModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(lifeSupportPurpleImage.snapshot(null, null));

            // IMPORTANTE: salviamo il nodo sorgente
            db.setDragViewOffsetX(event.getX());
            db.setDragViewOffsetY(event.getY());

            event.consume();
        });

        updateView();

    }

    @Override
    public void onModelChanged() {
        updateView();
    }

    private void updateView() {
        if (lifeSupportPurpleModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = lifeSupportPurpleModel.getID();
        String path = "/image/tiles/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        lifeSupportPurpleImage.setImage(image);
    }
}
