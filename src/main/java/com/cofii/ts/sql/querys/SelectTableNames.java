package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.Table;
import com.cofii.ts.store.TableS;
import com.cofii2.myInterfaces.IActions;

public class SelectTableNames implements IActions {

    private TableS tables = TableS.getInstance();
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
        Table table = new Table(rs.getInt(1), rs.getString(2), rs.getString(3));
        tables.addTable(table);
        
        if (selectTable) {
            MSQL.setTable(table);
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if (rsValue) {
            MSQL.setTablesOnTableNames(true);
        }

    }

}
