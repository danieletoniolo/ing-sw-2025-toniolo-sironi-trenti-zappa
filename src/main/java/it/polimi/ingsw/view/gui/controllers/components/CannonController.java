package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.CannonView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class CannonController implements MiniModelObserver {
    private CannonView cannonModel;

    @FXML
    private ImageView cannonImage;

    public void setCannonModel(CannonView cannonModel) {
        this.cannonModel = cannonModel;
        cannonModel.addListener(this);

        cannonImage.setOnDragDetected(event -> {
            Dragboard db = cannonImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(cannonModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(cannonImage.snapshot(null, null));

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
        if (cannonModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = cannonModel.getID();
        String path = "/image/tiles/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        cannonImage.setImage(image);
    }
}
