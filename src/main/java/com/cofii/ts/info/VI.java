package com.cofii.ts.info;

import java.io.IOException;

import com.cofii.ts.first.VFController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VI {

    private Stage stage = new Stage();

    public VI(VFController vf) {
        try {
            FXMLLoader loader = new FXMLLoader(VI.class.getResource("/com/cofii/ts/info/VI.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(VI.class.getResource("/com/cofii/ts/info/VI.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // scene.getStylesheets().add(VF.class.getResource("/VI.css").toExternalForm());
    }
}
