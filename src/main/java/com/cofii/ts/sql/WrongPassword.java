package com.cofii.ts.sql;

import java.sql.SQLException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.login.VLController;
import com.cofii.ts.other.NonCSS;
import com.cofii2.mysql.interfaces.IConnectionException;

public class WrongPassword implements IConnectionException {

    private VLController vl;
    private VFController vf;

    public WrongPassword(VLController vl, VFController vf) {
        this.vl = vl;
        this.vf = vf;
    }

    @Override
    public void exception(SQLException ex) {
        vf.getVl().getLbPassword().setText(vf.getVl().getLbPassword().getText() + " ~ Wrong password");
        vf.getVl().getLbPassword().setTextFill(NonCSS.TEXT_FILL_ERROR);
        vf.getVl().getBtnLogin().setDisable(true);
        MSQL.setWrongPassword(true);
    }

    @Override
    public void succes() {
        vf.getStage().show();
        vf.getVl().getStage().close();
        //vl.getStage().close();
    }

}
