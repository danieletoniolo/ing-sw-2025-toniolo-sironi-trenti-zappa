package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import it.polimi.ingsw.view.miniModel.components.CabinView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class CabinController implements MiniModelListener {
    private CabinView cabinModel;

    @FXML
    private ImageView cabinImage;

    public void setCabinModel(CabinView cabinModel) {
        this.cabinModel = cabinModel;
        cabinModel.addListener(this);

        cabinImage.setOnDragDetected(event -> {
            Dragboard db = cabinImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(cabinModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(cabinImage.snapshot(null, null));

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
        if (cabinModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = cabinModel.getID();
        String path = "/image/tiles/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        cabinImage.setImage(image);
    }
}
