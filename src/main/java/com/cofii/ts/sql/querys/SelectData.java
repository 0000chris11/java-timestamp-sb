package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.ColumnS;
import com.cofii2.myInterfaces.IActions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SelectData implements IActions {

    private VFController vf;
    private String message;

    private ColumnS columns = ColumnS.getInstance();

    private int columnCount;
    private ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();

    public SelectData(VFController vf, String message) {
        this.vf = vf;
        this.message = message;
    }

    @Override
    public void beforeQuery() {
        vf.getTable().getItems().clear();
        columnCount = MSQL.getColumns().length;
    }

    @Override
    public void setData(ResultSet rs, int rowN) throws SQLException {
        ObservableList<Object> row = FXCollections.observableArrayList();
        for (int a = 0; a < columnCount; a++) {
            if(columns.getType(a).contains("CHAR")){
                row.add(rs.getString((a + 1)));
            }else if(columns.getType(a).contains("INT")){
                row.add(rs.getInt((a + 1)));
            }
            
        }
        data.add(row);

    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if (rsValue) {
            vf.getTable().setItems(data);
            if (message != null) {
                vf.getLbStatus().setText(message);
                vf.getLbStatus().setTextFill(NonCSS.TEXT_FILL_OK);

                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        vf.getLbStatus().setText("Waiting for action...");
                        vf.getLbStatus().setTextFill(NonCSS.TEXT_FILL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        }
    }

}
