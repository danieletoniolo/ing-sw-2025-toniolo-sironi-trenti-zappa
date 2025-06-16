package it.polimi.ingsw.view.gui.controllers.cards;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import it.polimi.ingsw.view.miniModel.cards.AbandonedShipView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class AbandonedShipController implements MiniModelListener {
    private AbandonedShipView abandonedShipModel;

    @FXML
    private ImageView abandonedShipImage;

    public void setAbandonedShipModel(AbandonedShipView abandonedShipModel) {
        this.abandonedShipModel = abandonedShipModel;
        abandonedShipModel.addListener(this);

        abandonedShipImage.setOnDragDetected(event -> {
            Dragboard db = abandonedShipImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(abandonedShipModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(abandonedShipImage.snapshot(null, null));

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
        if (abandonedShipModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = abandonedShipModel.getID();
        String path = "/image/card/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        abandonedShipImage.setImage(image);
    }
}
