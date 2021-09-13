package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.myInterfaces.IActions;

/**
 * Select all tables to add them to a list
 */
public class SelectTableNames implements IActions {

    private Database currentDatabase = Users.getInstance().getCurrenUser().getCurrentDatabase();
    private boolean selectTable;
    private VFController vfc;

    public SelectTableNames(boolean selectTable) {
        this.selectTable = selectTable;
    }

    public SelectTableNames(boolean selectTable, VFController vfc) {
        this.selectTable = selectTable;
        this.vfc = vfc;
    }

    @Override
    public void beforeQuery() {
        if (!selectTable) {
            currentDatabase.clearTables();
        }
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        Table table = new Table(rs.getInt(1), rs.getString(2).replace(" ", "_"));

        if (selectTable) {
            Users.getInstance().getCurrenUser().getCurrentDatabase().setCurrentTable(table);
        } else {
            currentDatabase.addTable(table);
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if (vfc != null) {
            if (rsValue) {
                vfc.getTfTable().setPromptText("select a table");
            } else {
                vfc.getTfTable().setPromptText("no tables found");
                vfc.getVf().setNoTablesForCurrentDatabase(true);
            }
        }
    }

}
