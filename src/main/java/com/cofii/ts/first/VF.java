package com.cofii.ts.first;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collector;

import com.cofii.ts.cu.VCController;
import com.cofii.ts.login.VLController;
import com.cofii.ts.other.Dist;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.sql.querys.SelectDatabases;
import com.cofii.ts.sql.querys.SelectKeys;
import com.cofii.ts.sql.querys.SelectTableDefault;
import com.cofii.ts.sql.querys.ShowColumns;
import com.cofii.ts.sql.querys.CurrentDatabaseTablesExist;
import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.SceneZoom;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.mysql.MSQLP;
import com.cofii2.xml.ResourceXML;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VF {

    private static VFController vfc;
    private VLController vlc;
    private VCController vc;

    private Stage stage = new Stage();
    private static MSQLP ms;

    // INSTANCES-------------------------------------
    private Menus menus;
    // private static ColumnS columns = ColumnS.getInstance();
    // private static ColumnDS columnsd = ColumnDS.getInstance();
    private Dist dist;
    // ZOOM----------------------------------------
    private DoubleProperty scaleVF = new SimpleDoubleProperty(1.0);

    /**
     * true if its start from the login (not from VC)
     */
    private boolean startFromLogin = true;

    private boolean noDatabasesForCurrentUser = false;
    private boolean noTablesForCurrentDatabase = false;

    // STAGE LISTENERS -----------------------------------------
    private void stageMaximizedPropertyChange(boolean newValue) {
        Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        if (newValue) {
            /*
             * if (Arrays.asList(columnsd.getImageCS()).stream().allMatch(s ->
             * s.equals("No"))) { vf.getSplitLeft().setDividerPositions(1.0); }
             */
            if (currentTable != null) {
                if (currentTable.getImageC().equals("NONE")) {
                    vfc.getSplitLeft().setDividerPositions(1.0);
                }
            }
        }
    }

    private void heightPropertyChangeListener() {
        Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        /*
         * if (Arrays.asList(columnsd.getImageCS()).stream().allMatch(s ->
         * s.equals("No"))) { vf.getSplitLeft().setDividerPositions(1.0); }
         */
        if (currentTable != null) {
            if (currentTable.getImageC().equals("NONE")) {
                vfc.getSplitLeft().setDividerPositions(1.0);
            }
        }
    }

    // INIT-----------------------------------------
    /**
     * Does main tables exist (at selected Database level)
     */
    void mainTablesCreation() {
        // ms.selectTables(new CurrentDatabaseTablesExist());
        ms.executeStringUpdate(MSQL.CREATE_TABLE_NAMES);
        ms.executeStringUpdate(MSQL.CREATE_TABLE_CONFIG);
    }

    private void querysStart() {
        // SELECT DATABASES FOR CURRENT USER-----
        ms.selectData(MSQL.TABLE_DATABASES, new SelectDatabases(vfc));
        // ----------------------------------------
        if (!noDatabasesForCurrentUser) {
            // User.startDefaultDatabaseProperty();
            Users users = Users.getInstance();
            if (startFromLogin) {
                User.readDefaultDatabase();
                users.getCurrenUser().setCurrentDatabase(User.getDefaultDatabase());

                ms.use(users.getCurrenUser().getCurrentDatabase().getName());

                User.readDefaultOptions();
            }

            String databaseName = users.getCurrenUser().getCurrentDatabase().getName();
            vfc.getTfDatabase().setText(databaseName);
            vfc.getTfDatabaseAutoC().getDisableItems().add(databaseName);

            // TABLES ------------------------------------------
            // EXIST---------------------
            if (startFromLogin) {
                mainTablesCreation();
            }

            // SELECT TABLES FROM CURRENT DATABASE
            menus.addTablesToTfTableReset(vfc);

            // DEFAULT & CURRENT TABLE
            Database currentDatabase = users.getCurrenUser().getCurrentDatabase();
            Database.readDefaultTable();
            if (!noTablesForCurrentDatabase) {
                if (startFromLogin) {
                    if (Database.getDefaultTable() != null) {
                        currentDatabase.setCurrentTable(Database.getDefaultTable());
                    } else {
                        currentDatabase.setCurrentTable(Database.getTables().get(0));
                    }
                }
                String tableName = currentDatabase.getCurrentTable().getName();
                vfc.getTfTableAutoC().getDisableItems().add(tableName);
                vfc.getTfTable().setText(tableName);
            }
            // TABLE SELECT-----------------------------------
            if (currentDatabase.getCurrentTable() != null) {
                String tableName = currentDatabase.getCurrentTable().getName();
                vfc.selectionForEachTable(tableName);
                /*
                vfc.getLbDatabaseTable().setText(databaseName + "." + tableName);
                vfc.getLbDatabaseTable().setTooltip(new Tooltip(vfc.getLbDatabaseTable().getText()));
                vfc.getLbDatabaseTable().getTooltip().setShowDelay(Duration.ZERO);

                ms.selectColumns(tableName.replace(" ", "_"), new ShowColumns(vfc));
                ms.selectKeys(Users.getInstance().getCurrenUser().getDatabasesNames(), new SelectKeys(vfc));
                dist.distStart();
                ms.selectData(tableName.replace(" ", "_"), new SelectData(vfc, null));
                */
            } else {
                vfc.clearCurrentTableView();
            }
        } else {
            vfc.getTfDatabase().setPromptText(VFController.NO_DATABASES);
            vfc.getTfTable().setPromptText(VFController.NO_DATABASE_SELECTED);
        }
        /*
         * if (startFromLogin) { //ms.executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT, new
         * SelectTableDefault()); }
         */

        // OTHERS LISTENERS--------------------
        ms.setSQLException((ex, s) -> vfc.getLbStatus().setText(ex.getMessage(), NonCSS.TEXT_FILL_ERROR));
        // SHOW------------------------
        if (startFromLogin) {
            vlc.getStage().close();
            stage.show();

        }
    }

    private void init() {
        try {
            FXMLLoader loader = new FXMLLoader(VF.class.getResource("/com/cofii/ts/first/VF.fxml"));
            // ZOOMIMING PANE-----------------------
            SceneZoom sceneZoom = new SceneZoom(loader.load(), scaleVF);
            vfc = (VFController) loader.getController();
            sceneZoom.setParent(vfc.getBpMain());
            // ------------------------------------
            Scene scene = sceneZoom.getScene();
            scene.getStylesheets().add(VF.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            // START OR GO BACK OPTION-----------------------------
            if (vlc != null) {// NEW WINDOW
                stage.setScene(scene);
            } else {
                vc.getVf().getStage().setScene(scene);
                startFromLogin = false;
            }
            // MENU START-------------------------------------
            Menus.clearInstance();
            menus = Menus.getInstance(vfc);
            // STAGE LISTENER-------------------------------------------------
            stage.maximizedProperty().addListener((obs, oldValue, newValue) -> stageMaximizedPropertyChange(newValue));
            stage.heightProperty().addListener((obs, oldValue, newValue) -> heightPropertyChangeListener());
            // SOME SETTERS TO VFCONTROLLER-------------------------------------------------
            vfc.setStage(vlc != null ? stage : vc.getVf().getStage());
            vfc.setScene(scene);
            if (startFromLogin) {
                vfc.setVl(vlc);
            }
            vfc.setVf(this);

            if (startFromLogin) {
                ms = vlc.getMsRoot();
            }
            vfc.setMs(ms);
            // DIST START---------------------------------
            dist = Dist.getInstance(vfc);
            // -------------------------------------------
            querysStart();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VF(VLController vlc) {
        this.vlc = vlc;
        vc = null;
        init();
    }

    public VF(VCController vc) {
        this.vc = vc;
        vlc = null;
        init();
    }

    // GETTER & SETTERS -------------------------------------------
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean isNoDatabasesForCurrentUser() {
        return noDatabasesForCurrentUser;
    }

    public void setnoDatabasesForCurrentUser(boolean noDatabasesForCurrentUser) {
        this.noDatabasesForCurrentUser = noDatabasesForCurrentUser;
    }

    public boolean isNoTablesForCurrentDatabase() {
        return noTablesForCurrentDatabase;
    }

    public void setNoTablesForCurrentDatabase(boolean noTablesForCurrentDatabase) {
        this.noTablesForCurrentDatabase = noTablesForCurrentDatabase;
    }

}
