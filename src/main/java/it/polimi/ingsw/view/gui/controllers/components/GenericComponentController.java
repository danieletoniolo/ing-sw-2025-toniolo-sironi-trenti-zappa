package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import it.polimi.ingsw.view.miniModel.components.GenericComponentView;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class GenericComponentController implements MiniModelListener {
    private GenericComponentView genericComponentModel;

    @FXML
    private ImageView genericComponentImage;

    public void setGenericComponentModel(GenericComponentView genericComponentModel) {
        this.genericComponentModel = genericComponentModel;
        genericComponentModel.addListener(this);

        genericComponentImage.setOnDragDetected(event -> {
            Dragboard db = genericComponentImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(genericComponentModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(genericComponentImage.snapshot(null, null));

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
        if (genericComponentModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        String path = "/image/tiles/covered.jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        genericComponentImage.setImage(image);
    }
}
