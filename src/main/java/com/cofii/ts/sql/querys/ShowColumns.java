package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.first.VFController;
import com.cofii.ts.first.VFRow;
import com.cofii.ts.first.nodes.RectangelButtonImpl;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.SQLTypes;
import com.cofii.ts.store.main.Column;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.RectangleButton;
import com.cofii2.myInterfaces.IActions;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * Class to reset the columns, their nodes and the table columns
 */
public class ShowColumns implements IActions {

    private int rows;
    private VFController vfc;
    private Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
    // private ColumnS columns = ColumnS.getInstance();
    private SQLTypes types = SQLTypes.getInstance();

    public ShowColumns(VFController vfc) {
        this.vfc = vfc;
    }

    @Override
    public void beforeQuery() {
        // Arrays.asList(vf.getLbs()).forEach(e -> e.getChildren().clear());
        vfc.getGridPane().getChildren().clear();
        vfc.getRows().clear();

        vfc.getTable().getColumns().clear();
        // columns.clearColumn();
        currentTable.getColumns().clear();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String columnName = rs.getString(1);
        String typeHole = rs.getString(2).toUpperCase();
        String nulll = rs.getString(3);
        String defaultt = rs.getString(5) == null ? null : rs.getString(5);
        boolean extra = rs.getString(6).equals("auto_increment");

        VFRow vfRow = new VFRow(columnName.replace("_", " "));
        // TYPE & TYPE-LENGTH------------------------------------
        String type = typeHole;
        int typeLength = -1;
        if (typeHole.contains("(")) {
            type = typeHole.substring(0, typeHole.indexOf("("));
            typeLength = Integer.parseInt(typeHole.substring(typeHole.indexOf("(") + 1, typeHole.length() - 1));
        } else if (typeHole.equalsIgnoreCase("INT")) {
            typeLength = types.getTypeLength("INT");
        } else if (typeHole.equalsIgnoreCase("SMALLINT")) {
            typeLength = types.getTypeLength("SMALLINT");
        } else if (typeHole.equalsIgnoreCase("BIGINT")) {
            typeLength = types.getTypeLength("BIGINT");
        }
        // NULL------------------------------------
        boolean nullValue;
        if (nulll.equals("NO")) {
            nullValue = false;
        } else {
            nullValue = true;
        }
        // DEFAULT ------------------------------------
        vfRow.getTf().setPromptText(defaultt);
        // EXTRA ---------------------------------------
        if (extra) {
            RectangelButtonImpl rectangleButton = new RectangelButtonImpl("AUTO INC", Color.CYAN);
            vfRow.getHbProperty().getChildren().add(rectangleButton);
        }

        currentTable.getColumns().add(new Column(columnName, type, typeLength, nullValue, defaultt, extra));
        // ---------------------------------------------
        rows = row;
        // ADDING COLUMNS =========================================
        // TO VF GRID --------------------------
        vfc.getRows().add(vfRow);

        vfc.getGridPane().getRowConstraints().add(row - 1, new RowConstraints(-1, -1, -1, null, VPos.BOTTOM, true));
        vfc.getGridPane().add(vfRow.getLb(), 0, row - 1);
        vfc.getGridPane().add(vfRow.getVbCenter(), 1, row - 1);
        vfc.getGridPane().add(vfRow.getHbBtns(), 2, row - 1);

        GridPane.setValignment(vfRow.getLb(), VPos.CENTER);

        // TO TABLE ----------------------------
        final int index = row - 1;
        TableColumn<ObservableList<Object>, Object> column = new TableColumn<>(columnName);
        vfc.getTable().getColumns().add(column);

        column.setCellValueFactory(data -> {
            List<Object> rowValues = data.getValue();
            return index >= 0 && index < rowValues.size() ? new ReadOnlyObjectWrapper<>(rowValues.get(index)) : null;
        });
        // EDITABLE CELL----------------------------------
        column.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Object>() {
            @Override
            public String toString(Object object) {
                return object != null ? object.toString() : "ERROR";
            }

            @Override
            public Object fromString(String string) {
                return string;
            }
        }));
        column.setOnEditCommit(vfc::tableCellEdit);
        // --------------------------------------------------

    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        // MSQL.setColumnsLength(rows);
        /*
         * ??????????????????? String[] columns = new String[rows]; for (int a = 0; a <
         * rows; a++) { columns[a] =
         * ((Text)vfc.getLbs()[a].getChildren().get(0)).getText(); }
         */
        // MSQL.setColumns(columns);
    }

}
