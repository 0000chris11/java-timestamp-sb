package com.cofii.ts.cu;

import java.io.IOException;
import java.util.List;

import com.cofii.ts.first.VFController;
import com.cofii.ts.store.main.Column;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.popup.PopupMessage;
import com.cofii2.components.javafx.zoom.ZoomingPane;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class VC {

    private VCController vcc;
    private MSQLP ms;
    private Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();

    private DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0);
    // private UpdateTable updateTable;
    private ChangeListener<? super Boolean> stageFocusPropertyListener = (obs, oldValue, newValue) -> {
        if (newValue) {
            if (!vcc.getPopupMessageControl().getPopupMaster().getParentNode().equals(vcc.getBtnErrorDisplay())) {
                vcc.getPopupMessageControl().getPopupMaster().getPopup().hide();
                vcc.getPopupMessageControl().setPopupMaster(new PopupMessage("MASTER", vcc.getBtnErrorDisplay()));
            }
        }
    };

    private void createOption() {
        // TOP--------------------------------------------------------
        vcc.getBtnRenameTable().setVisible(false);

        VCRow.setVcc(vcc);
        VCRow.setCreateRows(true);
        for (int a = 0; a < vcc.getPresetRowsLenght(); a++) {
            VCRow.addRowTest(a);
        }
        // BOTTOM------------------------------------------------
        vcc.getBtnUpdatePK().setDisable(true);
        vcc.getBtnUpdateExtra().setDisable(true);
        vcc.getBtnUpdateDist().setDisable(true);
    }

    private void updateOption() {
        vcc.setUpdateControl(true);
        // TOP-------------------------------------------------------
        vcc.getTfTable().setText(currentTable.getName());

        vcc.getBtnRenameTable().setVisible(true);
        vcc.getBtnRenameTable().setOnAction(vcc::btnRenameTableAction);
        // CENTER ---------------------------------------------------
        VCRow.setVcc(vcc);
        VCRow.setCreateRows(false);
        vcc.setPresetRowsLenght(currentTable.getColumns().size());
        for (int a = 0; a < currentTable.getColumns().size(); a++) {
            // vcc.addRow(a, false);
            VCRow.addRowTest(a);
        }
        rowUpdateSetValues();
        // BOTTOM------------------------------------------------
        vcc.getBtnUpdatePK().setOnAction(vcc::updatePK);
        vcc.getBtnUpdateExtra().setOnAction(vcc::updateExtra);
        vcc.getBtnUpdateDist().setOnAction(vcc::updateDist);

        vcc.getLbUpdateLeft().setDisable(false);
        vcc.getBtnCreateUpdate().setVisible(false);

        vcc.getBtnUpdatePK().setId(Integer.toString(-1));
        vcc.getBtnUpdateExtra().setId(Integer.toString(-1));
        vcc.getBtnUpdateDist().setId(Integer.toString(-1));
        // ---------------------------------------------------------
    }

    private void rowUpdateSetValues() {
        List<VCRow> rows = VCRow.getRows();
        for (int a = 0; a < currentTable.getColumns().size(); a++) {
            Column columnn = currentTable.getColumns().get(a);
            VCRow row = rows.get(a);
            // -----------------------------------------------
            String columnName = columnn.getName();
            String typeName = columnn.getType();
            int typeLenght = columnn.getTypeLength();
            boolean nulll = columnn.isNulll();
            boolean pk = columnn.isPk();
            Object defaultt = columnn.getDefaultt();
            boolean extra = columnn.isExtra();

            boolean dist = columnn.isDist();
            boolean textArea = columnn.isTextArea();
            // -----------------------------------------------
            row.getTfColumn().setText(columnName);
            row.getTfType().setText(typeName);
            // May Hide either way ! --------------------------
            row.getTfTypeLength().setText(typeLenght > 0 ? Integer.toString(typeLenght) : "");
            row.getCkNull().setSelected(nulll);
            row.getRbPK().setSelected(pk);
            // Don't know if the tf-Default will turn visible (if selected) !
            if (defaultt != null) {
                row.getCkDefault().setSelected(true);
                row.getTfDefault().setText(defaultt.toString());
            }else{
                row.getCkDefault().setSelected(false);
            }
            row.getRbExtra().setSelected(extra);

            row.getBtnDist().setSelected(dist);
            row.getBtnTextArea().setSelected(textArea);
        }
    }

    // -----------------------------------------------------
    public VC(VFController vf, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VC.class.getResource("/com/cofii/ts/cu/VC.fxml"));

            // ZOOMIMING PANE -----------------------
            ZoomingPane zoomingPane = new ZoomingPane(loader.load());
            Scene scene = new Scene(zoomingPane);
            // -----------------------------------------
            vcc = (VCController) loader.getController();
            scene.getStylesheets().add(VC.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());

            vf.getStage().focusedProperty().removeListener(stageFocusPropertyListener);
            vf.getStage().focusedProperty().addListener(stageFocusPropertyListener);
            vf.getStage().setScene(scene);
            // ------------------------------------------------------
            vcc.setVf(vf);
            ms = vf.getMs();
            vcc.setMs(ms);
            // ------------------------------------------------------
            if (create) {// THE REASON FOR NOT ADDING THE NODES TO THE GRIDPANE IN THE CONTROLLER
                System.out.println(CC.CYAN + "\nCREATE TABLE" + CC.RESET);
                createOption();
            } else {
                System.out.println(CC.CYAN + "\nUPDATE TABLE" + CC.RESET);
                updateOption();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
