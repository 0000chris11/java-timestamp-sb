package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.Table;
import com.cofii2.myInterfaces.IActions;

public class SelectTableDefault implements IActions{

    @Override
    public void beforeQuery() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        MSQL.setTable(new Table(rs.getInt(1), rs.getString(2), rs.getString(3)));
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        // TODO Auto-generated method stub
    }
    
}