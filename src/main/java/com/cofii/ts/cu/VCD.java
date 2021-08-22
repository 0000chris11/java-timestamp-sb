package com.cofii.ts.cu;

import java.io.IOException;

import com.cofii.ts.first.VFController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VCD {

    private final Stage stage = new Stage();

    public VCD(VFController vfc) {
        try {
            FXMLLoader loader = new FXMLLoader(VCD.class.getResource("/com/cofii/ts/cu/VCD.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(VCD.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            stage.setScene(scene);

            VCDController vcd = (VCDController) loader.getController();
            vcd.setVfc(vfc);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
