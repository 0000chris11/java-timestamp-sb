package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.Column;
import com.cofii.ts.store.ColumnS;
import com.cofii2.myInterfaces.IActions;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public class ShowColumns implements IActions {

    private int rows;
    private VFController vf;
    private ColumnS columns = ColumnS.getInstance();

    public ShowColumns(VFController vf) {
        this.vf = vf;
    }

    @Override
    public void beforeQuery() {
        vf.getTable().getColumns().clear();
        columns.clearColumn();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String columnName = rs.getString(1);
        String type = rs.getString(2).toUpperCase();
        vf.getLbs()[row - 1].setText(columnName);

        vf.getLbs()[row - 1].setVisible(true);
        vf.getTfs()[row - 1].setVisible(true);
        vf.getBtns()[row - 1].setVisible(true);

        columns.addColumn(new Column(columnName, type));

        rows = row;
        //ADDING COLUMNS
        final int index = row - 1;
        TableColumn<ObservableList<Object>, Object> column = new TableColumn<>(columnName);
        column.setCellValueFactory(e -> new ReadOnlyObjectWrapper<>(e.getValue().get(index)));
        vf.getTable().getColumns().add(column);
  
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        MSQL.setColumnsLength(rows);

        String[] columns = new String[rows];
        for (int a = 0; a < rows; a++) {
            columns[a] = vf.getLbs()[a].getText();
        }
        MSQL.setColumns(columns);
    }

}