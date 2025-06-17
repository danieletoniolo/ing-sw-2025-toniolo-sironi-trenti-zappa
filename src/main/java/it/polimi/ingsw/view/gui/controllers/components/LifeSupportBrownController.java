package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.LifeSupportBrownView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class LifeSupportBrownController implements MiniModelObserver {
    private LifeSupportBrownView lifeSupportBrownModel;

    @FXML
    private ImageView lifeSupportBrownImage;

    public void setStorageModel(LifeSupportBrownView lifeSupportBrownModel) {
        this.lifeSupportBrownModel = lifeSupportBrownModel;
        lifeSupportBrownModel.addListener(this);
        // Update the image based on the storage model

        lifeSupportBrownImage.setOnDragDetected(event -> {
            Dragboard db = lifeSupportBrownImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(lifeSupportBrownModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(lifeSupportBrownImage.snapshot(null, null));

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
        if (lifeSupportBrownModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = lifeSupportBrownModel.getID();
        String path = "/image/tiles/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        lifeSupportBrownImage.setImage(image);
    }
}
