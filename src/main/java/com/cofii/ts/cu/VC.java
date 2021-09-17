package com.cofii.ts.cu;

import java.io.IOException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
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

    private void createOption() {
        setTextFill(true);
        vcc.createHelpPopupReset();
        vcc.pesetListInit(vcc.getCurrentRowLength());
        // TOP--------------------------------------------------------
        vcc.getBtnRenameTable().setVisible(false);

        for (int a = 0; a < vcc.getPresetRowsLenght(); a++) {
            vcc.addRowToGridPane(a);
            vcc.newRowCreateListeners(a);
        }
        // BOTTOM------------------------------------------------
        vcc.getBtnUpdatePK().setDisable(true);
        vcc.getBtnUpdateFK().setDisable(true);
        vcc.getBtnUpdateExtra().setDisable(true);
        vcc.getBtnUpdateDist().setDisable(true);
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
            vcc.getTfTable().setStyle(CSS.NODE_HINT);
            for (int a = 0; a < currentTable.getColumns().size(); a++) {
                vcc.getTfsColumn().get(a).setStyle(CSS.NODE_HINT);
                vcc.getTfasType().get(a).setStyle(CSS.NODE_HINT);
                vcc.getTfsTypeLength().get(a).setStyle(CSS.NODE_HINT);
                // vcc.getCksNull().get(a).setStyle(CSS.TEXT_FILL_HINT);
                vcc.getCksNull().get(a).setStyle(CSS.CKS_BG_HINT);
                vcc.getCksDefault().get(a).setStyle(CSS.CKS_BG_HINT);
                vcc.getTfsDefault().get(a).setStyle(CSS.NODE_HINT);
            }
        }
    }

    private void updateOption() {
        // setUpdateStore();
        setTextFill(false);
        vcc.createAddColumnHelpPopupReset();
        vcc.pesetListInit(currentTable.getColumns().size());
        // TOP-------------------------------------------------------
        vcc.getBtnRenameTable().setVisible(true);
        vcc.getBtnRenameTable().setOnAction(vcc::btnRenameTableAction);
        // CENTER ---------------------------------------------------
        vcc.setCurrentRowLength(currentTable.getColumns().size());
        for (int a = 0; a < currentTable.getColumns().size(); a++) {
            vcc.addRowToGridPane(a);
            // LISTENRS ------------------------------
            vcc.getBtnsRemoveColumn().get(a).setOnAction(vcc::dropColumnAction);
            vcc.getBtnsAddColumn().get(a).setOnAction(vcc::addColumnVisibleAction);

            if (a == 0) {
                vcc.getBtnsAddColumn().get(a).setContextMenu(vcc.getBeforeAfterOptionMenu());
                vcc.getBtnsAddColumn().get(a).setTooltip(vcc.getBeforeAfterOptionTooltip());
            }
            vcc.getBtnsRenameColumn().get(a).setOnAction(vcc::updateColumn);
            vcc.getBtnsChangeType().get(a).setOnAction(vcc::updateType);

            vcc.getCksNull().get(a).setOnAction(vcc::nullsAction);
            vcc.getBtnsChangeNull().get(a).setOnAction(vcc::updateNull);

            vcc.getBtnsChangeDefault().get(a).setOnAction(vcc::updateDefault);
            //-----------------------------------------------------
            vcc.getBtnsRenameColumn().get(a).setVisible(true);
            vcc.getBtnsChangeType().get(a).setVisible(true);
            vcc.getBtnsChangeNull().get(a).setVisible(true);
            vcc.getBtnsChangeDefault().get(a).setVisible(true);
        }
        // vcc.getRbsPK().forEach(e -> e.setOnAction(vcc::cksPKAction));
        // BOTTOM------------------------------------------------
        vcc.getBtnUpdatePK().setOnAction(vcc::updatePK);
        vcc.getBtnUpdateFK().setOnAction(vcc::updateFKS);
        vcc.getBtnUpdateExtra().setOnAction(vcc::updateExtra);
        vcc.getBtnUpdateDist().setOnAction(vcc::updateDist);

        vcc.getLbUpdateLeft().setDisable(false);
        vcc.getBtnCreateUpdate().setVisible(false);

        vcc.getBtnUpdatePK().setId(Integer.toString(-1));
        vcc.getBtnUpdateFK().setId(Integer.toString(-1));
        vcc.getBtnUpdateExtra().setId(Integer.toString(-1));
        vcc.getBtnUpdateDist().setId(Integer.toString(-1));
        //---------------------------------------------------------
        vcc.setUpdateControl(true);
    }

    // -----------------------------------------------------
    public VC(VFController vf, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VC.class.getResource("/com/cofii/ts/cu/VC.fxml"));

            /*
             * SceneZoom sz = new SceneZoom(loader.load(), zoomProperty);
             * sz.setParent(vcc.getBpMain()); Scene scene = sz.getScene();
             */
            Scene scene = new Scene(loader.load());
            vcc = (VCController) loader.getController();
            // vf.getStage().setScene(scene);
            scene.getStylesheets().add(VC.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
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
