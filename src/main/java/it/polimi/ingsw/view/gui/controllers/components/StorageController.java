package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class StorageController implements MiniModelListener {
    // Info about the storage in the modelView
    private StorageView storageModel;

    @FXML
    private ImageView storageImage;

    public void setStorageModel(StorageView storageModel) {
        this.storageModel = storageModel;
        storageModel.addListener(this);
        // Update the image based on the storage model

        storageImage.setOnDragDetected(event -> {
            Dragboard db = storageImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(storageModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(storageImage.snapshot(null, null));

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
        if (storageModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = storageModel.getID();
        String path = "/image/tiles/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        storageImage.setImage(image);
    }
}
