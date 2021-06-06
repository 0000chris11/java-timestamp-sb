package com.cofii.ts.first;

import com.cofii.ts.cu.VC;
import com.cofii.ts.info.VI;
import com.cofii.ts.other.Dist;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.other.Timers;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowColumns;
import com.cofii.ts.store.ColumnDS;
import com.cofii.ts.store.ColumnS;
import com.cofii.ts.store.Key;
import com.cofii.ts.store.Keys;
import com.cofii.ts.store.TableS;
import com.cofii2.components.javafx.TrueFalseWindow;
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
import javafx.scene.text.Text;

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
    private Menu tableDelete = new Menu("Delete table");
    private MenuItem tableDeleteThis = new MenuItem("Delete this table");
    // ---------------------------------------------------
    private static Menus instance;
    private static VFController vf;
    private TableS tables = TableS.getInstance();
    private ColumnDS columnds = ColumnDS.getInstance();
    private Dist dist = Dist.getInstance(vf);
    private Keys keys = Keys.getInstance();
    private Timers timers = Timers.getInstance(vf);

    // LISTENERS ---------------------------------------------------
    private void openChangeUserDBAction(ActionEvent e) {
        vf.getVl().getStage().show();
    }

    private void tableCreateAction(ActionEvent e) {
        new VC(vf, true);
    }
    private void tableUpdateAction(ActionEvent e){
        new VC(vf, false);
    }

    public void selectionForEachTable(ActionEvent e) {
        System.out.println(CC.CYAN + "\nCHANGE TABLE" + CC.RESET);
        MenuItem mi = (MenuItem) e.getSource();
        String table = mi.getText();
        System.out.println("\ttable: " + table);

        vf.getLbTable().setText(table);
        // RESET NODES ---------------------------------
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (vf.getLbs()[a].isVisible()) {
                vf.getLbs()[a].setVisible(false);

                if (!columnds.getDist(a).equals("No")) {
                    //vf.getGridPane().getChildren().remove(vf.getTfas()[a]);
                    //vf.getGridPane().add(vf.getTfs()[a], 1, a);
                    vf.getTfsPs()[a].setTfParent(null);
                    vf.getGridPane().getRowConstraints().get(a).setVgrow(Priority.NEVER);
 
                }
                vf.getTfs()[a].setVisible(false);
                vf.getBtns()[a].setVisible(false);

                vf.getTfs()[a].setText("");
            }
        }

        vf.getBtnFind().setDisable(false);
        vf.getBtnAdd().setDisable(false);
        // SELECT -------------------------------------
        String tableA = table.replace(" ", "_");
        vf.getMs().selectDataWhere(MSQL.TABLE_NAMES, "name", table, new SelectTableNames(true));
        vf.getMs().selectColumns(tableA, new ShowColumns(vf));
        resetKeys();
        System.out.println("\tMSQL's table: " + MSQL.getCurrentTable().getId() + " - "
                + MSQL.getCurrentTable().getName() + " - " + MSQL.getCurrentTable().getDist());

        dist.distStart();
        vf.getMs().selectData(tableA, new SelectData(vf, SelectData.MESSGE_TABLE_CHANGE + table));
    }
    //DELETE---------------------------------------------------
    private void deleteTables(ActionEvent e) {
        System.out.println(CC.CYAN + "Delete Table" + CC.RESET);
        String table = ((MenuItem) e.getSource()).getText().replace(" ", "_");

        TrueFalseWindow w = new TrueFalseWindow("Delete Table '" + table + "'?");
        w.getBtnFalse().setOnAction(ef -> w.hide());
        w.getBtnTrue().setOnAction(et -> {deleteTablesYes(table); w.hide();});
        w.show();

    }
    private void deleteThisTable(ActionEvent e){
        System.out.println(CC.CYAN + "Delete This Table" + CC.RESET);
        String table = MSQL.getCurrentTable().getName().replace(" ", "_");

        TrueFalseWindow w = new TrueFalseWindow("Delete Table '" + table + "'?");
        w.getBtnFalse().setOnAction(ef -> w.hide());
        w.getBtnTrue().setOnAction(et -> {deleteTablesYes(table); w.hide();});
        w.show();
    }

    private void deleteTablesYes(String table) {
        boolean deleteTable = vf.getMs().deleteTable(table);
        if (deleteTable) {
            boolean removeFromTableNames = vf.getMs().deleteRow(MSQL.TABLE_NAMES, "Name", table.replace("_", " "));
            if (removeFromTableNames) {
                vf.getLbStatus().setText("Table '" + table + "' has been deleted");
                vf.getLbStatus().setTextFill(NonCSS.TEXT_FILL_OK);

                addMenuItemsReset();
                vf.clearCurrentTableView();
            } else {
                vf.getLbStatus().setText(
                        "FATAL: Table '" + table + "' has been deleted but not removed from " + MSQL.TABLE_NAMES);
                vf.getLbStatus().setTextFill(NonCSS.TEXT_FILL_ERROR);
            }
        } else {
            vf.getLbStatus().setText("Table '" + table + "' fail to be deleted");
            vf.getLbStatus().setTextFill(NonCSS.TEXT_FILL_ERROR);
        }

        timers.playLbStatusReset(vf.getLbStatus());

    }
    //------------------------------------------------------
    private void tableInfoAction(ActionEvent e) {
        new VI(vf);
    }
    //------------------------------------------------------
    public void addMenuItemsReset() {
        vf.getMs().executeQuery(MSQL.SELECT_TABLE_NAMES, new SelectTableNames(false));
        if (tables.size() == 0) {
            vf.getMenuSelection().getItems().clear();
            tableDelete.getItems().clear();
            vf.getMenuSelection().getItems().add(new MenuItem("No tables added"));
            tableDelete.getItems().add(new MenuItem("No tables added"));
        } else {
            vf.getMenuSelection().getItems().clear();
            tableDelete.getItems().clear();
            for (int a = 0; a < tables.size(); a++) {
                vf.getMenuSelection().getItems().add(new MenuItem(tables.getTable(a)));
                tableDelete.getItems().add(new MenuItem(tables.getTable(a)));
            }
        }

        vf.getMenuSelection().getItems().forEach(e -> e.setOnAction(this::selectionForEachTable));
        tableDelete.getItems().forEach(e -> e.setOnAction(this::deleteTables));
        tableDeleteThis.setOnAction(this::deleteThisTable);
    }

    private void resetKeys() {
        Key[] keyRows = keys.getCurrentTableKeys();

        for (int a = 0; a < keyRows.length; a++) {
            String columnName = keyRows[a].getColumnName();
            String constraintType = keyRows[a].getConstraintType();
            int ordinalPosition = keyRows[a].getOrdinalPosition();

            vf.getLbs()[ordinalPosition - 1].getChildren().clear();
            Text textColumnName = new Text(columnName);
            textColumnName.setFill(NonCSS.TEXT_FILL);

            if (constraintType.equals("PRIMARY KEY")) {
                Text textPk = new Text("(P) ");
                textPk.setFill(NonCSS.TEXT_FILL_PK);

                vf.getLbs()[ordinalPosition - 1].getChildren().addAll(textPk, textColumnName);
            } else if (constraintType.equals("FOREIGN KEY")) {
                Text textFk = new Text("(F) ");
                textFk.setFill(NonCSS.TEXT_FILL_PK);

                vf.getLbs()[ordinalPosition - 1].getChildren().addAll(textFk, textColumnName);
            }
        }
    }
    // INIT---------------------------------------------------
    
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
        // LISTENERS----------------------------------------------
        openChangeUserDB.setOnAction(this::openChangeUserDBAction);
        /*
         * ObservableList<MenuItem> menuItems = vf.getMenuSelection().getItems(); for
         * (MenuItem menuItem : menuItems) {
         * menuItem.setOnAction(this::selectionForEachTable); }
         */
        tableInfo.setOnAction(this::tableInfoAction);
        tableCreate.setOnAction(this::tableCreateAction);
        tableUpdate.setOnAction(this::tableUpdateAction);
    }
    // GET & SETTERS----------------------------------------------------
    public Menu getTableDelete() {
        return tableDelete;
    }

    public void setTableDelete(Menu tableDelete) {
        this.tableDelete = tableDelete;
    }

}
