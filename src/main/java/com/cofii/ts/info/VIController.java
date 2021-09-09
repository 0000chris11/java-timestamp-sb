package com.cofii.ts.info;

import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.FK;
import com.cofii.ts.store.FKS;
import com.cofii.ts.store.PK;
import com.cofii.ts.store.PKS;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class VIController implements Initializable {

    @FXML
    private HBox hbColumnName;
    @FXML
    private HBox hbType;
    @FXML
    private HBox hbTypeLength;
    @FXML
    private HBox hbNull;
    @FXML
    private HBox hbPK;
    @FXML
    private HBox hbFK;
    @FXML
    private HBox hbDefault;
    @FXML
    private HBox hbExtra;
    @FXML
    private HBox hbDist;
    @FXML
    private HBox hbImageC;
    // --------------------------------------
    @FXML
    private GridPane gridPaneLeft;
    @FXML
    private GridPane gridPaneRight;
    // --------------------------------------
    //private ColumnS columns = ColumnS.getInstance();
    //private ColumnDS columnsd = ColumnDS.getInstance();
    private PKS pks = PKS.getInstance();
    private FKS fks = FKS.getInstance();

    // NON-FXML
    private Label[] lbColumns = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbTypes = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbTypeslength = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbNulls = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbPK = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbFK = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbDefaults = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbExtras = new Label[MSQL.MAX_COLUMNS];

    private Label[] lbDist = new Label[MSQL.MAX_COLUMNS];
    private Label[] lbImageC = new Label[MSQL.MAX_COLUMNS];

    // -----------------------------------------------------------
    private void nonFXMLNodeInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            lbColumns[a] = new Label();
            lbTypes[a] = new Label();
            lbTypeslength[a] = new Label();
            lbNulls[a] = new Label();
            lbPK[a] = new Label();
            lbFK[a] = new Label();
            lbDefaults[a] = new Label();
            lbExtras[a] = new Label();

            lbDist[a] = new Label();
            lbImageC[a] = new Label();

            int row = a + 1;
            gridPaneLeft.add(lbColumns[a], 0, row);
            gridPaneLeft.add(lbTypes[a], 1, row);
            gridPaneLeft.add(lbTypeslength[a], 2, row);
            gridPaneLeft.add(lbNulls[a], 3, row);
            gridPaneLeft.add(lbPK[a], 4, row);
            gridPaneLeft.add(lbFK[a], 5, row);
            gridPaneLeft.add(lbDefaults[a], 6, row);
            gridPaneLeft.add(lbExtras[a], 7, row);

            gridPaneRight.add(lbDist[a], 0, row);
            gridPaneRight.add(lbImageC[a], 1, row);
        }
    }

    private void nonFXMLNodeSet() {
        Table table = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        int length = table.getColumns().size();
        for (int a = 0; a < length; a++) {
            String column = table.getColumns().get(a).getName();
            String type = table.getColumns().get(a).getType();
            int typeLength = table.getColumns().get(a).getTypeLength();
            boolean nulll = table.getColumns().get(a).getNulll();
            String defaultt = table.getColumns().get(a).getDefaultt();
            String extra = table.getColumns().get(a).getExtra() ? "Yes" : "No";

            String dist = table.getColumns().get(a).getDist() ? "Yes" : "No";
            String imageC = table.getColumns().get(a).getImageC() ? "Yes" : "No";

            lbColumns[a].setText(column);
            lbTypes[a].setText(type);
            lbTypeslength[a].setText(Integer.toString(typeLength));
            lbNulls[a].setText(Boolean.toString(nulll));
            lbPK[a].setText("No");
            lbFK[a].setText("No");

            if (defaultt != null) {
                lbDefaults[a].setText(defaultt);
            }else{
                lbDefaults[a].setText("null");
                lbDefaults[a].setTextFill(Color.DARKGRAY);
            }

            if (extra.equals("Yes")) {
                lbExtras[a].setText("AUTO INCREMENT");
                lbExtras[a].setStyle("-fx-font-weight: bold;");
            } else {
                lbExtras[a].setText("No");
            }

            lbDist[a].setText(dist);
            if (dist.equals("Yes")) {
                lbDist[a].setStyle("-fx-font-weight: bold;");
            }

            if (imageC.equals("Yes")) {
                lbImageC[a].setText(imageC + "\n" + table.getImageCPaths());
            } else {
                lbImageC[a].setText("No");
            }
        }
        // PRIMARY KEYS---------------------------------------------
        PK[] cpks = pks.getCurrentTablePKS();
        for (int a = 0; a < cpks.length; a++) {
            cpks[a].getColumns().forEach(cols -> {
                int ordinalPosition = cols.index - 1;
                lbPK[ordinalPosition].setText("Yes");
                lbPK[ordinalPosition].setTextFill(NonCSS.TEXT_FILL_PK);
                lbPK[ordinalPosition].setStyle("-fx-font-weight: bold;");
            });
        }
        // PRIMARY KEYS---------------------------------------------
        FK[] cfks = fks.getCurrentTableFKS();
        for (int a = 0; a < cfks.length; a++) {
            cfks[a].getColumns().forEach(cols -> {
                int ordinalPosition = cols.index - 1;
                lbFK[ordinalPosition].setText("Yes");
                lbPK[ordinalPosition].setTextFill(NonCSS.TEXT_FILL_FK);
                lbPK[ordinalPosition].setStyle("-fx-font-weight: bold;");
            });
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TOP--------------------
        hbColumnName.setStyle(CSS.NEW_ROW);
        hbType.setStyle(CSS.NEW_ROW);
        hbTypeLength.setStyle(CSS.NEW_ROW);
        hbNull.setStyle(CSS.NEW_ROW);
        hbPK.setStyle(CSS.NEW_ROW);
        hbFK.setStyle(CSS.NEW_ROW);
        hbDefault.setStyle(CSS.NEW_ROW);
        hbExtra.setStyle(CSS.NEW_ROW);

        hbDist.setStyle(CSS.NEW_ROW);
        hbImageC.setStyle(CSS.NEW_ROW);
        // -----------------------
        nonFXMLNodeInit();
        nonFXMLNodeSet();

        gridPaneLeft.setGridLinesVisible(true);
        gridPaneRight.setGridLinesVisible(true);
    }

}
