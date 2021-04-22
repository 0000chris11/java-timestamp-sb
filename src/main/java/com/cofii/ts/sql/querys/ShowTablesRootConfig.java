package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.sql.MSQL;
import com.cofii2.myInterfaces.IActions;

public class ShowTablesRootConfig implements IActions{

    @Override
    public void beforeQuery() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String table = rs.getString(1);
        if(table.equalsIgnoreCase(MSQL.TABLE_DEFAULT_USER)){
            MSQL.setTableDefaultUserExist(true);
        }
        
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(!rsValue){
            MSQL.setTableDefaultUserExist(false);
        }
        
    }
    
}
