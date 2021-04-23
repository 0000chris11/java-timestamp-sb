package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii2.myInterfaces.IActions;

public class ShowColumns implements IActions{

    private int rows;
    private VFController vf;
    public ShowColumns(VFController vf){
        this.vf = vf;
    }

    @Override
    public void beforeQuery() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String column = rs.getString(1);
        vf.getLbs()[row - 1].setText(column);

        vf.getLbs()[row - 1].setVisible(true);
        vf.getTfs()[row - 1].setVisible(true);
        vf.getBtns()[row - 1].setVisible(true);

        rows = row;
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        MSQL.setColumnsLength(rows);
        
    }
    
}
