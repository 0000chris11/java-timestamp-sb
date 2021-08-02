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

        scene.getStylesheets().add(VL.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
        stage.setScene(scene);

        // AFTER INIT
        VLController controller = (VLController) loader.getController();
        // controller.sceneKR(null);
        // scene.setOnKeyReleased(controller::sceneKR); //FIND A SOLUTION

        controller.setStage(stage);
        String option = "";
        if (!getParameters().getRaw().isEmpty()) {
            option = getParameters().getRaw().get(0);
        }
        if (!MSQL.getUser().equals("NONE") && !option.equals("login")) {
            new VF(controller);
        } else if (controller.isShowStage() || option.equals("login")) {
            stage.show();
        }
    }
    // --------------------------------------------------
}
