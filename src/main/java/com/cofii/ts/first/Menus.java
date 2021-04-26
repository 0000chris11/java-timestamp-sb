package com.cofii.ts.first;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class Menus {
    //Open
    private MenuItem openChangeUserDB = new MenuItem("Change User or DB");
    private MenuItem openTableMain = new MenuItem("Open Tables Info");
    //Table
    private Menu tableOp = new Menu("Options");
    private CheckMenuItem tableOpClearValues = new CheckMenuItem("Clear nodes values when a row is deleted");
    private CheckMenuItem tableOpReloacImage = new CheckMenuItem("Reload ImageC");
    private Menu tableChangeDTable = new Menu("Change Default Table");
    private MenuItem tableCreate = new Menu("Create new table");
    private MenuItem tableUpdate = new Menu("Update table");
    private MenuItem tableDelete = new Menu("Delete table");
    private MenuItem tableDeleteThis = new Menu("Delete this table");
    //---------------------------------------------------
    private void openChangeUserDBAction(ActionEvent e){
        vf.getVl().getStage().show();
    }
    //---------------------------------------------------
    private static Menus instance;
    private static VFController vf;
    public static Menus getInstance(VFController vf){
        Menus.vf = vf;
        if(instance == null){
            instance = new Menus();
        }
        return instance;
    }

    private Menus(){
        vf.getMenuOpen().getItems().addAll(openChangeUserDB, openTableMain);
        vf.getMenuTable().getItems().addAll(tableOp, new SeparatorMenuItem(), tableChangeDTable, new SeparatorMenuItem(), tableCreate, tableUpdate, tableDelete, tableDeleteThis);

        openChangeUserDB.setOnAction(this::openChangeUserDBAction);
    }
}
