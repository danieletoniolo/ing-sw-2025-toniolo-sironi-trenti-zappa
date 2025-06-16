package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import it.polimi.ingsw.view.miniModel.components.BatteryView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class BatteryController implements MiniModelListener {
    private BatteryView batteryModel;

    @FXML
    private ImageView batteryImage;

    public void setBatteryModel(BatteryView batteryModel) {
        this.batteryModel = batteryModel;
        batteryModel.addListener(this);

        batteryImage.setOnDragDetected(event -> {
            Dragboard db = batteryImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Passa anche l'ID
            content.putString(String.valueOf(batteryModel.getID()));
            db.setContent(content);

            // Metti un'immagine drag
            db.setDragView(batteryImage.snapshot(null, null));

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
        if (batteryModel == null) return;

        // Prendi l'ID dal MiniModel e carica l'immagine
        int id = batteryModel.getID();
        String path = "/image/tiles/" + id + ".jpg";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        batteryImage.setImage(image);
    }
}
