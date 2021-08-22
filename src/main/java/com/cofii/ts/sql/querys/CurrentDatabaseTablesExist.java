package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.sql.MSQL;
import com.cofii2.myInterfaces.IActions;

/**
 * Determine if the necessary tables exist in the current Database
 * 
 */
public class CurrentDatabaseTablesExist implements IActions{

    @Override
    public void beforeQuery() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String table = rs.getString(1);
        if(table.equalsIgnoreCase(MSQL.TABLE_NAMES)){
            MSQL.setTableNamesExist(true);
        }
        if(table.equalsIgnoreCase(MSQL.TABLE_DEFAULT)){
            MSQL.setTableDefaultExist(true);
        }
        if(table.equalsIgnoreCase(MSQL.TABLE_CONFIG)){
            MSQL.setTableConfigExist(true);
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        // TODO Auto-generated method stub
        
    }
    
}
