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
import com.cofii2.mysql.MSQLP;
import com.cofii2.xml.ResourceXML;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VF {

    private static VFController vfc;
    private VLController vlc;
    private VCController vc;

    private Stage stage = new Stage();
    private static MSQLP ms;

    // INSTANCES-------------------------------------
    private Menus menus;
    private Table currentTable;
    // private static ColumnS columns = ColumnS.getInstance();
    // private static ColumnDS columnsd = ColumnDS.getInstance();
    private Dist dist;
    // ZOOM----------------------------------------
    private DoubleProperty scaleVF = new SimpleDoubleProperty(1.0);

    /**
     * true if its start from the login (not from VC)
     */
    private boolean startFromLogin = true;
    private boolean noDatabases = false;

    private int defaultDatabaseId = 0;
    private int defaultTableId = 0;

    // STAGE LISTENERS -----------------------------------------
    private void stageMaximizedPropertyChange(boolean newValue) {
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
    private void querysStart() {
        // SELECT DATABASES FOR CURRENT USER-----
        ms.selectData(MSQL.TABLE_DATABASES, new SelectDatabases(this, vfc));
        // ----------------------------------------
        if (noDatabases) {
            vfc.getTfDatabaseAutoC().addItem(VFController.NO_DATABASES);
        } else {
            // DATABASES FOUND-----------------------------
            String resource = Users.getInstance().getDefaultResource();
            // READING XML
            new ResourceXML(resource, ResourceXML.READ_XML, doc -> {
                User currentUser = Users.getInstance().getCurrenUser();

                defaultDatabaseId = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("database").item(0)
                        .getAttributes().item(0).getTextContent());
                defaultTableId = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("table").item(0)
                        .getAttributes().item(0).getTextContent());

                Database database;
                if (defaultDatabaseId > 0) {
                    // DEFAULT DATABASE
                    database = currentUser.getDatabase(defaultDatabaseId);
                    currentUser.setDefaultDatabase(database);
                } else {
                    // FIRST ADDED DATABASE
                    database = currentUser.getDatabases().get(0);
                }

                ms.use(database.getName());
                currentUser.setCurrentDatabase(database);
                return null;// JUST READING
            });
            // TABLES ------------------------------------------
            // EXIST---------------------
            ms.selectTables(new CurrentDatabaseTablesExist());
            if (!MSQL.isTableNamesExist()) {
                ms.executeStringUpdate(MSQL.CREATE_TABLE_NAMES);// NOT TESTED
            }
            if (!MSQL.isTableConfigExist()) {
                ms.executeStringUpdate(MSQL.CREATE_TABLE_CONFIG);// NOT TESTED
            }

            // SELECT TABLES FROM CURRENT DATABASE
            menus.addMenuItemsReset();
            // DEFAULT & CURRENT TABLE
            Database currentDatabase = Users.getInstance().getCurrenUser().getCurrentDatabase();
            if (defaultTableId > 0) {
                currentTable = currentDatabase.getTable(defaultTableId);
                currentDatabase.setDefaultTable(currentTable);
            } else {
                currentTable = currentDatabase.getTables().get(0);
            }

            currentDatabase.setCurrentTable(currentTable);

            // TABLE SELECT-----------------------------------
            if (currentTable != null) {
                String table = currentTable.getName();
                vfc.getLbTable().setText(table);

                ms.selectColumns(table.replace(" ", "_"), new ShowColumns(vfc));
                ms.selectKeys(Users.getInstance().getCurrenUser().getDatabasesNames(), new SelectKeys(vfc));
                dist.distStart();

                ms.selectData(table.replace(" ", "_"), new SelectData(vfc, null));
            } else {
                vfc.clearCurrentTableView();
            }
        }
        /*
         * if (startFromLogin) { //ms.executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT, new
         * SelectTableDefault()); }
         */

        // OTHERS LISTENERS--------------------
        ms.setSQLException((ex, s) -> vfc.getLbStatus().setText(ex.getMessage(), NonCSS.TEXT_FILL_ERROR));
        //SHOW------------------------
        vlc.getStage().close();
        stage.show();
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
            vfc.setVl(vlc);

            ms = vlc.getMsRoot();
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

    public boolean isNoDatabases() {
        return noDatabases;
    }

    public void setNoDatabases(boolean noDatabases) {
        this.noDatabases = noDatabases;
    }
}
