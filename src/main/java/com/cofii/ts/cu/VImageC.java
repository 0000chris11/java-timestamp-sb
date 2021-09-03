package com.cofii.ts.cu;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VImageC {

    private static Stage stage = new Stage();
    private VImageCController vicc;

    private static VImageC instance;
    public static VImageC getInstance(VCController vc, boolean create){
        if(instance == null){
            instance = new VImageC(vc, create);
        }else{
            stage.show();
        }
        return instance;
    }

    private VImageC(VCController vc, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VImageC.class.getResource("/com/cofii/ts/cu/VImageC.fxml"));
            Scene scene = new Scene(loader.load());

            vicc = (VImageCController) loader.getController();
            vicc.setStage(stage);

            scene.getStylesheets().add(VImageC.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            stage.setScene(scene);
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

    
}
