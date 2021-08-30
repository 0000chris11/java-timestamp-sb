package com.cofii.ts.login;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VF;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.ShowUsers;
import com.cofii.ts.store.main.Option;
import com.cofii.ts.store.main.Options;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.mysql.DefaultConnection;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.RootConfigConnection;
import com.cofii2.mysql.enums.QueryResult;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VL extends Application {

    private VLController vlc;
    private String optionArgs = "";

    // QUERY------------------------------------------
    private void selectUsers(ResultSet rs, boolean rsValues, SQLException ex) throws SQLException {
        if (rsValues) {
            vlc.getTfUserAC().clearItems();

            int id = rs.getInt(1);
            String user = rs.getString(2);

            Users.getInstance().addUser(new User(id, user));
            vlc.getTfUserAC().addItem(user);

        } else if (ex != null) {
            ex.printStackTrace();
        }
    }

    private void selectOptions(ResultSet rs, boolean rsValues, SQLException ex) throws SQLException{
        if(rsValues){
            int id = rs.getInt(1);
            String optionName = rs.getString(2);
            String defaultValue = rs.getString(3);

            Option option = new Option(id, optionName, defaultValue);
            option.setDefaultValue(defaultValue);
            Options.getInstance().getOptionList().add(option);
        }else{
            //NO OPTIONS ACTION
        }
    }
    // INIT--------------------------------------------------------
    private void mainRootConfigTablesCreation() {
        MSQLP msInit = new MSQLP(new DefaultConnection());// FROM mysql DATABASE
        msInit.executeStringUpdate(MSQL.CREATE_DB_ROOTCONFIG);

        msInit.executeStringUpdate(MSQL.CREATE_TABLE_USERS);

        msInit.executeStringUpdate(MSQL.CREATE_TABLE_DEFAULT_USER);
        msInit.executeStringUpdate(MSQL.CREATE_TABLE_DATABASES);
        msInit.executeStringUpdate(MSQL.CREATE_TABLE_USERS_DEFAULTS);

        msInit.executeStringUpdate(MSQL.CREATE_TABLE_USER_OPTIONS);
        msInit.executeStringUpdate(MSQL.CREATE_TABLE_USER_DEFAULTS_OPTIONS);

        msInit.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("START");
        // ARGS-----------------------------------
        optionArgs = "";
        if (!getParameters().getRaw().isEmpty()) {
            optionArgs = getParameters().getRaw().get(0);
        }
        // CONTROLLER CLASS START--------------------
        FXMLLoader loader = new FXMLLoader(VL.class.getResource("/com/cofii/ts/login/VL.fxml"));
        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(VL.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
        stage.setScene(scene);
        // AFTER INIT-------------------------------------------------
        vlc = (VLController) loader.getController();
        // QUERYS----------------------------------
        mainRootConfigTablesCreation();
        MSQLP msRoot = new MSQLP(new RootConfigConnection());
        vlc.setMsRoot(msRoot);
        // SELECT USER-----------------------------------
        msRoot.selectData(MSQL.TABLE_USERS, this::selectUsers);
        // SELECT OPTIONS--------------------------------
        msRoot.selectData(MSQL.TABLE_USER_OPTIONS, this::selectOptions);
        // DEFAULT USER --------------------------------
        Object[] defaultUserArray = msRoot.selectValues(MSQL.TABLE_DEFAULT_USER, "id_user", 1);
        if (defaultUserArray.length > 0) {
            User defaultUser = Users.getUser((int) defaultUserArray[0]);
            Users.setDefaultUser(defaultUser);
            Users.getInstance().setCurrentUser(defaultUser);
        }
        // ------------------------
        boolean showStage = false;
        if (Users.getInstance().getCurrenUser() != null/* || option.equals("login") */) {
            showStage = true;
        }
        // ----------------------------------------
        vlc.setStage(stage);
        if (showStage /* && !option.equals("login") */) {
            new VF(vlc);
        } else if (!showStage/* || option.equals("login") */) {
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
