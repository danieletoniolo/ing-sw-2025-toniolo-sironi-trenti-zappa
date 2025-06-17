package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.EngineView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class EngineController implements MiniModelObserver {
    private EngineView engineModel;

    @FXML
    private ImageView engineImage;

    public void setEngineModel(EngineView engineModel) {
        this.engineModel = engineModel;
        engineModel.addListener(this);

        engineImage.setOnDragDetected(event -> {
            Dragboard db = engineImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(engineModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(engineImage.snapshot(null, null));

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
        if (engineModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = engineModel.getID();
        String path = "/image/tiles/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        engineImage.setImage(image);
    }
}
