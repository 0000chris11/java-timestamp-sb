package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.sql.MSQL;
import com.cofii2.myInterfaces.IActions;

public class SelectDefaultUser implements IActions{

    @Override
    public void beforeQuery() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        MSQL.setUser(rs.getString(1));
        MSQL.setPassword(rs.getString(2));
        MSQL.setDatabase(rs.getString(3));
        
        System.out.println("TEST user: " + MSQL.getUser());
        System.out.println("TEST password: " + MSQL.getPassword());
        System.out.println("TEST database: " + MSQL.getDatabase());
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        // TODO Auto-generated method stub
        
    }
    
}
