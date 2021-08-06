package com.cofii.ts.first;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.cu.VC;
import com.cofii.ts.info.VI;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.Dist;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.other.Timers;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowColumns;
import com.cofii.ts.store.FK;
import com.cofii.ts.store.FKS;
import com.cofii.ts.store.PK;
import com.cofii.ts.store.PKS;
import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.TrueFalseWindow;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
    private static VFController vf;
    private Database currentDatabase = Users.getInstance().getCurrenUser().getCurrentDatabase();
    private Table currentTable = currentDatabase.getCurrentTable();
    //private ColumnDS columnds = ColumnDS.getInstance();
    private Dist dist = Dist.getInstance(vf);
    private PKS pks = PKS.getInstance();
    private FKS fks = FKS.getInstance();

    private Timers timers = Timers.getInstance(vf);

    // LISTENERS ---------------------------------------------------
    private void openChangeUserDBAction(ActionEvent e) {
        vf.getVl().getStage().show();
    }

    private void tableCreateAction(ActionEvent e) {
        new VC(vf, true);
    }

    private void tableUpdateAction(ActionEvent e) {
        new VC(vf, false);
    }

    public void selectionForEachTable(ActionEvent e) {
        System.out.println(CC.CYAN + "\nCHANGE TABLE" + CC.RESET);

        MenuItem mi = (MenuItem) e.getSource();
        String tableName = mi.getText();
        System.out.println("\ttable: " + tableName);

        vf.getLbTable().setText(tableName);
        // RESET NODES ---------------------------------
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (vf.getLbs()[a].isVisible()) {
                vf.getLbs()[a].setVisible(false);

                if (Boolean.TRUE.equals(currentTable.getColumnDists().get(a)) || fks.getYesAndNoFKS()[a].equals("Yes")) {// RESETING DIST
                    vf.getTfsAutoC().get(a).setTfParent(null);
                    vf.getTfs()[a].setStyle(CSS.TFS_DEFAULT_LOOK);
                }
                vf.getTfs()[a].setVisible(false);
                vf.getBtns()[a].setVisible(false);

                vf.getTfs()[a].setText("");
            }
        }
        vf.getTfsFKList().forEach(List::clear);
        Arrays.asList(vf.getBtns()).forEach(btn -> btn.setSelected(true));
        vf.setSelectedRow(null);

        vf.getBtnFind().setDisable(false);
        vf.getBtnAdd().setDisable(false);
        // SELECT -------------------------------------
        String tableA = tableName.replace(" ", "_");
        vf.getMs().selectDataWhere(MSQL.TABLE_NAMES, "name", tableName, new SelectTableNames(true));
        vf.getMs().selectColumns(tableA, new ShowColumns(vf));
        resetKeys();
        System.out.println("\tMSQL's table: " + currentTable.getId() + " - "
                + currentTable.getName() + " - " + currentTable.getDist());

        dist.distStart();
        vf.getMs().selectData(tableA, new SelectData(vf, SelectData.MESSGE_TABLE_CHANGE + tableName));
    }

    // DELETE---------------------------------------------------
    private void deleteTables(ActionEvent e) {
        System.out.println(CC.CYAN + "Delete Table" + CC.RESET);
        String table = ((MenuItem) e.getSource()).getText().replace(" ", "_");

        TrueFalseWindow w = new TrueFalseWindow("Delete Table '" + table + "'?");
        w.getBtnFalse().setOnAction(ef -> w.hide());
        w.getBtnTrue().setOnAction(et -> {
            deleteTablesYes(table);
            w.hide();
        });
        w.show();

    }

    private void deleteThisTable(ActionEvent e) {
        System.out.println(CC.CYAN + "Delete This Table" + CC.RESET);
        String table = currentTable.getName().replace(" ", "_");

        TrueFalseWindow w = new TrueFalseWindow("Delete Table '" + table + "'?");
        w.getBtnFalse().setOnAction(ef -> w.hide());
        w.getBtnTrue().setOnAction(et -> {
            deleteTablesYes(table);
            w.hide();
        });
        w.show();
    }

    private void deleteTablesYes(String table) {
        boolean deleteTable = vf.getMs().deleteTable(table);
        if (deleteTable) {
            boolean removeFromTableNames = vf.getMs().deleteRow(MSQL.TABLE_NAMES, "Name", table.replace("_", " "));
            if (removeFromTableNames) {
                vf.getLbStatus().setText("Table '" + table + "' has been deleted", NonCSS.TEXT_FILL_OK,
                        Duration.seconds(2));

                addMenuItemsReset();
                vf.clearCurrentTableView();
            } else {
                vf.getLbStatus().setText(
                        "FATAL: Table '" + table + "' has been deleted but not removed from " + MSQL.TABLE_NAMES,
                        NonCSS.TEXT_FILL_ERROR);
            }
        } else {
            vf.getLbStatus().setText("Table '" + table + "' fail to be deleted", NonCSS.TEXT_FILL_ERROR);
        }
    }

    // ------------------------------------------------------
    private void tableInfoAction(ActionEvent e) {
        new VI(vf);
    }

    // ------------------------------------------------------
    public void addMenuItemsReset() {
        vf.getMs().executeQuery(MSQL.SELECT_TABLE_NAMES, new SelectTableNames(false));
        if (currentDatabase.size() == 0) {
            vf.getMenuSelection().getItems().clear();
            tableDelete.getItems().clear();
            vf.getMenuSelection().getItems().add(new MenuItem("No tables added"));
            tableDelete.getItems().add(new MenuItem("No tables added"));
        } else {
            vf.getMenuSelection().getItems().clear();
            tableDelete.getItems().clear();
            for (int a = 0; a < currentDatabase.size(); a++) {
                vf.getMenuSelection().getItems().add(new MenuItem(currentDatabase.getTable(a)));
                tableDelete.getItems().add(new MenuItem(currentDatabase.getTable(a)));
            }
        }

        vf.getMenuSelection().getItems().forEach(e -> e.setOnAction(this::selectionForEachTable));
        tableDelete.getItems().forEach(e -> e.setOnAction(this::deleteTables));
        tableDeleteThis.setOnAction(this::deleteThisTable);
    }

    public void resetKeys() {
        // PRIMARY KEYS---------------------------------------------
        PK[] cpks = pks.getCurrentTablePKS();
        if (cpks.length > 0) {
            cpks[0].getColumns().forEach(cols -> {
                int ordinalPosition = cols.index - 1;
                String columnName = cols.string;

                Text textPk = new Text("(P) ");
                textPk.setFill(NonCSS.TEXT_FILL_PK);

                Text textColumnName = new Text(columnName);
                textColumnName.setFill(NonCSS.TEXT_FILL);

                vf.getLbs()[ordinalPosition].getChildren().clear();
                vf.getLbs()[ordinalPosition].getChildren().addAll(textPk, textColumnName);
            });
        }
        // FOREIGN KEYS---------------------------------------------
        FK[] cfks = fks.getCurrentTableFKS();
        for (int a = 0; a < cfks.length; a++) {
            final String referencedDatabase = cfks[a].getReferencedDatabase();
            final String referencedTable = cfks[a].getReferencedTable();

            int[] indexs = { 0 };
            final int aa = a;
            cfks[a].getColumns().forEach(cols -> {
                int ordinalPosition = cols.index - 1;
                String columnName = cols.string;

                Text textFk = new Text("(F) ");
                textFk.setFill(NonCSS.TEXT_FILL_FK);

                Text textColumnName = new Text(columnName);
                textColumnName.setFill(NonCSS.TEXT_FILL);

                vf.getLbs()[ordinalPosition].getChildren().clear();
                vf.getLbs()[ordinalPosition].getChildren().addAll(textFk, textColumnName);

                //vf.getTfsAutoC()[ordinalPosition].setTfParent(vf.getTfs()[ordinalPosition]);
                vf.getTfs()[ordinalPosition].setStyle(CSS.TFS_FK_LOOK);
                // QUERY---------------------------
                String column = cfks[aa].getReferencedColumns().get(indexs[0]++);

                vf.getMs().setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
                vf.getMs().selectDistinctColumn(referencedDatabase + "." + referencedTable, column.replace(" ", "_"),
                        rs -> {
                            try {
                                boolean rsValues = false;
                                while (rs.next()) {
                                    rsValues = true;
                                    vf.getTfsFKList().get(ordinalPosition).add(rs.getString(1));
                                }
                                if (!rsValues) {
                                    vf.addTfsFKTextProperty(ordinalPosition);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
            });
        }
        /*
         * for (int a = 0; a < keyRows.length; a++) { String columnName =
         * keyRows[a].getColumnName(); String constraintType =
         * keyRows[a].getConstraintType(); int ordinalPosition =
         * keyRows[a].getOrdinalPosition();
         * 
         * vf.getLbs()[ordinalPosition - 1].getChildren().clear(); Text textColumnName =
         * new Text(columnName); textColumnName.setFill(NonCSS.TEXT_FILL);
         * 
         * if (constraintType.equals("PRIMARY KEY")) { Text textPk = new Text("(P) ");
         * textPk.setFill(NonCSS.TEXT_FILL_PK);
         * 
         * vf.getLbs()[ordinalPosition - 1].getChildren().addAll(textPk,
         * textColumnName); } else if (constraintType.equals("FOREIGN KEY")) { Text
         * textFk = new Text("(F) "); textFk.setFill(NonCSS.TEXT_FILL_PK);
         * 
         * vf.getLbs()[ordinalPosition - 1].getChildren().addAll(textFk,
         * textColumnName); } }
         */
    }

    // INIT---------------------------------------------------
    private static Menus instance;

    public static Menus getInstance(VFController vf) {
        Menus.vf = vf;
        if (instance == null) {
            instance = new Menus();
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
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
