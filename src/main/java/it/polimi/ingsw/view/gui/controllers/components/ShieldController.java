package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import it.polimi.ingsw.view.miniModel.components.ShieldView;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class ShieldController implements MiniModelListener {
    private ShieldView shieldModel;

    @FXML
    private ImageView shieldImage;

    public void setShieldModel(ShieldView shieldModel) {
        this.shieldModel = shieldModel;
        shieldModel.addListener(this);
        // Update the image based on the storage model

        shieldImage.setOnDragDetected(event -> {
            Dragboard db = shieldImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(shieldModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(shieldImage.snapshot(null, null));

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
        if (shieldModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = shieldModel.getID();
        String path = "/image/tiles/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        shieldImage.setImage(image);
    }
}
