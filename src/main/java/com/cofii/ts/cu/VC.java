package com.cofii.ts.cu;

import java.io.IOException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class VC {

    private VCController vcc;
    private MSQLP ms;
    private Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();

    private DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0);
    // private UpdateTable updateTable;

    private void createOption() {
        vcc.createHelpPopupReset();
        // TOP--------------------------------------------------------
        vcc.getBtnRenameTable().setVisible(false);

        for (int a = 0; a < vcc.getPresetRowsLenght(); a++) { 
            vcc.addRow(a, true);
        }
        setTextFill(true);
        // BOTTOM------------------------------------------------
        vcc.getBtnUpdatePK().setDisable(true);
        vcc.getBtnUpdateFK().setDisable(true);
        vcc.getBtnUpdateExtra().setDisable(true);
        vcc.getBtnUpdateDist().setDisable(true);
    }

    private void setTextFill(boolean create) {
        if (create) {
            for (int a = 0; a < vcc.getCurrentRowLength(); a++) {
                /* NECESSARY ???????????
                vcc.getCksNull().get(a).applyCss();
                vcc.getCksNull().get(a).setStyle(CSS.CKS_BG);
                // vcc.getCksNull().get(a).setBackground(new Background(new BackgroundFill(,
                // CornerRadii.EMPTY, Insets.EMPTY)));
                vcc.getCksDefault().get(a).setStyle(CSS.CKS_BG);
                */
            }
        } else {
            // TOP ------------------------------------
            vcc.getTfTable().setStyle(CSS.NODE_TEXTFILL_HINT);
            // CENTER ---------------------------------
            for (int a = 0; a < currentTable.getColumns().size(); a++) {
                vcc.getTfsColumn().get(a).setStyle(CSS.NODE_TEXTFILL_HINT);
                vcc.getTfasType().get(a).setStyle(CSS.NODE_TEXTFILL_HINT);
                vcc.getTfsTypeLength().get(a).setStyle(CSS.NODE_TEXTFILL_HINT);
                vcc.getCksNull().get(a).setStyle(CSS.CKS_BG_HINT);
                vcc.getCksDefault().get(a).setStyle(CSS.CKS_BG_HINT);
                vcc.getTfsDefault().get(a).setStyle(CSS.NODE_TEXTFILL_HINT);
            }
        }
    }

    private void updateOption() {
        // setUpdateStore();
        vcc.createAddColumnHelpPopupReset();
        // TOP-------------------------------------------------------
        vcc.getBtnRenameTable().setVisible(true);
        vcc.getBtnRenameTable().setOnAction(vcc::btnRenameTableAction);
        // CENTER ---------------------------------------------------
        vcc.setCurrentRowLength(currentTable.getColumns().size());
        for (int a = 0; a < currentTable.getColumns().size(); a++) {
            vcc.addRow(a, false);
        }
        setTextFill(false);
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
