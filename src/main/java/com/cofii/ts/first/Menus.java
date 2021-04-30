package com.cofii.ts.first;

import com.cofii.ts.info.VI;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowColumns;
import com.cofii2.stores.CC;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class Menus {
    // Open
    private MenuItem openChangeUserDB = new MenuItem("Change User or DB");
    private MenuItem openTableMain = new MenuItem("Open Tables Info");
    // Table
    private MenuItem tableInfo = new MenuItem("Current Table Info");
    private Menu tableOp = new Menu("Options");
    private CheckMenuItem tableOpClearValues = new CheckMenuItem("Clear nodes values when a row is deleted");
    private CheckMenuItem tableOpReloacImage = new CheckMenuItem("Reload ImageC");
    private Menu tableChangeDTable = new Menu("Change Default Table");
    private MenuItem tableCreate = new MenuItem("Create new table");
    private MenuItem tableUpdate = new MenuItem("Update table");
    private MenuItem tableDelete = new MenuItem("Delete table");
    private MenuItem tableDeleteThis = new MenuItem("Delete this table");

    // ---------------------------------------------------
    private void openChangeUserDBAction(ActionEvent e) {
        vf.getVl().getStage().show();
    }

    private void selectionForEachTable(ActionEvent e) {
        System.out.println(CC.CYAN + "\nCHANGE TABLE" + CC.RESET);
        MenuItem mi = (MenuItem) e.getSource();
        String table = mi.getText();
        System.out.println("\ttable: " + table);

        vf.getLbTable().setText(table);
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (vf.getLbs()[a].isVisible()) {
                vf.getLbs()[a].setVisible(false);

                if (!vf.getCbs()[a].isNeedsLayout()) {
                    vf.getGridPane().getChildren().remove(vf.getCbs()[a]);
                    
                    if (vf.getTfs()[a].isNeedsLayout()) {
                        vf.getGridPane().add(vf.getTfs()[a], 1, a);
                    }
                }
                vf.getTfs()[a].setVisible(false);
                vf.getBtns()[a].setVisible(false);

                vf.getTfs()[a].setText("");
                vf.getCbs()[a].getEditor().setText("");
            }
        }

        String tableA = table.replace(" ", "_");
        vf.getMs().selectDataWhere(MSQL.TABLE_NAMES, "name", table, new SelectTableNames(true));
        vf.getMs().selectColumns(tableA, new ShowColumns(vf));
        System.out.println("\tMSQL's table: " + MSQL.getCurrentTable().getId() + " - " + MSQL.getCurrentTable().getName() + " - "
                + MSQL.getCurrentTable().getDist());
        VF.distOldWay(MSQL.getCurrentTable().getDist());
        vf.getMs().selectData(tableA, new SelectData(vf, SelectData.MESSGE_TABLE_CHANGE + table));
    }

    private void tableInfoAction(ActionEvent e){
        new VI(vf);
    }
    // ---------------------------------------------------
    private static Menus instance;
    private static VFController vf;

    public static Menus getInstance(VFController vf) {
        Menus.vf = vf;
        if (instance == null) {
            instance = new Menus();
        }
        return instance;
    }

    private Menus() {
        vf.getMenuOpen().getItems().addAll(openChangeUserDB, openTableMain);
        vf.getMenuTable().getItems().addAll(tableInfo, tableOp, new SeparatorMenuItem(), tableChangeDTable,
                new SeparatorMenuItem(), tableCreate, tableUpdate, tableDelete, tableDeleteThis);
        // LISTENERS
        openChangeUserDB.setOnAction(this::openChangeUserDBAction);
        ObservableList<MenuItem> menuItems = vf.getMenuSelection().getItems();
        for (MenuItem menuItem : menuItems) {
            menuItem.setOnAction(this::selectionForEachTable);
        }

        tableInfo.setOnAction(this::tableInfoAction);
    }
}
