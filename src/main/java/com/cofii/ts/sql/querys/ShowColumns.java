package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.SQLTypes;
import com.cofii.ts.store.main.Column;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
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
    private Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
    //private ColumnS columns = ColumnS.getInstance();
    private SQLTypes types = SQLTypes.getInstance();

    public ShowColumns(VFController vf) {
        this.vf = vf;
    }

    @Override
    public void beforeQuery() {
        Arrays.asList(vf.getLbs()).forEach(e -> e.getChildren().clear());

        vf.getTable().getColumns().clear();
        //columns.clearColumn();
        currentTable.getColumns().clear();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String columnName = rs.getString(1).replace("_", " ");
        String typeHole = rs.getString(2).toUpperCase();
        String nulll = rs.getString(3);
        String defaultt = rs.getString(5) == null ? null : rs.getString(5);
        boolean extra = rs.getString(6).equals("auto_increment");
        //TYPE & TYPE-LENGTH------------------------------------
        String type = typeHole;
        int typeLength = -1;
        if(typeHole.contains("(")){
            type = typeHole.substring(0, typeHole.indexOf("("));
            typeLength = Integer.parseInt(typeHole.substring(typeHole.indexOf("(") + 1, typeHole.length() - 1));
        }else if(typeHole.equalsIgnoreCase("INT")){
            typeLength = types.getTypeLength("INT");
        } else if(typeHole.equalsIgnoreCase("SMALLINT")){
            typeLength = types.getTypeLength("SMALLINT");
        } else if(typeHole.equalsIgnoreCase("BIGINT")){
            typeLength = types.getTypeLength("BIGINT");
        }
        //NULL------------------------------------
        boolean nullValue;
        if(nulll.equals("NO")){
            nullValue = false;
        }else{
            nullValue = true;
        }
        //DEFAULT------------------------------------
        vf.getTfs()[row - 1].setPromptText(defaultt);
        //NODES VISIBILITY ----------------------------------------------
        Text textColumnName = new Text(columnName);
        textColumnName.setFill(NonCSS.TEXT_FILL);
        vf.getLbs()[row - 1].getChildren().add(textColumnName);
        vf.getLbs()[row - 1].setVisible(true);
        if(extra){
            vf.getTfs()[row - 1].setPromptText("AUTO_INCREMENT");
        }else if(defaultt == null || defaultt.isEmpty()){
            vf.getTfs()[row - 1].setPromptText(null);
        }
        vf.getTfs()[row - 1].setVisible(true);
        vf.getBtns()[row - 1].setVisible(true);
        //---------------------------------------------
        //columns.addColumn(new Column(columnName, type, typeLength, nullValue, defaultt, extra));
        currentTable.getColumns().add(new Column(columnName, type, typeLength, nullValue, defaultt, extra));
        //---------------------------------------------
        rows = row;
        //ADDING COLUMNS-------------------------------
        final int index = row - 1;
        TableColumn<ObservableList<Object>, Object> column = new TableColumn<>(columnName);
        vf.getTable().getColumns().add(column);
        //column.setCellValueFactory(e -> new ReadOnlyObjectWrapper<>(e.getValue().get(index)));
        column.setCellValueFactory(data -> {
            List<Object> rowValues = data.getValue();
            return index >= 0 && index < rowValues.size()
                         ? new ReadOnlyObjectWrapper<>(rowValues.get(index)) // does just the same as ReadOnlyStringWrapper in this case
                         : null; // no value, if outside of valid index range
        });
        //EDITABLE CELL----------------------------------
        column.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Object>(){
            @Override
            public String toString(Object object) {
                return object != null ? object.toString() : "ERROR";
            }
            @Override
            public Object fromString(String string) {
                return string;
            }
        }));
        column.setOnEditCommit(vf::tableCellEdit);
        //--------------------------------------------------
        
  
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        //MSQL.setColumnsLength(rows);

        String[] columns = new String[rows];
        for (int a = 0; a < rows; a++) {
            columns[a] = ((Text)vf.getLbs()[a].getChildren().get(0)).getText();
        }
        //MSQL.setColumns(columns);
    }

}
