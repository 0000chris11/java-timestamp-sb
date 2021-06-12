package com.cofii.ts.cu;

import java.io.IOException;
import java.util.Arrays;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.Dist;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectKeys;
import com.cofii.ts.store.ColumnDS;
import com.cofii.ts.store.ColumnS;
import com.cofii.ts.store.Keys;
import com.cofii.ts.store.UpdateTable;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;
import com.cofii2.stores.IntDString;
import com.cofii2.stores.QString;
import com.cofii2.stores.TString;

import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class VC {

    private VCController vc;
    private MSQLP ms;
    private ColumnS columns = ColumnS.getInstance();

    private UpdateTable updateTable;

    // -----------------------------------------------------
    private void rowDisplay(int size) {
        for (int a = 0; a < size; a++) {
            int row = a + 1;

            vc.getGridPaneLeft().add(vc.getLbsN()[a], 0, row);
            vc.getGridPaneLeft().add(vc.getHbsName()[a], 1, row);
            vc.getGridPaneLeft().add(vc.getHbsType()[a], 2, row);
            vc.getGridPaneLeft().add(vc.getHbsNull()[a], 3, row);
            vc.getGridPaneLeft().add(vc.getHbsPK()[a], 4, row);
            vc.getGridPaneLeft().add(vc.getHbsFK()[a], 5, row);
            vc.getGridPaneLeft().add(vc.getHbsDefault()[a], 6, row);
            vc.getGridPaneLeft().add(vc.getHbsExtra()[a], 7, row);

            GridPane.setValignment(vc.getLbsN()[a], VPos.TOP);
            // RIGHT-----------------------------------------
            vc.getGridPaneRight().add(vc.getBtnsDist()[a], 0, row);
            vc.getGridPaneRight().add(vc.getBtnsImageC()[a], 1, row);

            GridPane.setValignment(vc.getBtnsDist()[a], VPos.TOP);
            GridPane.setValignment(vc.getBtnsImageC()[a], VPos.TOP);
        }
    }

    private void createOption() {
        vc.pesetListInit(vc.getCurrentRowLength());
        // TOP--------------------------------------------------------
        vc.getBtnRenameTable().setVisible(false);
        rowDisplay(vc.getPresetRowsLenght());
        // LEFT-------------------------------------------------------
        vc.getGridPaneLeft().getRowConstraints().forEach(e -> {
            e.setValignment(VPos.TOP);
            e.setVgrow(Priority.NEVER);
        });
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            vc.getBtnsRemoveColumn()[a].setOnAction(vc::btnsRemoveCreateAction);
            vc.getBtnsAddColumn()[a].setOnAction(vc::btnsAddCreateAction);

            vc.getBtnsRenameColumn()[a].setVisible(false);
            vc.getBtnsChangeType()[a].setVisible(false);
            vc.getBtnsChangeNull()[a].setVisible(false);
            vc.getBtnsChangePK()[a].setVisible(false);
            vc.getBtnsChangeFK()[a].setVisible(false);
            vc.getBtnsChangeDefault()[a].setVisible(false);
        }
        vc.btnAddRemoveColumnInit();
        // LEFT-BOTTOM------------------------------------------------
        vc.getBtnUpdatePK().setDisable(true);
        vc.getBtnUpdateFK().setDisable(true);
        vc.getBtnUpdateExtra().setDisable(true);
        // RIGHT ROW-------------------------------------------------------
        vc.getGridPaneRight().getRowConstraints().forEach(e -> {
            e.setValignment(VPos.TOP);
            e.setVgrow(Priority.NEVER);
        });
        // RIGHT-BOTTOM------------------------------------------------
        vc.getBtnUpdateDist().setDisable(true);
    }

    private void setUpdateStore() {
        Keys keys = Keys.getInstance();
        ColumnDS columnds = ColumnDS.getInstance();
        int columnCount = columns.size();

        String table = MSQL.getCurrentTable().getName().replace("_", " ");
        String[] columnsName = columns.getColumns();
        String[] types = columns.getTypes();
        int[] typesLength = columns.getTypesLength();
        boolean[] nulls = columns.getNulls();
        String[] pks = keys.getPKS();
        QString[] fks = keys.getFKS();
        String[] fksFormed = new String[columns.size()];
        String[] defaults = columns.getDefaults();
        int extra = columns.getExtra();

        String[] dists = columnds.getDists();
        String[] imageCS = columnds.getImageCS();
        String[] imageCPath = columnds.getImageCPaths();
        // ----------------------------------------------------
        
        vc.getTfTable().setText(table);
        for (int a = 0; a < columnCount; a++) {
            vc.getTfsColumn()[a].setText(columnsName[a].replace("_", " "));
            vc.getTfasType()[a].setText(types[a]);
            vc.getTfsTypeLength()[a].setText(Integer.toString(typesLength[a]));
            vc.getCksNull()[a].setSelected(nulls[a]);
            vc.getCksPK()[a].setSelected(pks[a].equals("Yes"));
            if (fks[a] != null) {//NOT TESTED
                vc.getCksFK()[a].setSelected(true);
                fksFormed[a] = fks[a].getString2() + "." + fks[a].getString3() + "." + fks[a].getString4();
                vc.getTfasFK()[a].setText(fksFormed[a]);
            }

            if (defaults[a] != null) {
                vc.getCksDefault()[a].setSelected(true);
                vc.getTfsDefault()[a].setText(defaults[a]);
            } else {
                vc.getCksDefault()[a].setSelected(false);
            }
            vc.getRbsExtra()[a].setSelected(extra == a);
            // DISTS---------------------------------------------
            vc.getBtnsDist()[a].setSelected(dists[a].equals("Yes"));
            vc.getBtnsImageC()[a].setSelected(imageCS[a].equals("Yes"));// ERROR IF THERE IS MORE THAN ONE
            if (!imageCPath[a].equals("NONE")) {
                vc.getTfImageCPath().setText(imageCPath[a]);
            }
        }

        // ----------------------------------------------------
        //updateTable = new UpdateTable(table, columnsName, types, typesLength, nulls, pks, fks, fksFormed, defaults, extra);
        updateTable = new UpdateTable();
        updateTable.setTable(table);
        updateTable.setColumns(columnsName);
        updateTable.setTypes(types);
        updateTable.setTypesLength(typesLength);
        updateTable.setNulls(nulls);
        updateTable.setPks(pks);
        updateTable.setFks(fks);
        updateTable.setFkFormed(fksFormed);
        updateTable.setDefaults(defaults);
        updateTable.setExtra(extra);

        updateTable.setDist(dists);
        updateTable.setImageC(imageCS);
        updateTable.setImageCPath(imageCPath);
        
        updateTable.setRowLength(columnCount);
        vc.setUpdateTable(updateTable);
    }

    private void updateOption() {
        vc.pesetListInit(columns.size());
        setUpdateStore();
        // TOP-------------------------------------------------------
        vc.getBtnRenameTable().setVisible(true);
        // LEFT------------------------------------------------------
        rowDisplay(columns.size());
        vc.setCurrentRowLength(columns.size());
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            vc.getBtnsRemoveColumn()[a].setOnAction(vc::btnsRemoveUpdateAction);
            vc.getBtnsAddColumn()[a].setOnAction(vc::btnsAddUpdateAction);

            vc.getBtnsRenameColumn()[a].setVisible(true);
            vc.getBtnsChangeType()[a].setVisible(true);
            vc.getBtnsChangeNull()[a].setVisible(true);
            vc.getBtnsChangePK()[a].setVisible(true);// DELETE
            vc.getBtnsChangeFK()[a].setVisible(true);// DELETE
            vc.getBtnsChangeDefault()[a].setVisible(true);
        }
        // LEFT-BOTTOM------------------------------------------------
        Arrays.asList(vc.getCksPK()).forEach(e -> e.setOnAction(vc::cksPKAction));

        vc.getLbUpdateLeft().setDisable(false);
        // RIGHT-BOTTOM------------------------------------------------
        //BOTTOM-----------------------------------------------------
        vc.getBtnCreateUpdate().setVisible(false);
        vc.setUpdateControl(true);
    }

    // -----------------------------------------------------
    public VC(VFController vf, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VC.class.getResource("/com/cofii/ts/cu/VC.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(VC.class.getResource("/com/cofii/ts/cu/VC.css").toExternalForm());
            vf.getStage().setScene(scene);
            // ------------------------------------------------------
            vc = (VCController) loader.getController();
            vc.setVf(vf);
            ms = vf.getMs();
            vc.setMs(ms);
            // ------------------------------------------------------
            if (create) {// THE REASON FOR NOT ADDING THE NODES IN THE CONTROLLER
                createOption();
                System.out.println(CC.CYAN + "\nCREATE TABLE" + CC.RESET);
            } else {
                System.out.println(CC.CYAN + "\nUPDATE TABLE" + CC.RESET);
                updateOption();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
