package com.cofii.ts.first;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.cu.VCController;
import com.cofii.ts.login.VLController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.Dist;
import com.cofii.ts.sql.CurrenConnection;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.WrongPassword;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.sql.querys.SelectDistinct;
import com.cofii.ts.sql.querys.SelectKeys;
import com.cofii.ts.sql.querys.SelectTableDefault;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowColumns;
import com.cofii.ts.sql.querys.ShowTableCurrentDB;
import com.cofii.ts.store.ColumnD;
import com.cofii.ts.store.ColumnDS;
import com.cofii.ts.store.ColumnS;
import com.cofii.ts.store.TableS;
import com.cofii2.mysql.MSQLP;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import com.cofii2.components.javafx.ZoomingPane;

public class VF {

    private static VFController vf;
    private VLController vl;
    private VCController vc;

    private Stage stage = new Stage();
    private static MSQLP ms;

    private TableS tables = TableS.getInstance();
    private Menus menus;
    private static ColumnS columns = ColumnS.getInstance();
    private static ColumnDS columnsd = ColumnDS.getInstance();
    private Dist dist;

    private DoubleProperty scaleVF = new SimpleDoubleProperty(1.0);

    // -----------------------------------------
    private void stageMaximizedPropertyChange(boolean newValue) {
        if (newValue) {
            if (Arrays.asList(columnsd.getImageCS()).stream().allMatch(s -> s.equals("No"))) {
                vf.getSplitLeft().setDividerPositions(1.0);
            }
        }
    }

    // -----------------------------------------
    private void afterFirstQuerySucces() {
        ms.selectTables(new ShowTableCurrentDB());
        if (!MSQL.isTableNamesExist()) {
            ms.executeUpdate(MSQL.CREATE_TABLE_NAMES);// NOT TESTED
        }
        if (!MSQL.isTableDefaultExist()) {
            ms.executeUpdate(MSQL.CREATE_TABLE_DEFAUT);// NOT TESTED
        }
        if (!MSQL.isTableConfigExist()) {
            ms.executeUpdate(MSQL.CREATE_TABLE_CONFIG);// NOT TESTED
        }
        // TABLE LIST
        // addMenuItems();
        menus.addMenuItemsReset();

        ms.executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT, new SelectTableDefault());
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
    }

    private void init() {
        try {
            FXMLLoader loader = new FXMLLoader(VF.class.getResource("/com/cofii/ts/first/VF.fxml"));

            ZoomingPane zp = new ZoomingPane(loader.load());
            zp.zoomFactorProperty().bind(scaleVF);

            Group gp = new Group(zp);
            ScrollPane scMain = new ScrollPane(gp);
            scMain.setFitToWidth(true);
            scMain.setFitToHeight(true);

            Scene scene = new Scene(scMain);
            scene.getStylesheets().add(VF.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());

            if (vl != null) {// NEW WINDOW
                stage.setScene(scene);
            } else {
                vc.getVf().getStage().setScene(scene);
            }
            // -------------------------------------------------
            vf = (VFController) loader.getController();

            Menus.clearInstance();
            menus = Menus.getInstance(vf);
            // -------------------------------------------------
            stage.maximizedProperty().addListener((obs, oldValue, newValue) -> stageMaximizedPropertyChange(newValue));
            // ZOOMING-------------------
            vf.getBpMain().prefHeightProperty().bind(scene.heightProperty());
            vf.getBpMain().prefWidthProperty().bind(scene.widthProperty());

            scene.setOnKeyReleased(e -> {
                if (e.isControlDown()) {
                    double newValue = scaleVF.getValue();
                    if (e.getCode() == KeyCode.PLUS) {
                        newValue += 0.01;
                    } else if (e.getCode() == KeyCode.MINUS && newValue > 1.0) {
                        newValue -= 0.01;
                    }
                    scaleVF.setValue(newValue);

                }
            });
            // TEST
            /*
            zp.setStyle(
                    "-fx-border-style: solid solid solid solid;  -fx-border-width: 2;  -fx-border-color: RED;");
            vf.getBtnAdd().addEventHandler(ActionEvent.ACTION, e -> {
                if (!zp.getStyle().contains("-fx-border-style: none")) {
                    zp.setStyle("-fx-border-style: none");
                    gp.setStyle("-fx-border-style: solid solid solid solid;  -fx-border-width: 2;  -fx-border-color: red;");
                }else{
                    gp.setStyle("-fx-border-style: none");
                    zp.setStyle("-fx-border-style: solid solid solid solid;  -fx-border-width: 2;  -fx-border-color: blue;");
                }
            });
            */
            // -------------------------------------------------
            vf.setStage(vl != null ? stage : vc.getVf().getStage());
            vf.setScene(scene);
            vf.setVl(vl);

            ms = new MSQLP(new CurrenConnection(), new WrongPassword(vl, vf));

            vf.setMs(ms);
            dist = Dist.getInstance(vf);
            // -------------------------------------------
            if (!MSQL.isWrongPassword()) {
                afterFirstQuerySucces();
            }
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

    // -------------------------------------------
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
