package com.cofii.ts.first;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VFD {

    private Stage stage = new Stage();

    public VFD(VFController vf) {
        try {
            FXMLLoader loader = new FXMLLoader(VF.class.getResource("/com/cofii/ts/first/VFD.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(VF.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            VFDController vfd = loader.getController();
            vfd.setVf(vf);

            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
