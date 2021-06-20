package com.cofii.ts.cu;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
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

    private VCController vcc;
    private MSQLP ms;
    private ColumnS columns = ColumnS.getInstance();

    // private UpdateTable updateTable;

    // -----------------------------------------------------
    private void rowDisplay(int size) {
        for (int a = 0; a < size; a++) {
            int row = a + 1;

            vcc.getGridPaneLeft().add(vcc.getHbsN()[a], 0, row);
            vcc.getGridPaneLeft().add(vcc.getHbsName()[a], 1, row);
            vcc.getGridPaneLeft().add(vcc.getHbsType()[a], 2, row);
            vcc.getGridPaneLeft().add(vcc.getHbsNull()[a], 3, row);
            vcc.getGridPaneLeft().add(vcc.getHbsPK()[a], 4, row);
            vcc.getGridPaneLeft().add(vcc.getHbsFK()[a], 5, row);
            vcc.getGridPaneLeft().add(vcc.getHbsDefault()[a], 6, row);
            vcc.getGridPaneLeft().add(vcc.getHbsExtra()[a], 7, row);

            GridPane.setValignment(vcc.getLbsN()[a], VPos.TOP);
            // RIGHT-----------------------------------------
            vcc.getGridPaneRight().add(vcc.getBtnsDist()[a], 0, row);
            vcc.getGridPaneRight().add(vcc.getBtnsImageC()[a], 1, row);

            GridPane.setValignment(vcc.getBtnsDist()[a], VPos.TOP);
            GridPane.setValignment(vcc.getBtnsImageC()[a], VPos.TOP);
        }
    }

    private void createOption() {
        setTextFill(true);
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
            vcc.getBtnsRemoveColumn()[a].setOnAction(vcc::btnsRemoveCreateAction);
            vcc.getBtnsAddColumn()[a].setOnAction(vcc::btnsAddCreateAction);

            vcc.getBtnsRenameColumn()[a].setVisible(false);
            vcc.getBtnsChangeType()[a].setVisible(false);
            vcc.getBtnsChangeNull()[a].setVisible(false);
            vcc.getBtnsChangePK()[a].setVisible(false);
            vcc.getBtnsChangeFK()[a].setVisible(false);
            vcc.getBtnsChangeDefault()[a].setVisible(false);
        }
        vcc.btnAddRemoveColumnInit();
        // LEFT-BOTTOM------------------------------------------------
        vcc.getBtnUpdatePK().setDisable(true);
        vcc.getBtnUpdateFK().setDisable(true);
        vcc.getBtnUpdateExtra().setDisable(true);
        // RIGHT ROW-------------------------------------------------------
        vcc.getGridPaneRight().getRowConstraints().forEach(e -> {
            e.setValignment(VPos.TOP);
            e.setVgrow(Priority.NEVER);
        });
        // RIGHT-BOTTOM------------------------------------------------
        vcc.getBtnUpdateDist().setDisable(true);
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

        vcc.getTfTable().setText(table);
        for (int a = 0; a < columnCount; a++) {
            vcc.getTfsColumn()[a].setText(columnsName.get(a).replace("_", " "));
            vcc.getTfasType()[a].setText(types.get(a));
            vcc.getTfsTypeLength()[a].setText(Integer.toString(typesLength.get(a)));
            vcc.getCksNull()[a].setSelected(nulls.get(a));
            vcc.getCksPK()[a].setSelected(pks.get(a).equals("Yes"));
            if (fks.get(a) != null) {// NOT TESTED
                vcc.getCksFK()[a].setSelected(true);
                fksFormed[a] = fks.get(a).getString2() + "." + fks.get(a).getString3() + "." + fks.get(a).getString4();
                vcc.getTfasFK()[a].setText(fksFormed[a]);
            }

            if (defaults.get(a) != null) {
                vcc.getCksDefault()[a].setSelected(true);
                vcc.getTfsDefault()[a].setVisible(true);
                vcc.getTfsDefault()[a].setText(defaults.get(a));
            } else {
                vcc.getCksDefault()[a].setSelected(false);
            }
            vcc.getRbsExtra()[a].setSelected(extra == a);
            // DISTS---------------------------------------------
            vcc.getBtnsDist()[a].setSelected(dists.get(a).equals("Yes"));
            vcc.getBtnsImageC()[a].setSelected(imageCS.get(a).equals("Yes"));// ERROR IF THERE IS MORE THAN ONE
            if (!imageCPath.get(a).equals("NONE")) {
                vcc.getTfImageCPath().setText(imageCPath.get(a));
            }
        }

        // ----------------------------------------------------
        // updateTable = new UpdateTable(table, columnsName, types, typesLength, nulls,
        // pks, fks, fksFormed, defaults, extra);
        UpdateTable updateTable = new UpdateTable();
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
        vcc.setUpdateTable(updateTable);
    }

    private void setTextFill(boolean create) {
        if (create) {
            for (int a = 0; a < vcc.getCurrentRowLength(); a++) {
                vcc.getCksNull()[a].setStyle(CSS.CKS_BG);
                vcc.getCksDefault()[a].setStyle(CSS.CKS_BG);
            }
        } else {
            vcc.getTfTable().setStyle(CSS.TEXT_FILL_HINT);
            for (int a = 0; a < columns.size(); a++) {
                vcc.getTfsColumn()[a].setStyle(CSS.TEXT_FILL_HINT);
                vcc.getTfasType()[a].setStyle(CSS.TEXT_FILL_HINT);
                vcc.getTfsTypeLength()[a].setStyle(CSS.TEXT_FILL_HINT);
                // vcc.getCksNull()[a].setStyle(CSS.TEXT_FILL_HINT);
                vcc.getCksNull()[a].setStyle(CSS.CKS_BG_HINT);
                vcc.getCksDefault()[a].setStyle(CSS.CKS_BG_HINT);
                vcc.getTfsDefault()[a].setStyle(CSS.TEXT_FILL_HINT);
            }
        }
    }

    private void updateOption() {
        setUpdateStore();
        setTextFill(false);
        vcc.pesetListInit(columns.size());
        // TOP-------------------------------------------------------
        vcc.getBtnRenameTable().setVisible(true);
        vcc.getBtnRenameTable().setOnAction(vcc::btnRenameTableAction);
        // LEFT------------------------------------------------------
        rowDisplay(columns.size());
        vcc.setCurrentRowLength(columns.size());
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            vcc.getBtnsRemoveColumn()[a].setOnAction(vcc::btnsRemoveUpdateAction);
            vcc.getBtnsAddColumn()[a].setOnAction(vcc::btnsColumnSetVisibleAction);
            vcc.getBtnsRenameColumn()[a].setOnAction(vcc::btnsRenameColumn);
            vcc.getBtnsChangeType()[a].setOnAction(vcc::btnsChangeType);
            vcc.getCksNull()[a].setOnAction(vcc::cksNullAction);
            vcc.getBtnsChangeNull()[a].setOnAction(vcc::btnsChangeNull);
            vcc.getBtnsChangeDefault()[a].setOnAction(vcc::btnsChangeDefault);

            vcc.getBtnsRenameColumn()[a].setVisible(true);
            vcc.getBtnsChangeType()[a].setVisible(true);
            vcc.getBtnsChangeNull()[a].setVisible(true);
            vcc.getBtnsChangePK()[a].setVisible(true);// DELETE
            vcc.getBtnsChangeFK()[a].setVisible(true);// DELETE
            vcc.getBtnsChangeDefault()[a].setVisible(true);
        }
        Arrays.asList(vcc.getCksPK()).forEach(e -> e.setOnAction(vcc::cksPKAction));
        // LEFT-BOTTOM------------------------------------------------

        vcc.getLbUpdateLeft().setDisable(false);
        // RIGHT-BOTTOM------------------------------------------------
        // BOTTOM-----------------------------------------------------
        vcc.getBtnCreateUpdate().setVisible(false);
        vcc.setUpdateControl(true);
    }

    // -----------------------------------------------------
    public VC(VFController vf, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VC.class.getResource("/com/cofii/ts/cu/VC.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(VC.class.getResource("/com/cofii/ts/cu/VC.css").toExternalForm());
            vf.getStage().setScene(scene);
            // ------------------------------------------------------
            vcc = (VCController) loader.getController();
            vcc.setVf(vf);
            ms = vf.getMs();
            vcc.setMs(ms);
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
