package com.cofii.ts.game;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VG {
    
    private Stage stage = new Stage();

    public VG(ObservableList<ObservableList<Object>> data){
        try {
            FXMLLoader loader = new FXMLLoader(VG.class.getResource("/com/cofii/ts/game/VG.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(VG.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());

            VGController vgc = (VGController) loader.getController();
            vgc.setData(data);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
