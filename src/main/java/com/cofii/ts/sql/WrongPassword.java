package com.cofii.ts.sql;

import java.sql.SQLException;
import java.util.Arrays;

import com.cofii.ts.first.VFController;
import com.cofii.ts.login.VLController;
import com.cofii.ts.other.NonCSS;
import com.cofii2.mysql.interfaces.IConnectionException;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WrongPassword implements IConnectionException {

    private VLController vl;
    private VFController vf;

    public WrongPassword(VLController vl, VFController vf) {
        this.vl = vl;
        this.vf = vf;
    }

    @Override
    public void exception(SQLException ex) {
        if (!vf.getVl().isShowStage()) {
            Stage stage = new Stage();

            TextArea ta = new TextArea();
            for(StackTraceElement x :ex.getStackTrace()){
                ta.appendText(x.toString());
            }
            
            VBox status = new VBox(new Label(ex.getMessage()), ta);
            stage.setScene(new Scene(status));

            stage.show();
        }
        vf.getVl().getLbPassword().setText(vf.getVl().getLbPassword().getText() + " ~ Wrong password");
        vf.getVl().getLbPassword().setTextFill(NonCSS.TEXT_FILL_ERROR);
        vf.getVl().getBtnLogin().setDisable(true);
        MSQL.setWrongPassword(true);
    }

    @Override
    public void succes() {
        vf.getStage().show();
        vf.getVl().getStage().close();
        // vl.getStage().close();
    }

}
