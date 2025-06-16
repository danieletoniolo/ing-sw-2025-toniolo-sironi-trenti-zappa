package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import it.polimi.ingsw.view.miniModel.components.ConnectorsView;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class ConnectorsController implements MiniModelListener {
    private ConnectorsView connectorsModel;

    @FXML
    private ImageView connectorsImage;

    public void setConnectorsModel(ConnectorsView connectorsModel) {
        this.connectorsModel = connectorsModel;
        connectorsModel.addListener(this);
        // Update the image based on the storage model

        connectorsImage.setOnDragDetected(event -> {
            Dragboard db = connectorsImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(connectorsModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(connectorsImage.snapshot(null, null));

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
        if (connectorsModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = connectorsModel.getID();
        String path = "/image/tiles/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        connectorsImage.setImage(image);
    }
}
