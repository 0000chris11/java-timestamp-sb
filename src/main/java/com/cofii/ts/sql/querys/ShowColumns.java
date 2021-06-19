package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.Column;
import com.cofii.ts.store.ColumnS;
import com.cofii2.myInterfaces.IActions;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * Class to reset the columns, their nodes and the table columns
 */
public class ShowColumns implements IActions {

    private int rows;
    private VFController vf;
    private ColumnS columns = ColumnS.getInstance();

    public ShowColumns(VFController vf) {
        this.vf = vf;
    }

    @Override
    public void beforeQuery() {
        Arrays.asList(vf.getLbs()).forEach(e -> e.getChildren().clear());

        vf.getTable().getColumns().clear();
        columns.clearColumn();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String columnName = rs.getString(1).replace("_", " ");
        String typeHole = rs.getString(2).toUpperCase();
        String nulll = rs.getString(3);
        String defaultt = rs.getString(5);
        String extra = rs.getString(6);
        //TYPE & TYPE-LENGTH------------------------------------
        String type = typeHole;
        int typeLength = -1;
        if(typeHole.contains("(")){
            type = typeHole.substring(0, typeHole.indexOf("("));
            typeLength = Integer.parseInt(typeHole.substring(typeHole.indexOf("(") + 1, typeHole.length() - 1));
        }else if(typeHole.equalsIgnoreCase("int")){
            typeLength = 11;
        }
        //NULL------------------------------------
        boolean nullValue;
        if(nulll.equals("NO")){
            nullValue = false;
        }else{
            nullValue = true;
        }
        //EXTRA---------------------------------------------
        extra = extra.equals("auto_increment") ? "Yes":"No";
        //NODES VISIBILITY ----------------------------------------------
        Text textColumnName = new Text(columnName);
        textColumnName.setFill(NonCSS.TEXT_FILL);
        vf.getLbs()[row - 1].getChildren().add(textColumnName);
        vf.getLbs()[row - 1].setVisible(true);
        if(extra.equals("Yes")){
            vf.getTfs()[row - 1].setPromptText("AUTO_INCREMENT");
        }else{
            vf.getTfs()[row - 1].setPromptText(null);
        }
        vf.getTfs()[row - 1].setVisible(true);
        vf.getBtns()[row - 1].setVisible(true);
        //---------------------------------------------
        columns.addColumn(new Column(columnName, type, typeLength, nullValue, defaultt, extra));
        //---------------------------------------------
        rows = row;
        //ADDING COLUMNS-------------------------------
        final int index = row - 1;
        TableColumn<ObservableList<Object>, Object> column = new TableColumn<>(columnName);
        column.setCellValueFactory(e -> new ReadOnlyObjectWrapper<>(e.getValue().get(index)));
        //EDITABLE CELL----------------------------------
        column.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Object>(){
            @Override
            public String toString(Object object) {
                return object.toString();
            }
            @Override
            public Object fromString(String string) {
                return string;
            }
        }));
        column.setOnEditCommit(vf::tableCellEdit);
        //--------------------------------------------------
        vf.getTable().getColumns().add(column);
  
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        MSQL.setColumnsLength(rows);

        String[] columns = new String[rows];
        for (int a = 0; a < rows; a++) {
            columns[a] = ((Text)vf.getLbs()[a].getChildren().get(0)).getText();
        }
        MSQL.setColumns(columns);
    }

}
