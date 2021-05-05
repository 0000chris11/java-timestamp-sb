package com.cofii.ts.first;

import com.cofii.ts.cu.VC;
import com.cofii.ts.info.VI;
import com.cofii.ts.other.Dist;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowColumns;
import com.cofii2.stores.CC;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

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
    private Dist dist = Dist.getInstance(vf);

    // LISTENERS ---------------------------------------------------
    private void openChangeUserDBAction(ActionEvent e) {
        vf.getVl().getStage().show();
    }

    private void tableCreateAction(ActionEvent e) {
        new VC(vf, true);
    }

    private void selectionForEachTable(ActionEvent e) {
        System.out.println(CC.CYAN + "\nCHANGE TABLE" + CC.RESET);
        MenuItem mi = (MenuItem) e.getSource();
        String table = mi.getText();
        System.out.println("\ttable: " + table);

        vf.getLbTable().setText(table);
        // RESET -------------------------
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (vf.getLbs()[a].isVisible()) {
                vf.getLbs()[a].setVisible(false);

                if (!vf.getTfas()[a].isNeedsLayout()) {
                    vf.getGridPane().getChildren().remove(vf.getTfas()[a]);
                    vf.getGridPane().add(vf.getTfs()[a], 1, a);

                    vf.getGridPane().getRowConstraints().get(a).setVgrow(Priority.NEVER);
                    /*
                     * GridPane.setMargin(vf.getLbs()[a], new Insets(0, 0, 0, 0));
                     * GridPane.setMargin(vf.getTfs()[a], new Insets(0, 0, 0, 0));
                     * GridPane.setMargin(vf.getBtns()[a], new Insets(0, 0, 0, 0));
                     */
                    // vf.getGridPane().getRowConstraints().get(a).setPrefHeight(30);

                }
                vf.getTfs()[a].setVisible(false);
                vf.getBtns()[a].setVisible(false);

                vf.getTfs()[a].setText("");
                vf.getTfas()[a].getTf().setText("");
            }
        }
        // SELECT -------------------------------------
        String tableA = table.replace(" ", "_");
        vf.getMs().selectDataWhere(MSQL.TABLE_NAMES, "name", table, new SelectTableNames(true));
        vf.getMs().selectColumns(tableA, new ShowColumns(vf));
        System.out.println("\tMSQL's table: " + MSQL.getCurrentTable().getId() + " - "
                + MSQL.getCurrentTable().getName() + " - " + MSQL.getCurrentTable().getDist());

        dist.distInitOldWay(MSQL.getCurrentTable().getDist());
        vf.getMs().selectData(tableA, new SelectData(vf, SelectData.MESSGE_TABLE_CHANGE + table));
    }

    private void tableInfoAction(ActionEvent e) {
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
        tableCreate.setOnAction(this::tableCreateAction);
    }
}
