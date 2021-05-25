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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class VC {

    private MSQLP ms;

    public VC(VFController vf, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VC.class.getResource("/com/cofii/ts/cu/VC.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(VC.class.getResource("/com/cofii/ts/cu/VC.css").toExternalForm());
            vf.getStage().setScene(scene);

            VCController vc = (VCController) loader.getController();

            vc.setVf(vf);
            ms = vf.getMs();
            if (create) {// THE REASON FOR NOT ADDING THE NODES IN THE CONTROLLER
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
                // RIGHT-SUB------------------
                //vc.getGridPaneRightSub().add(vc.getBtnChangeDist(), 0, 0);
                //vc.getGridPaneRightSub().add(vc.getHbImageC(), 1, 0);
                // LEFT ROW-------------------------------------------------------
                vc.getGridPaneLeft().getRowConstraints().forEach(e -> {
                    e.setValignment(VPos.TOP);
                    //e.setPrefHeight(-1);
                    //e.setMaxHeight(-1);

                    e.setVgrow(Priority.NEVER);
                });
                //vc.getGridPaneLeft().getRowConstraints().get(vc.getGridPaneLeft().getRowCount() - 2).setVgrow(Priority.ALWAYS);
                //vc.getGridPaneLeftSub().getColumnConstraints().get(0).setPrefWidth(vc.getLbsN()[0].getWidth());
                //vc.getGridPaneLeftSub().getColumnConstraints().get(1).setPrefWidth(vc.getHbsName()[0].getWidth());

                // RIGHT ROW-------------------------------------------------------
                vc.getGridPaneRight().getRowConstraints().forEach(e -> {
                    e.setValignment(VPos.TOP);
                    //e.setPrefHeight(-1);
                    //e.setMaxHeight(-1);
                    e.setVgrow(Priority.NEVER);
                });
                //vc.getGridPaneRight().getRowConstraints().get(vc.getPresetRowsLenght()).setVgrow(Priority.ALWAYS);
                //vc.getGridPaneRight().getRowConstraints().get(vc.getPresetRowsLenght()).setPrefHeight(200);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
