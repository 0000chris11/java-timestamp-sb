package com.cofii.ts.first;

import java.io.IOException;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.CurrenConnection;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.WrongPassword;
import com.cofii.ts.sql.querys.SelectTableDefault;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowColumns;
import com.cofii.ts.sql.querys.ShowTableCurrentDB;
import com.cofii.ts.store.TableS;
import com.cofii2.mysql.MSQLP;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class VF {

    private Stage stage = new Stage();
    private MSQLP ms;
    private TableS tables = TableS.getInstance();

    public VF(VLController vl) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VF.fxml"));
            stage.setScene(new Scene(loader.load()));
            // -------------------------------------------------
            VFController controller = (VFController) loader.getController();
            controller.setStage(stage);
            controller.setVl(vl);

            ms = new MSQLP(new CurrenConnection(MSQL.getDatabase(), MSQL.getUser(), MSQL.getPassword()),
                    new WrongPassword(vl, controller));

            if (!MSQL.isWrongPassword()) {


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
                //TABLE LIST
                ms.executeQuery(MSQL.SELECT_TABLE_NAMES, new SelectTableNames());
                if(tables.size() == 0){
                    controller.getMenuSelection().getItems().clear();
                    controller.getMenuSelection().getItems().add(new MenuItem("No tables added"));
                }else{
                    controller.getMenuSelection().getItems().clear();
                    for(int a = 0; a < tables.size(); a++){
                        controller.getMenuSelection().getItems().add(new MenuItem(tables.getTable(a)));
                    }
                }

                ms.executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT, new SelectTableDefault());
                if(MSQL.getTable() != null){
                    System.out.println(MSQL.getTable().getId() + " - " + MSQL.getTable().getName() + " - " + MSQL.getTable().getDist());
                    ms.selectColumns(MSQL.getTable().getName().replace(" ", "_"), new ShowColumns(controller));
                }
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
