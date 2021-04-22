package com.cofii.ts.first;

import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.sql.CurrenConnection;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.WrongPassword;
import com.cofii.ts.sql.querys.ShowTableCurrentDB;
import com.cofii2.mysql.MSQLP;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;

public class VFController implements Initializable{

    @FXML
    private MenuBar menuBar;
    //-----------------------------------------
    private MSQLP ms = new MSQLP(new CurrenConnection(MSQL.getDatabase(), MSQL.getUser(), MSQL.getPassword()), new WrongPassword());
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ms.selectTables(new ShowTableCurrentDB());
    }

}
