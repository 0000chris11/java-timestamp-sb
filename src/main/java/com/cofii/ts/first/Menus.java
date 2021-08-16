package com.cofii.ts.first;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.cu.VC;
import com.cofii.ts.cu.VCD;
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
    private final MenuItem openChangeUserDB = new MenuItem("Change User or DB");
    private final MenuItem openTableMain = new MenuItem("Open Tables Info");
    // Options
    private final MenuItem optionsCreateDatabase = new MenuItem("Create Database");
    private final MenuItem optionsTableInfo = new MenuItem("Current Table Info");
    private final Menu optionsTableOp = new Menu("Options");
    private final CheckMenuItem optionsTableOpClearValues = new CheckMenuItem("Clear nodes values when a row is deleted");
    private final CheckMenuItem optionsTableOpReloacImage = new CheckMenuItem("Reload ImageC");
    private final Menu optionsTableChangeDTable = new Menu("Change Default Table");
    private final MenuItem optionsTableCreate = new MenuItem("Create new table");
    private final MenuItem optionsTableUpdate = new MenuItem("Update table");
    private final Menu optionsTableDelete = new Menu("Delete table");
    private final MenuItem optionsTableDeleteThis = new MenuItem("Delete this table");
    // ---------------------------------------------------
    private static VFController vfc;
    private Database currentDatabase;
    private Table currentTable;
    // private ColumnDS columnds = ColumnDS.getInstance();
    private Dist dist = Dist.getInstance(vfc);
    private PKS pks = PKS.getInstance();
    private FKS fks = FKS.getInstance();

    private Timers timers = Timers.getInstance(vfc);

    // LISTENERS ---------------------------------------------------
    private void openChangeUserDBAction(ActionEvent e) {
        vfc.getVl().getStage().show();
    }

    private void optionsCreateDatabaseAction(ActionEvent e){
        new VCD(vfc);
    }

    private void tableCreateAction(ActionEvent e) {
        new VC(vfc, true);
    }

    private void tableUpdateAction(ActionEvent e) {
        new VC(vfc, false);
    }

    public void selectionForEachTable(ActionEvent e) {
        
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
        currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
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
        boolean deleteTable = vfc.getMs().deleteTable(table);
        if (deleteTable) {
            boolean removeFromTableNames = vfc.getMs().deleteRow(MSQL.TABLE_NAMES, "Name", table.replace("_", " "));
            if (removeFromTableNames) {
                vfc.getLbStatus().setText("Table '" + table + "' has been deleted", NonCSS.TEXT_FILL_OK,
                        Duration.seconds(2));

                addTablesToTfTableReset();
                vfc.clearCurrentTableView();
            } else {
                vfc.getLbStatus().setText(
                        "FATAL: Table '" + table + "' has been deleted but not removed from " + MSQL.TABLE_NAMES,
                        NonCSS.TEXT_FILL_ERROR);
            }
        } else {
            vfc.getLbStatus().setText("Table '" + table + "' fail to be deleted", NonCSS.TEXT_FILL_ERROR);
        }
    }

    // ------------------------------------------------------
    private void tableInfoAction(ActionEvent e) {
        new VI(vfc);
    }

    // ------------------------------------------------------
    public void addTablesToTfTableReset() {
        currentDatabase = Users.getInstance().getCurrenUser().getCurrentDatabase();
        vfc.getMs().executeQuery(MSQL.SELECT_TABLE_NAMES, new SelectTableNames(false));
        
        vfc.getTfTableAutoC().clearItems();
        if (currentDatabase.size() == 0) {
            //vfc.getMenuSelection().getItems().clear();
            optionsTableDelete.getItems().clear();
            //vfc.getMenuSelection().getItems().add(new MenuItem("No tables added"));
            vfc.getTfTable().setPromptText(VFController.NO_DATABASE_SELECTED);
            optionsTableDelete.getItems().add(new MenuItem("No tables added"));
        } else {
            //vfc.getMenuSelection().getItems().clear();
            optionsTableDelete.getItems().clear();
            for (int a = 0; a < currentDatabase.size(); a++) {//TABLES SIZE
                //vfc.getMenuSelection().getItems().add(new MenuItem(currentDatabase.getTableName(a)));
                vfc.getTfTableAutoC().addItem(currentDatabase.getTableName(a));
                optionsTableDelete.getItems().add(new MenuItem(currentDatabase.getTableName(a)));
            }
        }

        //vfc.getMenuSelection().getItems().forEach(e -> e.setOnAction(this::selectionForEachTable));
        optionsTableDelete.getItems().forEach(e -> e.setOnAction(this::deleteTables));
        optionsTableDeleteThis.setOnAction(this::deleteThisTable);
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

                vfc.getLbs()[ordinalPosition].getChildren().clear();
                vfc.getLbs()[ordinalPosition].getChildren().addAll(textPk, textColumnName);
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

                vfc.getLbs()[ordinalPosition].getChildren().clear();
                vfc.getLbs()[ordinalPosition].getChildren().addAll(textFk, textColumnName);

                // vf.getTfsAutoC()[ordinalPosition].setTfParent(vf.getTfs()[ordinalPosition]);
                vfc.getTfs()[ordinalPosition].setStyle(CSS.TFS_FK_LOOK);
                // QUERY---------------------------
                String column = cfks[aa].getReferencedColumns().get(indexs[0]++);

                vfc.getMs().setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
                vfc.getMs().selectDistinctColumn(referencedDatabase + "." + referencedTable, column.replace(" ", "_"),
                        rs -> {
                            try {
                                boolean rsValues = false;
                                while (rs.next()) {
                                    rsValues = true;
                                    vfc.getTfsFKList().get(ordinalPosition).add(rs.getString(1));
                                }
                                if (!rsValues) {
                                    vfc.addTfsFKTextProperty(ordinalPosition);
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
        Menus.vfc = vf;
        if (instance == null) {
            instance = new Menus();
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    private Menus() {
        vfc.getMenuOpen().getItems().addAll(openChangeUserDB, openTableMain);
        vfc.getMenuOptions().getItems().addAll(optionsCreateDatabase, new SeparatorMenuItem(), optionsTableInfo, optionsTableOp,
                new SeparatorMenuItem(), optionsTableChangeDTable, new SeparatorMenuItem(), optionsTableCreate, optionsTableUpdate,
                optionsTableDelete, optionsTableDeleteThis);
        // LISTENERS----------------------------------------------
        openChangeUserDB.setOnAction(this::openChangeUserDBAction);

        optionsCreateDatabase.setOnAction(this::optionsCreateDatabaseAction);
        optionsTableInfo.setOnAction(this::tableInfoAction);
        optionsTableCreate.setOnAction(this::tableCreateAction);
        optionsTableUpdate.setOnAction(this::tableUpdateAction);
    }

    // GET & SETTERS----------------------------------------------------
    public Menu getOptionsTableDelete() {
        return optionsTableDelete;
    }
}
