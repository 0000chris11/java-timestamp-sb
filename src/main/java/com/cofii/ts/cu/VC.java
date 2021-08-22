package com.cofii.ts.cu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.FK;
import com.cofii.ts.store.FKS;
import com.cofii.ts.store.PKS;
import com.cofii.ts.store.UpdateTable;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.SceneZoom;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class VC {

    private VCController vcc;
    private MSQLP ms;
    private Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();

    private DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0);
    // private UpdateTable updateTable;

    //LISTENERS----------------------------------------
    
    // -----------------------------------------------------
    private void rowDisplay(int size) {
        for (int a = 0; a < size; a++) {
            int row = a + 1;

            vcc.getGridPaneLeft().add(vcc.getHbsN().get(a), 0, row);
            vcc.getGridPaneLeft().add(vcc.getHbsName().get(a), 1, row);
            vcc.getGridPaneLeft().add(vcc.getHbsType().get(a), 2, row);
            vcc.getGridPaneLeft().add(vcc.getHbsNull().get(a), 3, row);
            vcc.getGridPaneLeft().add(vcc.getHbsPK().get(a), 4, row);
            vcc.getGridPaneLeft().add(vcc.getHbsFK().get(a), 5, row);
            vcc.getGridPaneLeft().add(vcc.getHbsDefault().get(a), 6, row);
            vcc.getGridPaneLeft().add(vcc.getHbsExtra().get(a), 7, row);

            GridPane.setValignment(vcc.getLbsN().get(a), VPos.TOP);
            // RIGHT-----------------------------------------
            vcc.getGridPaneRight().add(vcc.getBtnsDist().get(a), 0, row);
            vcc.getGridPaneRight().add(vcc.getBtnsImageC().get(a), 1, row);

            GridPane.setValignment(vcc.getBtnsDist().get(a), VPos.TOP);
            GridPane.setValignment(vcc.getBtnsImageC().get(a), VPos.TOP);
        }
    }

    private void createOption() {
        setTextFill(true);
        vcc.createHelpPopupReset();
        vcc.pesetListInit(vcc.getCurrentRowLength());
        // TOP--------------------------------------------------------
        vcc.getBtnRenameTable().setVisible(false);
        rowDisplay(vcc.getPresetRowsLenght());
        // LEFT-------------------------------------------------------
        vcc.getGridPaneLeft().getRowConstraints().forEach(e -> {
            e.setValignment(VPos.TOP);
            e.setVgrow(Priority.NEVER);
        });
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            vcc.getBtnsRemoveColumn().get(a).setOnAction(vcc::btnsRemoveCreateAction);
            vcc.getBtnsAddColumn().get(a).setOnAction(vcc::btnsAddCreateAction);

            vcc.getBtnsRenameColumn().get(a).setVisible(false);
            vcc.getBtnsChangeType().get(a).setVisible(false);
            vcc.getBtnsChangeNull().get(a).setVisible(false);
            vcc.getBtnsChangeDefault().get(a).setVisible(false);
        }
        vcc.btnAddRemoveColumnInit();
        // LEFT-BOTTOM------------------------------------------------
        vcc.getHbLeftUpdate().setDisable(true);
        /*
        vcc.getBtnUpdatePK().setDisable(true);
        vcc.getBtnUpdateFK().setDisable(true);
        vcc.getBtnUpdateExtra().setDisable(true);
        */
        // RIGHT ROW-------------------------------------------------------
        vcc.getGridPaneRight().getRowConstraints().forEach(e -> {
            e.setValignment(VPos.TOP);
            e.setVgrow(Priority.NEVER);
        });
        // RIGHT-BOTTOM------------------------------------------------
        //vcc.getHbRightUpdate().setDisable(true);
        vcc.getBtnUpdateDist().setDisable(true);
        vcc.getBtnUpdateImageC().setDisable(true);
    }

    private void setUpdateStore() {
        PKS pks = PKS.getInstance();
        FKS fks = FKS.getInstance();

        //ColumnDS columnds = ColumnDS.getInstance();
        int columnCount = currentTable.getColumns().size();
        String tableName = currentTable.getName().replace("_", " ");

        List<String> columnsName = new ArrayList<>(currentTable.getColumnNames());
        List<String> types = new ArrayList<>(currentTable.getColumnTypes());
        List<Integer> typesLength = new ArrayList<>(currentTable.getColumnTypeLengths());
        List<Boolean> nulls = new ArrayList<>(currentTable.getColumnNulls());

        List<String> cpks = new ArrayList<>(Arrays.asList(pks.getYesAndNoPKS()));

        FK[] cfks = fks.getCurrentTableFKS();
        List<String> yfks = new ArrayList<>(Arrays.asList(fks.getYesAndNoFKS()));
        String[] fksFormed = new String[currentTable.getColumns().size()];

        List<Object> defaults = new ArrayList<>(currentTable.getColumnDefaults());
        int extra = currentTable.getExtra();

        List<Boolean> dists = new ArrayList<>(currentTable.getDistList());
        List<Boolean> imageCS = new ArrayList<>(currentTable.getImageCList());
        //List<String> imageCPathList = Arrays.asList(columnds.getImageCPaths());
        //String imageCPath = "NONE";

        // ----------------------------------------------------
        vcc.getTfTable().setText(tableName);
        for (int a = 0; a < columnCount; a++) {
            vcc.getTfsColumn().get(a).setText(columnsName.get(a).replace("_", " "));
            vcc.getTfasType().get(a).setText(types.get(a));
            vcc.getTfsTypeLength().get(a).setText(Integer.toString(typesLength.get(a)));
            vcc.getCksNull().get(a).setSelected(nulls.get(a));
            vcc.getRbsPK().get(a).setSelected(cpks.get(a).equals("Yes"));
            /*
             * if (fks.get(a) != null) {// NOT TESTED vcc.getCksFK()[a].setSelected(true);
             * fksFormed[a] = fks.get(a).getString2() + "." + fks.get(a).getString3() + "."
             * + fks.get(a).getString4(); vcc.getTfasFK()[a].setText(fksFormed[a]); }
             */
            // vcc.getBtnsSelectedFK()[a].setDisable(true);

            final int aa = a;
            Arrays.asList(cfks).forEach(fk -> {
                StringBuilder sb = new StringBuilder();
                sb.append(fk.getReferencedDatabase()).append(".");
                sb.append(fk.getReferencedTable()).append(" (");
                fk.getReferencedColumns().forEach(s -> sb.append(s).append(","));
                sb.deleteCharAt(sb.length() - 1).append(")");// TEST

                fk.getColumns().forEach(is -> {
                    if (is.index - 1 == aa) {
                        // vcc.getCksFK()[aa].setSelected(true);
                        // vcc.getBtnsSelectedFK()[aa].setDisable(false);

                        fksFormed[aa] = sb.toString();
                        vcc.getTfasFK().get(aa).setText(fksFormed[aa]);
                        vcc.getTfasFK().get(aa).setVisible(true);

                        vcc.getBtnsSelectedFK().get(aa).setText("REM");
                    }
                });
                // FOR ONLY ONE MIX FOREIGN KEY
                vcc.getBtnsSelectedFK().stream().filter(btn -> btn.getText().equals("REM"))
                        .forEach(btn -> btn.setText("REM (A)"));
            });
            // fksFormed[a] = pks.get

            if (defaults.get(a) != null) {
                vcc.getCksDefault().get(a).setSelected(true);
                vcc.getTfsDefault().get(a).setVisible(true);
                vcc.getTfsDefault().get(a).setText(defaults.get(a).toString());
            } else {
                vcc.getCksDefault().get(a).setSelected(false);
            }
            vcc.getRbsExtra().get(a).setSelected(extra == a);
            // DISTS---------------------------------------------
            vcc.getBtnsDist().get(a).setSelected(dists.get(a));
            vcc.getBtnsImageC().get(a).setSelected(imageCS.get(a));// ERROR IF THERE IS MORE THAN ONE
            
        }
        if (!currentTable.getImageCPath().equals("NONE")) {
            vcc.getTfImageCPath().setText(currentTable.getImageCPath());
        }

        // ----------------------------------------------------
        // updateTable = new UpdateTable(table, columnsName, types, typesLength, nulls,
        // pks, fks, fksFormed, defaults, extra);
        UpdateTable updateTable = new UpdateTable();
        updateTable.setTable(tableName);
        updateTable.setColumns(columnsName);
        updateTable.setTypes(types);
        updateTable.setTypesLength(typesLength);
        updateTable.setNulls(nulls);
        updateTable.setPks(cpks);
        updateTable.setFks(yfks);
        // updateTable.setFksConstraint(fksConstraint);
        updateTable.setFkFormed(new ArrayList<>(Arrays.asList(fksFormed)));
        updateTable.setDefaults(defaults);
        updateTable.setExtra(extra);

        updateTable.setDistHole(currentTable.getDist());
        updateTable.setDists(dists);
        //updateTable.setDistYN(dists);
        updateTable.setImageCHole(currentTable.getImageC());
        updateTable.setImageCS(imageCS);
        updateTable.setImageCPathHole(currentTable.getImageCPath());

        updateTable.setRowLength(columnCount);
        vcc.setUpdateTable(updateTable);
    }

    private void setTextFill(boolean create) {

        if (create) {
            for (int a = 0; a < vcc.getCurrentRowLength(); a++) {
                vcc.getCksNull().get(a).applyCss();
                vcc.getCksNull().get(a).setStyle(CSS.CKS_BG);
                // vcc.getCksNull().get(a).setBackground(new Background(new BackgroundFill(,
                // CornerRadii.EMPTY, Insets.EMPTY)));
                vcc.getCksDefault().get(a).setStyle(CSS.CKS_BG);
            }
        } else {
            vcc.getTfTable().setStyle(CSS.TEXT_FILL_HINT);
            for (int a = 0; a < currentTable.getColumns().size() ; a++) {
                vcc.getTfsColumn().get(a).setStyle(CSS.TEXT_FILL_HINT);
                vcc.getTfasType().get(a).setStyle(CSS.TEXT_FILL_HINT);
                vcc.getTfsTypeLength().get(a).setStyle(CSS.TEXT_FILL_HINT);
                // vcc.getCksNull().get(a).setStyle(CSS.TEXT_FILL_HINT);
                vcc.getCksNull().get(a).setStyle(CSS.CKS_BG_HINT);
                vcc.getCksDefault().get(a).setStyle(CSS.CKS_BG_HINT);
                vcc.getTfsDefault().get(a).setStyle(CSS.TEXT_FILL_HINT);
            }
        }
    }

    private void updateOption() {
        setUpdateStore();
        setTextFill(false);
        vcc.createAddColumnHelpPopupReset();
        vcc.pesetListInit(currentTable.getColumns().size());
        // TOP-------------------------------------------------------
        vcc.getBtnRenameTable().setVisible(true);
        vcc.getBtnRenameTable().setOnAction(vcc::btnRenameTableAction);
        // LEFT------------------------------------------------------
        rowDisplay(currentTable.getColumns().size());
        vcc.setCurrentRowLength(currentTable.getColumns().size());
        for (int a = 0; a < currentTable.getColumns().size(); a++) {
            vcc.getBtnsRemoveColumn().get(a).setOnAction(vcc::btnsRemoveUpdateAction);
            vcc.getBtnsAddColumn().get(a).setOnAction(vcc::btnsColumnSetVisibleAction);

            if (a == 0) {
                vcc.getBtnsAddColumn().get(a).setContextMenu(vcc.getBeforeAfterOptionMenu());
                vcc.getBtnsAddColumn().get(a).setTooltip(vcc.getBeforeAfterOptionTooltip());
            }
            vcc.getBtnsRenameColumn().get(a).setOnAction(vcc::btnsRenameColumn);
            vcc.getBtnsChangeType().get(a).setOnAction(vcc::btnsChangeType);
            vcc.getCksNull().get(a).setOnAction(vcc::cksNullAction);
            vcc.getBtnsChangeNull().get(a).setOnAction(vcc::btnsChangeNull);
            //vcc.getBtnsSelectedFK().get(a).setOnAction(vcc::btnsSelectedFK);
            vcc.getBtnsChangeDefault().get(a).setOnAction(vcc::btnsChangeDefault);

            vcc.getBtnsRenameColumn().get(a).setVisible(true);
            vcc.getBtnsChangeType().get(a).setVisible(true);
            vcc.getBtnsChangeNull().get(a).setVisible(true);
            vcc.getBtnsChangeDefault().get(a).setVisible(true);
        }
        //vcc.getRbsPK().forEach(e -> e.setOnAction(vcc::cksPKAction));
        // LEFT-BOTTOM------------------------------------------------
        vcc.getBtnUpdatePK().setOnAction(vcc::btnUpdatePK);
        vcc.getBtnUpdateFK().setOnAction(vcc::btnUpdateFKS);
        vcc.getBtnUpdateExtra().setOnAction(vcc::btnUpdateExtra);

        vcc.getLbUpdateLeft().setDisable(false);

        vcc.getBtnUpdatePK().setId(Integer.toString(-1));
        vcc.getBtnUpdateFK().setId(Integer.toString(-1));
        vcc.getBtnUpdateExtra().setId(Integer.toString(-1));
        // RIGHT-BOTTOM------------------------------------------------
        vcc.getBtnUpdateDist().setOnAction(vcc::btnUpdateDist);
        vcc.getBtnUpdateImageC().setOnAction(vcc::btnUpdateImageC);

        vcc.getBtnUpdateDist().setId(Integer.toString(-1));
        vcc.getBtnUpdateImageC().setId(Integer.toString(-1));
        // BOTTOM-----------------------------------------------------
        vcc.getBtnCreateUpdate().setVisible(false);
        vcc.setUpdateControl(true);
    }

    // -----------------------------------------------------
    public VC(VFController vf, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VC.class.getResource("/com/cofii/ts/cu/VC.fxml"));

            
            SceneZoom sz = new SceneZoom(loader.load(), zoomProperty);
            vcc = (VCController) loader.getController();
            sz.setParent(vcc.getBpMain());
            Scene scene = sz.getScene();

            scene.getStylesheets().add(VC.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
            vf.getStage().setScene(scene);
            // ------------------------------------------------------
            vcc.setVf(vf);
            ms = vf.getMs();
            vcc.setMs(ms);
            //LISTENERS------------------------------------
            //vf.getStage().maximizedProperty().addListener((obs, oldValue, newValue) -> stageMaximizedPropertyChange(newValue));
            // ------------------------------------------------------
            if (create) {// THE REASON FOR NOT ADDING THE NODES IN THE CONTROLLER
                createOption();
                System.out.println(CC.CYAN + "\nCREATE TABLE" + CC.RESET);
            } else {
                System.out.println(CC.CYAN + "\nUPDATE TABLE" + CC.RESET);
                updateOption();
            }
            // OTHERS----------------------------
            boolean disable = vcc.getBtnsImageC().stream().anyMatch(btn -> btn.isSelected());
            vcc.getTfImageCPath().setDisable(!disable);
            vcc.getBtnSelectImageC().setDisable(!disable);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
