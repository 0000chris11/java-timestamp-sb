package com.cofii.ts.first;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.CurrenConnection;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.WrongPassword;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.sql.querys.SelectTableDefault;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowColumns;
import com.cofii.ts.sql.querys.ShowTableCurrentDB;
import com.cofii.ts.store.TableS;
import com.cofii2.mysql.MSQLP;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

public class VF {

    private static VFController controller;
    private Stage stage = new Stage();
    private MSQLP ms;
    private TableS tables = TableS.getInstance();

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
        addMenuItems();

        ms.executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT, new SelectTableDefault());
        if (MSQL.getTable() != null) {
            String table = MSQL.getTable().getName();
            String dist = MSQL.getTable().getDist();
            controller.getLbTable().setText(table);

            ms.selectColumns(table.replace(" ", "_"), new ShowColumns(controller));
            distOldWay(dist);

            ms.selectData(table.replace(" ", "_"), new SelectData(controller, null));
        }else{
            //NOT TESTED
        }
    }

    private void addMenuItems(){
        ms.executeQuery(MSQL.SELECT_TABLE_NAMES, new SelectTableNames(false));
        if (tables.size() == 0) {
            controller.getMenuSelection().getItems().clear();
            controller.getMenuSelection().getItems().add(new MenuItem("No tables added"));
        } else {
            controller.getMenuSelection().getItems().clear();
            for (int a = 0; a < tables.size(); a++) {
                controller.getMenuSelection().getItems().add(new MenuItem(tables.getTable(a)));
            }
        }
        //ADDING REMAINING MENUS
        Menus menus = Menus.getInstance(controller);
    }

    public static void distOldWay(String dist) {
        int length = dist.length();
        int p = 5;
        // X2: 3_4 :: 7
        while (p <= length) {
            int c = Character.getNumericValue(dist.charAt(p - 1));
            controller.getGridPane().getChildren().remove(controller.getTfs()[c - 1]);
            controller.getGridPane().add(controller.getCbs()[c - 1], 1, c - 1);

            p += 2;
        }

    }

    public VF(VLController vl) {
        try {
            FXMLLoader loader = new FXMLLoader(VF.class.getResource("/com/cofii/ts/first/VF.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(VF.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            stage.setScene(scene);
            // -------------------------------------------------
            controller = (VFController) loader.getController();
            controller.setStage(stage);
            controller.setVl(vl);

            ms = new MSQLP(new CurrenConnection(MSQL.getDatabase(), MSQL.getUser(), MSQL.getPassword()),
                    new WrongPassword(vl, controller));

            controller.setMs(ms);

            if (!MSQL.isWrongPassword()) {
                afterFirstQuerySucces();
            }
            // -------------------------------------------------

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
