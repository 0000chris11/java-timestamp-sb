package com.cofii.ts.login;

import com.cofii.ts.first.VF;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDefaultUser;
import com.cofii.ts.sql.querys.ShowUsers;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.xml.ResourceXML;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VL extends Application {

    private String option = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("START");
        //ARGS-----------------------------------
        option = "";
        if (!getParameters().getRaw().isEmpty()) {
            option = getParameters().getRaw().get(0);
        }
        //CONTROLLER CLASS START--------------------
        FXMLLoader loader = new FXMLLoader(VL.class.getResource("/com/cofii/ts/login/VL.fxml"));
        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(VL.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
        stage.setScene(scene);

        // AFTER INIT-------------------------------------------------
        VLController vlc = (VLController) loader.getController();
        // QUERYS----------------------------------
        boolean showStage = false;
        // XML DEFAULTS READ --------------------------------
        String resource = Users.getInstance().getDefaultResource();
        new ResourceXML(resource, ResourceXML.READ_XML, doc -> {
            int defaultUserId = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("user").item(0)
                    .getAttributes().item(0).getTextContent());

            if (defaultUserId > 0) {
                User user = Users.getInstance().getUser(defaultUserId);
                Users.getInstance().setDefaultUser(user);
                Users.getInstance().setCurrenUser(user);
            } 
            return null;
        });
        // DEFAULT USER------------------------
        // vlc.getMsRoot().executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT_USER, new
        // SelectDefaultUser());
        if (Users.getInstance().getCurrenUser() != null/* || option.equals("login") */) {
            // vlc.getMsRoot().selectUsers(new ShowUsers(vlc));
            vlc.getMsRoot().selectData(MSQL.TABLE_USERS, new ShowUsers(vlc));
            showStage = true;
        }
        // ----------------------------------------
        vlc.setStage(stage);
        if (showStage /*&& !option.equals("login")*/) {
            new VF(vlc);
        } else if (!showStage/* || option.equals("login")*/) {
            stage.show();
        }
    }

}
