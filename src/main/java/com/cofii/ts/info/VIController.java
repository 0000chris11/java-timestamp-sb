package com.cofii.ts.info;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.ColumnDS;
import com.cofii.ts.store.ColumnS;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class VIController implements Initializable {
    @FXML
    private GridPane gridPaneLeft;
    @FXML
    private GridPane gridPaneRight;

    private ColumnS columns = ColumnS.getInstance();
    private ColumnDS columnsd = ColumnDS.getInstance();

    // NON-FXML
    private Label[] lbColumns = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbTypes = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbTypeslength = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbNulls = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbKeys = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbDefaults = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbExtras = new Label[MSQL.MAX_COLUMNS];

    private Label[] lbDist = new Label[MSQL.MAX_COLUMNS];
    // -----------------------------------------------------------
    private void nonFXMLNodeInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            lbColumns[a] = new Label();
            lbTypes[a] = new Label();
            lbTypeslength[a] = new Label();
            lbNulls[a] = new Label();
            lbKeys[a] = new Label();
            lbDefaults[a] = new Label();
            lbExtras[a] = new Label();

            lbDist[a] = new Label();

            int row = a + 1;
            gridPaneLeft.add(lbColumns[a], 0, row);
            gridPaneLeft.add(lbTypes[a], 1, row);
            gridPaneLeft.add(lbTypeslength[a], 2, row);
            gridPaneLeft.add(lbNulls[a], 3, row);
            gridPaneLeft.add(lbKeys[a], 4, row);
            gridPaneLeft.add(lbDefaults[a], 5, row);
            gridPaneLeft.add(lbExtras[a], 6, row);

            gridPaneRight.add(lbDist[a], 0, row);
        }
    }
    private void nonFXMLNodeSet(){
        int length = columns.size();
        for (int a = 0; a < length; a++) {
            String column = columns.getColumn(a);
            String type = columns.getType(a);
            int typeLength = columns.getTypeLength(a);
            boolean nulll = columns.getNull(a);
            String key = columns.getKey(a);
            String defaultt = columns.getDefault(a);
            String extra = columns.getExtra(a);

            String dist = columnsd.getDist(a);

            lbColumns[a].setText(column);
            lbTypes[a].setText(type);
            lbTypeslength[a].setText(Integer.toString(typeLength));
            lbNulls[a].setText(Boolean.toString(nulll));
            lbKeys[a].setText(key);
            lbDefaults[a].setText(defaultt);
            lbExtras[a].setText(extra);

            lbDist[a].setText(dist);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nonFXMLNodeInit();
        nonFXMLNodeSet();
    }

}
