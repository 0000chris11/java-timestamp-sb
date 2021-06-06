package com.cofii.ts.cu;

import java.io.IOException;
import java.util.Arrays;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectKeys;
import com.cofii2.mysql.MSQLP;

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
    //-----------------------------------------------------
    private void createOption(){
        //TOP--------------------------------------------------------
        vc.getBtnRenameTable().setVisible(false);
        for (int a = 0; a < vc.getPresetRowsLenght(); a++) {
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
        // LEFT-------------------------------------------------------
        vc.getGridPaneLeft().getRowConstraints().forEach(e -> {
            e.setValignment(VPos.TOP);
            e.setVgrow(Priority.NEVER);
        });
        for(int a = 0; a < MSQL.MAX_COLUMNS; a++){
            vc.getBtnsRemoveColumn()[a].setOnAction(vc::btnsRemoveCreateAction);
            vc.getBtnsAddColumn()[a].setOnAction(vc::btnsAddCreateAction);
            vc.getBtnsRenameColumn()[a].setVisible(false);
            vc.getBtnsChangeType()[a].setVisible(false);
            vc.getBtnsChangeNull()[a].setVisible(false);
            vc.getBtnsChangePK()[a].setVisible(false);
            vc.getBtnsChangeFK()[a].setVisible(false);
            vc.getBtnsChangeDefault()[a].setVisible(false);
        }
        //LEFT-BOTTOM------------------------------------------------
        vc.getBtnUpdatePK().setDisable(true);
        vc.getBtnUpdateFK().setDisable(true);
        vc.getBtnUpdateExtra().setDisable(true);
        // RIGHT ROW-------------------------------------------------------
        vc.getGridPaneRight().getRowConstraints().forEach(e -> {
            e.setValignment(VPos.TOP);
            e.setVgrow(Priority.NEVER);
        });
        //RIGHT-BOTTOM------------------------------------------------
        vc.getBtnUpdateDist().setDisable(true);
    }
    private void updateOption(){
        //TOP-------------------------------------------------------
        vc.getBtnRenameTable().setVisible(true);
        //LEFT------------------------------------------------------
        for(int a = 0; a < MSQL.MAX_COLUMNS; a++){
            vc.getBtnsRemoveColumn()[a].setOnAction(vc::btnsRemoveUpdateAction);
            vc.getBtnsAddColumn()[a].setOnAction(vc::btnsAddUpdateAction);
            vc.getBtnsRenameColumn()[a].setVisible(true);
            vc.getBtnsChangeType()[a].setVisible(true);
            vc.getBtnsChangeNull()[a].setVisible(true);
            vc.getBtnsChangePK()[a].setVisible(true);//DELETE
            vc.getBtnsChangeFK()[a].setVisible(true);//DELETE
            vc.getBtnsChangeDefault()[a].setVisible(true);
        }
        //LEFT-BOTTOM------------------------------------------------
        vc.getBtnUpdatePK().setDisable(false);
        vc.getBtnUpdateFK().setDisable(false);
        vc.getBtnUpdateExtra().setDisable(false);
        //RIGHT-BOTTOM------------------------------------------------
        vc.getBtnUpdateDist().setDisable(false);
    }
    //-----------------------------------------------------
    public VC(VFController vf, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VC.class.getResource("/com/cofii/ts/cu/VC.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(VC.class.getResource("/com/cofii/ts/cu/VC.css").toExternalForm());
            vf.getStage().setScene(scene);
            //------------------------------------------------------
            vc = (VCController) loader.getController();
            vc.setVf(vf);
            ms = vf.getMs();
            vc.setMs(ms);
            //------------------------------------------------------
            if (create) {// THE REASON FOR NOT ADDING THE NODES IN THE CONTROLLER
                createOption();
            }else{
                updateOption();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
