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
        vl.getLbPassword().setText(vl.getLbPassword().getText() + " ~ Wrong password");
        vl.getLbPassword().setTextFill(NonCSS.TEXT_FILL_ERROR);
        vl.getBtnLogin().setDisable(true);
        MSQL.setWrongPassword(true);
    }

    @Override
    public void succes() {
        vf.getStage().show();
        vl.getStage().close();
    }

}
