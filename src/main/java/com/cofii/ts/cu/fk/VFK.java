package com.cofii.ts.cu.fk;

import java.io.IOException;

import com.cofii.ts.cu.VCController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VFK {

    private static Stage stage = new Stage();
    private static String[] columns;
    private static VFKController vfkc;
    // INSTANCE =================================================================
    private static VFK instance;

    public static VFK getInstance(VCController vc, boolean create) {
        if (instance == null) {
            instance = new VFK(vc, create);
        } else {
            stage.show();
        }
        // ADDING COLUMNS-------------------------------
        if (create) {
            int columnsLength = vc.getCurrentRowLength();
            columns = vc.getTfsColumn().stream().map(TextField::getText).limit(columnsLength)
                    .toArray(size -> new String[size]);
            if (vfkc != null) {
                // RE-ADD FLOW PANE'S CHILDRENS
            }
        }
        return instance;
    }

    // CONSTRUCTOR ================================================
    private VFK(VCController vc, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VFK.class.getResource("/com/cofii/ts/cu/fk/FKV.fxml"));

            Scene scene = new Scene(loader.load());

            vfkc = (VFKController) loader.getController();
            vfkc.setStage(stage);

            scene.getStylesheets().add(VFK.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            stage.setScene(scene);
            // AFTER CONTROLLER INIT ---------------------------------
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
