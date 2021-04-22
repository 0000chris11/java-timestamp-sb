package com.cofii.ts.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VL extends Application{

    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //System.out.println(FXMLLoader.load(getClass().getResource("VL.fxml")).getClass());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VL.fxml"));
        stage.setScene(new Scene(loader.load()));
        
        VLController controller = (VLController)loader.getController();
        controller.setStage(stage);
        if(controller.isShowStage()){
            stage.show();
        }
    }
    //--------------------------------------------------
}
