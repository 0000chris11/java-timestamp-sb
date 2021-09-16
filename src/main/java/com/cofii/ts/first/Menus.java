package com.cofii.ts.first;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cofii.ts.cu.VC;
import com.cofii.ts.cu.VCD;

import com.cofii.ts.game.VG;
import com.cofii.ts.info.VI;
import com.cofii.ts.options.VO;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;

import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.FK;
import com.cofii.ts.store.main.PK;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.TrueFalseWindow;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Menus {
    // Open
    private final MenuItem openChangeUserDB = new MenuItem("Change User or DB");
    private final MenuItem openTableMain = new MenuItem("Open Tables Info");
    private final MenuItem openGame = new MenuItem("Start game for this table");
    // Options
    private final MenuItem optionsGeneralOptions = new MenuItem("General Options");
    private final MenuItem optionsCreateDatabase = new MenuItem("Create Database");
    private final MenuItem optionsTableInfo = new MenuItem("Current Table Info");
    private final Menu optionsTableOp = new Menu("Options");
    private final Menu optionsTableChangeDTable = new Menu("Change Default Table");
    private final MenuItem optionsTableCreate = new MenuItem("Create new table");
    private final MenuItem optionsTableUpdate = new MenuItem("Update table");
    private final Menu optionsTableDelete = new Menu("Delete table");
    private final MenuItem optionsTableDeleteThis = new MenuItem("Delete this table");
    // ---------------------------------------------------
    private static VFController vfc;
    private Database currentDatabase;
    private Table currentTable;

    // QUERYS-----------------------------------------------------
    private void selectTables(ResultSet rs, boolean rsValues, SQLException ex) throws SQLException {
        if (rsValues) {
            Table table = new Table(rs.getInt(1), rs.getString(2).replace(" ", "_"));

            currentDatabase.addTable(table);
        }
    }

    private void selectCustoms(ResultSet rs, boolean rsValues, SQLException ex) throws SQLException {
        if(rsValues){
            Database currentDatabase = null;
            if(currentDatabase == null){
            currentDatabase = Users.getInstance().getCurrenUser().getCurrentDatabase();
            }
            int tableId = rs.getInt(1);
            String dist = rs.getString(2);
            String textArea = rs.getString(3);

            Table table = currentDatabase.getTable(tableId);
            table.setDist(dist);
            table.setTextArea(textArea);
        }
    }

    private void selectPaths(ResultSet rs, boolean rsValues, SQLException ex) throws SQLException {
        if (rsValues) {

        }
    }

    // LISTENERS ---------------------------------------------------
    private void openChangeUserDBAction(ActionEvent e) {
        vfc.getVl().getStage().show();
    }

    private void optionsCreateDatabaseAction(ActionEvent e) {
        new VCD(vfc);
    }

    private void tableCreateAction(ActionEvent e) {
        new VC(vfc, true);
    }

    private void tableUpdateAction(ActionEvent e) {
        new VC(vfc, false);
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

                addTablesToTfTableReset(vfc);
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
    public void addTablesToTfTableReset(VFController vfc) {
        currentDatabase = Users.getInstance().getCurrenUser().getCurrentDatabase();
        // SELECT TABLES-------------------------------
        currentDatabase.clearTables();
        vfc.getMs().selectData(MSQL.TABLE_NAMES, this::selectTables);
        //vfc.getMs().selectData(MSQL.TABLE_CUSTOMS, this::selectCustoms);MAY NOT BE NECESSARY
        vfc.getMs().selectData(MSQL.PATHS, vfc.getVf()::selectPathsForCurrentUser);

        if (!Database.getTables().isEmpty()) {
            vfc.getTfTable().setPromptText("select a table");
        } else {
            vfc.getTfTable().setPromptText("no tables found");
            vfc.getVf().setNoTablesForCurrentDatabase(true);
        }
        // ------------------------------------------------
        vfc.getTfTableAutoC().clearItems();
        if (currentDatabase.size() == 0) {
            // vfc.getMenuSelection().getItems().clear();
            optionsTableDelete.getItems().clear();
            // vfc.getMenuSelection().getItems().add(new MenuItem("No tables added"));
            vfc.getTfTable().setPromptText(VFController.NO_DATABASE_SELECTED);
            optionsTableDelete.getItems().add(new MenuItem("No tables added"));
        } else {
            // vfc.getMenuSelection().getItems().clear();
            optionsTableDelete.getItems().clear();
            for (int a = 0; a < currentDatabase.size(); a++) {// TABLES SIZE
                // vfc.getMenuSelection().getItems().add(new
                // MenuItem(currentDatabase.getTableName(a)));
                vfc.getTfTableAutoC().addItem(currentDatabase.getTableName(a));
                optionsTableDelete.getItems().add(new MenuItem(currentDatabase.getTableName(a)));
            }
        }

        // vfc.getMenuSelection().getItems().forEach(e ->
        // e.setOnAction(this::selectionForEachTable));
        optionsTableDelete.getItems().forEach(e -> e.setOnAction(this::deleteTables));
        optionsTableDeleteThis.setOnAction(this::deleteThisTable);
    }

    public void resetCurrentTableKeys() {
        currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        // PRIMARY KEYS---------------------------------------------
        List<PK> cpks = currentTable.getPKS();
        cpks.forEach(pk -> {
            int ordinalPosition = pk.getOrdinalPosition() - 1;
            String columnName = pk.getColumnName();

            Text textPk = new Text("(P) ");
            textPk.setFill(NonCSS.TEXT_FILL_PK);

            Text textColumnName = new Text(columnName);
            textColumnName.setFill(NonCSS.TEXT_FILL);

            vfc.getLbs()[ordinalPosition].getChildren().clear();
            vfc.getLbs()[ordinalPosition].getChildren().addAll(textPk, textColumnName);
        });

        // FOREIGN KEYS---------------------------------------------
        List<FK> cfks = currentTable.getFKS();
        Set<String> constraints = new HashSet<>();
        int[] indexs = { 1 };
        cfks.forEach(fk -> {
            int ordinalPosition = fk.getOrdinalPosition() - 1;
            String columnName = fk.getColumnName();

            String constraintType = fk.getConstraintType();
            boolean added = constraints.add(constraintType);
            boolean match = constraints.stream().anyMatch(cons -> cons.equals(constraintType));

            if (match) {
                Text textFk = new Text("(F" + (indexs[0]++) + ") ");
                textFk.setFill(NonCSS.TEXT_FILL_FK);

                Text textColumnName = new Text(columnName);
                textColumnName.setFill(NonCSS.TEXT_FILL);

                vfc.getLbs()[ordinalPosition].getChildren().clear();
                vfc.getLbs()[ordinalPosition].getChildren().addAll(textFk, textColumnName);
                vfc.getTfs()[ordinalPosition].setStyle(CSS.TFS_FK_LOOK);

                if (added) {// ONCE PER GROUP
                    String referencedDatabase = fk.getReferencedDatabaseName();
                    String referencedTable = fk.getReferencedTableName();
                    String referencedColumn = fk.getReferencedColumnName();
                    vfc.getMs().setDistinctOrder(MSQLP.MOST_USE_ORDER);
                    vfc.getMs().selectDistinctColumn(referencedDatabase + "." + referencedTable, referencedColumn,
                            (rs, rsValues, ex) -> vfc.getTfsFKList().get(ordinalPosition).add(rs.getString(1)));
                }
            }

        });
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
        vfc.getMenuOpen().getItems().addAll(openChangeUserDB, openTableMain, new SeparatorMenuItem(), openGame);
        vfc.getMenuOptions().getItems().addAll(optionsGeneralOptions, new SeparatorMenuItem(), optionsCreateDatabase,
                new SeparatorMenuItem(), optionsTableInfo, optionsTableOp, new SeparatorMenuItem(),
                optionsTableChangeDTable, new SeparatorMenuItem(), optionsTableCreate, optionsTableUpdate,
                optionsTableDelete, optionsTableDeleteThis);
        // LISTENERS----------------------------------------------
        optionsGeneralOptions.setOnAction(e -> new VO(vfc));
        openChangeUserDB.setOnAction(this::openChangeUserDBAction);
        openGame.setOnAction(e -> new VG(vfc.getTable().getItems()));

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
