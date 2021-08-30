package com.cofii.ts.cu;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VImageC {

    private Stage stage = new Stage();

    public VImageC(boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VImageC.class.getResource("/com/cofii/ts/cu/VImageC.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(VImageC.class.getResource("/com/cofii/ts/cu/VF.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
