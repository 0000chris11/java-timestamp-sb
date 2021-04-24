package com.cofii.ts.login;

import com.cofii.ts.first.VF;
import com.cofii.ts.sql.MSQL;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VL.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        //AFTER INIT
        VLController controller = (VLController)loader.getController();
        //controller.sceneKR(null);
        //scene.setOnKeyReleased(controller::sceneKR); //FIND A SOLUTION

        controller.setStage(stage);
        System.out.println("\nVL");
        System.out.println(controller.getStage() == null ? "controller.getStage() NULL" : "controller.getStage() NOT NULL");
        if (!MSQL.getUser().equals("NONE")) {
            new VF(controller);
        }
        if(controller.isShowStage()){
            stage.show();
        }
    }
    //--------------------------------------------------
}
