package com.cofii.ts.cu.imagec;

import java.io.IOException;

import com.cofii.ts.cu.VCController;
import com.cofii.ts.sql.MSQL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VImageC {

    private static Stage stage = new Stage();
    private static VImageCController vicc;
    private static String[] columns;

    private static VImageC instance;

    public static VImageC getInstance(VCController vc, boolean create) {
        if (instance == null) {
            instance = new VImageC(vc, create);
        } else {
            stage.show();
        }
        // ADDING COLUMNS-------------------------------
        if (create) {
            int columnsLength = vc.getCurrentRowLength();
            columns = vc.getTfsColumn().stream().map(TextField::getText).limit(columnsLength)
                    .toArray(size -> new String[size]);
            if (vicc != null) {
                vicc.getCbColumnSelect().getItems().addAll(columns);
                vicc.getCbColumnSelect().getSelectionModel().select(0);
            }
        }
        return instance;
    }

    private VImageC(VCController vc, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VImageC.class.getResource("/com/cofii/ts/cu/imagec/VImageC.fxml"));
            Scene scene = new Scene(loader.load());

            vicc = (VImageCController) loader.getController();
            vicc.setStage(stage);

            scene.getStylesheets().add(VImageC.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            stage.setScene(scene);
            // AFTER CONTROLLER INIT---------------------------------
            vicc.getCbDisplayOrder().getItems().addAll("Ascended", "Random");
            vicc.getCbType().getItems().addAll("File", "Folder", "All-Sub-Files");

            if (create) {
                vicc.getTfNumberImageC().setText(Integer.toString(MSQL.DEFAULT_IMAGES_LENGTH));

                vicc.getCbDisplayOrder().getSelectionModel().select(0);
                vicc.getCbType().getSelectionModel().select(0);
                //LISTENERS-----------------------------
                vicc.getBtnSaveUpdate().setOnAction(vicc::btnSaveCreateAction);
            }else{
                //LISTENERS-----------------------------
                vicc.getBtnSaveUpdate().setOnAction(vicc::btnSaveUpdateAction);
            }
            // ------------------------------------------------------
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public VImageCController getVicc() {
        return vicc;
    }

    public void setVicc(VImageCController vicc) {
        this.vicc = vicc;
    }

    public static void setInstance(VImageC instance) {
        VImageC.instance = instance;
    }

}
