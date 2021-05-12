package com.cofii.ts.cu;

import java.io.IOException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectKeys;
import com.cofii2.mysql.MSQLP;

import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class VC {

    private MSQLP ms;

    public VC(VFController vf, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VC.class.getResource("/com/cofii/ts/cu/VC.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(VC.class.getResource("/com/cofii/ts/cu/VC.css").toExternalForm());
            vf.getStage().setScene(scene);

            VCController vc = (VCController)loader.getController();

            vc.setVf(vf);
            ms = vf.getMs();
            if(create){//THE REASON FOR NOT ADDING THE NODES IN THE CONTROLLER
                for(int a = 0;a < vc.getPresetRowsLenght(); a++){
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

                }
                vc.getGridPaneLeft().getRowConstraints().forEach(e -> {
                    e.setValignment(VPos.TOP);
                    //e.setVgrow(Priority.ALWAYS);
                    e.setPrefHeight(-1);
                    e.setMaxHeight(-1);
                    //e.setFillHeight(true);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
