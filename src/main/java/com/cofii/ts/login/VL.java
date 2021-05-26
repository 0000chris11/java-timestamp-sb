package com.cofii.ts.login;

import com.cofii.ts.first.VF;
import com.cofii.ts.sql.MSQL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VL extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("START");
        FXMLLoader loader = new FXMLLoader(VL.class.getResource("/com/cofii/ts/login/VL.fxml"));
        Scene scene = new Scene(loader.load());
        
        scene.getStylesheets().add(VL.class.getResource("/com/cofii/ts/login/VL.css").toExternalForm());
        stage.setScene(scene);

        
        //AFTER INIT
        VLController controller = (VLController)loader.getController();
        //controller.sceneKR(null);
        //scene.setOnKeyReleased(controller::sceneKR); //FIND A SOLUTION

        controller.setStage(stage);
        if (!MSQL.getUser().equals("NONE")) {
            new VF(controller);
        }
        if(controller.isShowStage()){
            stage.show();
        }
    }
    // --------------------------------------------------
}
