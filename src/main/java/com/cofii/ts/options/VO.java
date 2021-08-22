package com.cofii.ts.options;

import java.io.IOException;

import com.cofii.ts.first.VFController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VO {

    private Stage stage = new Stage();

    public VO(VFController vfc) {
        try {
            FXMLLoader loader = new FXMLLoader(VO.class.getResource("/com/cofii/ts/options/VO.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(VO.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            stage.setScene(scene);

            VOController voc = (VOController) loader.getController();
            voc.setVfc(vfc);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
