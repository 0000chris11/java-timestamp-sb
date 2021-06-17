package com.cofii.ts.cu;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.first.VFController;

import com.cofii.ts.sql.MSQL;

import com.cofii.ts.store.ColumnDS;
import com.cofii.ts.store.ColumnS;
import com.cofii.ts.store.Keys;
import com.cofii.ts.store.UpdateTable;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;

import com.cofii2.stores.QString;

import org.apache.commons.lang3.ArrayUtils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class VC {

    private VCController vc;
    private MSQLP ms;
    private ColumnS columns = ColumnS.getInstance();

    private UpdateTable updateTable;

    // -----------------------------------------------------
    private void rowDisplay(int size) {
        for (int a = 0; a < size; a++) {
            int row = a + 1;

            vc.getGridPaneLeft().add(vc.getHbsN()[a], 0, row);
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
        List<String> columnsName = Arrays.asList(columns.getColumns());
        List<String> types = Arrays.asList(columns.getTypes());
        // List<Integer> typesLength = Arrays.asList(columns.getTypesLength());
        List<Integer> typesLength = Arrays.asList(ArrayUtils.toObject(columns.getTypesLength()));
        List<Boolean> nulls = Arrays.asList(ArrayUtils.toObject(columns.getNulls()));
        List<String> pks = Arrays.asList(keys.getPKS());
        List<QString> fks = Arrays.asList(keys.getFKS());
        String[] fksFormed = new String[columns.size()];
        List<String> defaults = Arrays.asList(columns.getDefaults());
        int extra = columns.getExtra();

        List<String> dists = Arrays.asList(columnds.getDists());
        List<String> imageCS = Arrays.asList(columnds.getImageCS());
        List<String> imageCPath = Arrays.asList(columnds.getImageCPaths());
        // ----------------------------------------------------

        vc.getTfTable().setText(table);
        for (int a = 0; a < columnCount; a++) {
            vc.getTfsColumn()[a].setText(columnsName.get(a).replace("_", " "));
            vc.getTfasType()[a].setText(types.get(a));
            vc.getTfsTypeLength()[a].setText(Integer.toString(typesLength.get(a)));
            vc.getCksNull()[a].setSelected(nulls.get(a));
            vc.getCksPK()[a].setSelected(pks.get(a).equals("Yes"));
            if (fks.get(a) != null) {// NOT TESTED
                vc.getCksFK()[a].setSelected(true);
                fksFormed[a] = fks.get(a).getString2() + "." + fks.get(a).getString3() + "." + fks.get(a).getString4();
                vc.getTfasFK()[a].setText(fksFormed[a]);
            }

            if (defaults.get(a) != null) {
                vc.getCksDefault()[a].setSelected(true);
                vc.getTfsDefault()[a].setVisible(true);
                vc.getTfsDefault()[a].setText(defaults.get(a));
            } else {
                vc.getCksDefault()[a].setSelected(false);
            }
            vc.getRbsExtra()[a].setSelected(extra == a);
            // DISTS---------------------------------------------
            vc.getBtnsDist()[a].setSelected(dists.get(a).equals("Yes"));
            vc.getBtnsImageC()[a].setSelected(imageCS.get(a).equals("Yes"));// ERROR IF THERE IS MORE THAN ONE
            if (!imageCPath.get(a).equals("NONE")) {
                vc.getTfImageCPath().setText(imageCPath.get(a));
            }
        }

        // ----------------------------------------------------
        // updateTable = new UpdateTable(table, columnsName, types, typesLength, nulls,
        // pks, fks, fksFormed, defaults, extra);
        updateTable = new UpdateTable();
        updateTable.setTable(table);
        updateTable.setColumns(columnsName);
        updateTable.setTypes(types);
        updateTable.setTypesLength(typesLength);
        updateTable.setNulls(nulls);
        updateTable.setPks(pks);
        updateTable.setFks(fks);
        updateTable.setFkFormed(Arrays.asList(fksFormed));
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
        vc.getBtnRenameTable().setOnAction(vc::btnRenameTableAction);
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
        Arrays.asList(vc.getCksPK()).forEach(e -> e.setOnAction(vc::cksPKAction));
        // LEFT-BOTTOM------------------------------------------------

        vc.getLbUpdateLeft().setDisable(false);
        // RIGHT-BOTTOM------------------------------------------------
        // BOTTOM-----------------------------------------------------
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
