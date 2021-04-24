package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii2.myInterfaces.IActions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SelectData implements IActions {

    private VFController vf;
    private int columnCount;
    private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    public SelectData(VFController vf) {
        this.vf = vf;
    }

    @Override
    public void beforeQuery() {
        vf.getTable().getItems().clear();
        columnCount = MSQL.getColumns().length;
    }

    @Override
    public void setData(ResultSet rs, int rowN) throws SQLException {
        ObservableList<String> row = FXCollections.observableArrayList();
        for (int a = 0; a < columnCount; a++) {

            row.add(rs.getString(a + 1));
        }
        data.add(row);

    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(rsValue){
            vf.getTable().setItems(data);
        }

    }

}
