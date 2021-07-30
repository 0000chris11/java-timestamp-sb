package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Table;
import com.cofii2.myInterfaces.IActions;

/**
 * Select all tables to add them to a list
 */
public class SelectTableNames implements IActions {

    private Database tables = Database.getInstance();
    private boolean selectTable;

    public SelectTableNames(boolean selectTable) {
        this.selectTable = selectTable;
    }

    @Override
    public void beforeQuery() {
        tables.clearTables();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        Table table = new Table(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
        tables.addTable(table);
        
        if (selectTable) {
            MSQL.setCurrentTable(table);
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if (rsValue) {
            MSQL.setTablesOnTableNames(true);
        }

    }

}
