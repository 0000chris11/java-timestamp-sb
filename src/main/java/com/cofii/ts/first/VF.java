package com.cofii.ts.first;

import java.io.IOException;

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
import com.cofii.ts.sql.querys.ShowTableCurrentDB;
import com.cofii.ts.store.main.Database;
import com.cofii2.components.javafx.SceneZoom;
import com.cofii2.mysql.MSQLP;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VF {

    private static VFController vf;
    private VLController vl;
    private VCController vc;

    private Stage stage = new Stage();
    private static MSQLP ms;

    //INSTANCES-------------------------------------
    private Database tables = Database.getInstance();
    private Menus menus;
    // private static ColumnS columns = ColumnS.getInstance();
    // private static ColumnDS columnsd = ColumnDS.getInstance();
    private Dist dist;
    //ZOOM----------------------------------------
    private DoubleProperty scaleVF = new SimpleDoubleProperty(1.0);

    /**
     * true if its start from the login (not from VC)
     */
    private boolean startFromLogin = true;
    private boolean noDatabases = false;

    // STAGE LISTENERS -----------------------------------------
    private void stageMaximizedPropertyChange(boolean newValue) {
        if (newValue) {
            /*
             * if (Arrays.asList(columnsd.getImageCS()).stream().allMatch(s ->
             * s.equals("No"))) { vf.getSplitLeft().setDividerPositions(1.0); }
             */
            if (MSQL.getCurrentTable() != null) {
                if (MSQL.getCurrentTable().getImageC().equals("NONE")) {
                    vf.getSplitLeft().setDividerPositions(1.0);
                }
            }
        }
    }

    private void heightPropertyChangeListener() {
        /*
         * if (Arrays.asList(columnsd.getImageCS()).stream().allMatch(s ->
         * s.equals("No"))) { vf.getSplitLeft().setDividerPositions(1.0); }
         */
        if (MSQL.getCurrentTable() != null) {
            if (MSQL.getCurrentTable().getImageC().equals("NONE")) {
                vf.getSplitLeft().setDividerPositions(1.0);
            }
        }
    }

    // INIT-----------------------------------------
    private void querysStart() {
        //SELECT DATABASES FOR CURRENT USER
        ms.selectData(MSQL.TABLE_DATABASES, new SelectDatabases(this));
        if(noDatabases){

        }
        //ms.use(database);

        ms.selectTables(new ShowTableCurrentDB());
        if (!MSQL.isTableNamesExist()) {
            ms.executeStringUpdate(MSQL.CREATE_TABLE_NAMES);// NOT TESTED
        }
        if (!MSQL.isTableDefaultExist()) {
            ms.executeStringUpdate(MSQL.CREATE_TABLE_DEFAUT);// NOT TESTED
        }
        if (!MSQL.isTableConfigExist()) {
            ms.executeStringUpdate(MSQL.CREATE_TABLE_CONFIG);// NOT TESTED
        }
        // TABLE LIST
        // addMenuItems();
        menus.addMenuItemsReset();

        if (startFromLogin) {
            ms.executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT, new SelectTableDefault());
        }
        if (MSQL.getCurrentTable() != null) {
            String table = MSQL.getCurrentTable().getName();
            vf.getLbTable().setText(table);

            ms.selectColumns(table.replace(" ", "_"), new ShowColumns(vf));
            ms.selectKeys(MSQL.getDatabases(), new SelectKeys(vf));
            dist.distStart();

            ms.selectData(table.replace(" ", "_"), new SelectData(vf, null));
        } else {
            vf.clearCurrentTableView();
        }
        // OTHERS LISTENERS--------------------
        ms.setSQLException((ex, s) -> vf.getLbStatus().setText(ex.getMessage(), NonCSS.TEXT_FILL_ERROR));
    }

    private void init() {
        try {
            FXMLLoader loader = new FXMLLoader(VF.class.getResource("/com/cofii/ts/first/VF.fxml"));
            // ZOOMIMING PANE-----------------------
            SceneZoom sceneZoom = new SceneZoom(loader.load(), scaleVF);
            vf = (VFController) loader.getController();
            sceneZoom.setParent(vf.getBpMain());
            // ------------------------------------
            Scene scene = sceneZoom.getScene();
            scene.getStylesheets().add(VF.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            // START OR GO BACK OPTION-----------------------------
            if (vl != null) {// NEW WINDOW
                stage.setScene(scene);
            } else {
                vc.getVf().getStage().setScene(scene);
                startFromLogin = false;
            }
            // MENU START-------------------------------------
            Menus.clearInstance();
            menus = Menus.getInstance(vf);
            // STAGE LISTENER-------------------------------------------------
            stage.maximizedProperty().addListener((obs, oldValue, newValue) -> stageMaximizedPropertyChange(newValue));
            stage.heightProperty().addListener((obs, oldValue, newValue) -> heightPropertyChangeListener());
            // SOME SETTERS TO VFCONTROLLER-------------------------------------------------
            vf.setStage(vl != null ? stage : vc.getVf().getStage());
            vf.setScene(scene);
            vf.setVl(vl);

            ms = vl.getMsRoot();
            vf.setMs(ms);
            // DIST START---------------------------------
            dist = Dist.getInstance(vf);
            // -------------------------------------------
            querysStart();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VF(VLController vl) {
        this.vl = vl;
        vc = null;
        init();
    }

    public VF(VCController vc) {
        this.vc = vc;
        vl = null;
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
