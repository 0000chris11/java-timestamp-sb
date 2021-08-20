package com.cofii.ts.login;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VF;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.RootConfigExist;
import com.cofii.ts.sql.querys.ShowUsers;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.mysql.DefaultConnection;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.RootConfigConnection;
import com.cofii2.mysql.enums.QueryResult;
import com.cofii2.xml.ResourceXML;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VL extends Application {

    private VLController vlc;
    private String option = "";

    private boolean tableUsersExist = false;
    private boolean tableUserDefaultsExist = false;
    private boolean tableDatabasesExist = false;

    // OLD QUERY REPLACE------------------------------------------
    private void selectUsers(QueryResult qr) {
        if (qr.getValue() == QueryResult.VALUES) {
            vlc.getTfUserAC().clearItems();
            ResultSet rs = qr.getResultSet();
            try {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String user = rs.getString(2);

                    Users.getInstance().addUser(new User(id, user));
                    vlc.getTfUserAC().addItem(user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else if (qr.getValue() == QueryResult.EXCEPTION) {
            qr.getSqlException().printStackTrace();
        }
    }

    private void selectTablesOnRootConfig(ResultSet rs, boolean rsValues, SQLException ex) throws SQLException {
        if (rsValues) {
            String tableName = rs.getString(1);

            if (tableName.equals(MSQL.TABLE_USERS)) {
                tableUsersExist = true;
            }
            if (tableName.equals(MSQL.TABLE_USER_DEFAULTS)) {
                tableUserDefaultsExist = true;
            }
            if (tableName.equals(MSQL.TABLE_DATABASES)) {
                tableDatabasesExist = true;
            }
        }
    }

    // INIT--------------------------------------------------------

    private void mainRootConfigTables() {
        MSQLP msInit = new MSQLP(new DefaultConnection());
        msInit.executeStringUpdate(MSQL.CREATE_DB_ROOTCONFIG);

        msInit.executeStringUpdate(MSQL.CREATE_TABLE_USERS);
        msInit.executeStringUpdate(MSQL.CREATE_TABLE_USERS_DEFAULTS);
        msInit.executeStringUpdate(MSQL.CREATE_TABLE_DATABASES);

        msInit.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("START");
        // ARGS-----------------------------------
        option = "";
        if (!getParameters().getRaw().isEmpty()) {
            option = getParameters().getRaw().get(0);
        }
        // CONTROLLER CLASS START--------------------
        FXMLLoader loader = new FXMLLoader(VL.class.getResource("/com/cofii/ts/login/VL.fxml"));
        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(VL.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
        stage.setScene(scene);
        // AFTER INIT-------------------------------------------------
        vlc = (VLController) loader.getController();
        // QUERYS----------------------------------
        mainRootConfigTables();

        MSQLP msRoot = new MSQLP(new RootConfigConnection());
        vlc.setMsRoot(msRoot);
        // SELECT USER-----------------------------------
        msRoot.selectData(MSQL.TABLE_USERS, this::selectUsers);
        // XML DEFAULTS READ --------------------------------
        // Users.getInstance().startDefaultProperty(msRoot);
        Users.readDefaultUser();
        if (Users.getDefaultUser() != null) {
            Users.getInstance().setCurrentUser(Users.getDefaultUser());
        }
        // DEFAULT USER------------------------
        boolean showStage = false;
        if (Users.getInstance().getCurrenUser() != null/* || option.equals("login") */) {
            // vlc.getMsRoot().selectUsers(new ShowUsers(vlc));
            msRoot.selectData(MSQL.TABLE_USERS, new ShowUsers(vlc));
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
