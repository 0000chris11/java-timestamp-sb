package com.cofii.ts.info;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.main.FK;
import com.cofii.ts.store.main.PK;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class VIController implements Initializable {

    // TOP----------------------------
    @FXML
    private HBox hbTop;
    @FXML
    private ScrollPane scGridPaneTop;
    @FXML
    private GridPane gridPaneTop;
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
    // BOTTOM --------------------------------------
    @FXML
    private HBox hbBottom;
    // CUSTOM------------
    @FXML
    private VBox vbCustom;
    @FXML
    private HBox hbCustomHeader;
    @FXML
    private ScrollPane scGridPaneCustom;
    @FXML
    private GridPane gridPaneCustom;
    @FXML
    private HBox hbDist;
    @FXML
    private HBox hbTextArea;
    // IMAGEC-------------
    @FXML
    private VBox vbImageC;
    @FXML
    private HBox hbImageCHeader;
    @FXML
    private GridPane gridPaneImageC;
    @FXML
    private HBox hbImageCColumn;
    @FXML
    private Label lbImageCColumn;
    @FXML
    private HBox hbImageCLength;
    @FXML
    private Label lbImageCLength;
    @FXML
    private HBox hbImageCDisplayOrder;
    @FXML
    private Label lbImageCDisplayOrder;
    @FXML
    private HBox hbImageCType;
    @FXML
    private Label lbImageCType;
    // IMAGEC PATH----------------------------
    @FXML
    private VBox vbImageCPathMain;
    @FXML
    private HBox hbImageCPathHeader;
    @FXML
    private ScrollPane scImageCPaths;
    @FXML
    private VBox vbImageCPath;

    // --------------------------------------
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
    private Label[] lbTextAreas = new Label[MSQL.MAX_COLUMNS];

    private Label[] lbImageC = new Label[MSQL.MAX_COLUMNS];

    private Label[] lbPath = new Label[MSQL.MAX_COLUMNS];

    // -----------------------------------------------------------
    private void nonFXMLNodeInit() {
        Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        int columnsLength = currentTable.getColumns().size();

        for (int a = 0; a < columnsLength; a++) {
            lbColumns[a] = new Label();
            lbTypes[a] = new Label();
            lbTypeslength[a] = new Label();
            lbNulls[a] = new Label();
            lbPK[a] = new Label();
            lbFK[a] = new Label();
            lbDefaults[a] = new Label();
            lbExtras[a] = new Label();

            lbDist[a] = new Label();
            lbTextAreas[a] = new Label("No");

            int row = a + 1;
            gridPaneTop.add(lbColumns[a], 0, row);
            gridPaneTop.add(lbTypes[a], 1, row);
            gridPaneTop.add(lbTypeslength[a], 2, row);
            gridPaneTop.add(lbNulls[a], 3, row);
            gridPaneTop.add(lbPK[a], 4, row);
            gridPaneTop.add(lbFK[a], 5, row);
            gridPaneTop.add(lbDefaults[a], 6, row);
            gridPaneTop.add(lbExtras[a], 7, row);

            gridPaneCustom.add(lbDist[a], 0, row);
            gridPaneCustom.add(lbTextAreas[a], 1, row);

        }
        int pathsLength = currentTable.getImageCPaths().size();
        for (int a = 0; a < pathsLength; a++) {
            lbPath[a] = new Label();
            vbImageCPath.getChildren().add(lbPath[a]);
        }
    }

    private void nonFXMLNodeSet() {
        Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        int length = currentTable.getColumns().size();

        for (int a = 0; a < length; a++) {
            String column = currentTable.getColumns().get(a).getName();
            String type = currentTable.getColumns().get(a).getType();
            int typeLength = currentTable.getColumns().get(a).getTypeLength();
            boolean nulll = currentTable.getColumns().get(a).getNulll();
            String defaultt = currentTable.getColumns().get(a).getDefaultt() != null
                    ? currentTable.getColumns().get(a).getDefaultt().toString()
                    : null;
            String extra = currentTable.getColumns().get(a).getExtra() ? "Yes" : "No";

            String dist = currentTable.getColumns().get(a).getDist() ? "Yes" : "No";

            lbColumns[a].setText(column);
            lbTypes[a].setText(type);
            lbTypeslength[a].setText(Integer.toString(typeLength));
            lbNulls[a].setText(Boolean.toString(nulll));
            lbPK[a].setText("No");
            lbFK[a].setText("No");

            if (defaultt != null) {
                lbDefaults[a].setText(defaultt);
            } else {
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

        }
        // KEYS---------------------------------------------
        List<PK> cpks = currentTable.getPKS();
        for (int a = 0; a < cpks.size(); a++) {
            cpks.forEach(pk -> {
                int ordinalPosition = pk.getOrdinalPosition() - 1;
                lbPK[ordinalPosition].setText("Yes");
                lbPK[ordinalPosition].setTextFill(NonCSS.TEXT_FILL_PK);
                lbPK[ordinalPosition].setStyle("-fx-font-weight: bold;");
            });
        }
        List<FK> cfks = currentTable.getFKS();
        for (int a = 0; a < cfks.size(); a++) {
            // REQUIERES WAY MORE INFO !!!!!!!!!!!!!!!
            cfks.forEach(fk -> {
                int ordinalPosition = fk.getOrdinalPosition() - 1;
                lbFK[ordinalPosition].setText("Yes");
                lbFK[ordinalPosition].setTextFill(NonCSS.TEXT_FILL_FK);
                lbFK[ordinalPosition].setStyle("-fx-font-weight: bold;");
            });
        }
        // IMAGEC----------------------------------------------------
        if (!currentTable.getImageCPaths().isEmpty()) {
            String imageCColumn = currentTable.getImageCColumnName();
            String imageCLength = Integer.toString(currentTable.getImageCLength());
            String imageCDisplayOrder = currentTable.getDisplayOrder();
            String imageCType = currentTable.getImageType();

            lbImageCColumn.setText(imageCColumn);
            lbImageCLength.setText(imageCLength);
            lbImageCDisplayOrder.setText(imageCDisplayOrder);
            lbImageCType.setText(imageCType);

            int pathsLength = currentTable.getImageCPaths().size();
            for (int a = 0; a < pathsLength; a++) {
                lbPath[a].setText(currentTable.getImageCPaths().get(a).getPathName());
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPaneTop.minWidthProperty().bind(scGridPaneTop.widthProperty());
        // TOP
        hbColumnName.getStyleClass().add("vi-header");
        hbType.getStyleClass().add("vi-header");
        hbTypeLength.getStyleClass().add("vi-header");
        hbNull.getStyleClass().add("vi-header");
        hbPK.getStyleClass().add("vi-header");
        hbFK.getStyleClass().add("vi-header");
        hbDefault.getStyleClass().add("vi-header");
        hbExtra.getStyleClass().add("vi-header");
        // CUSTOM
        hbCustomHeader.getStyleClass().add("vi-header");
        hbDist.getStyleClass().add("vi-sub-header");
        hbTextArea.getStyleClass().add("vi-sub-header");
        // IMAGEC
        hbImageCHeader.getStyleClass().add("vi-header");
        hbImageCPathHeader.getStyleClass().add("vi-header");
        // -----------------------
        nonFXMLNodeInit();
        nonFXMLNodeSet();

        gridPaneTop.setGridLinesVisible(true);
        gridPaneCustom.setGridLinesVisible(true);
    }

}
