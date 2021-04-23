package com.cofii.ts.first;

import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.CurrenConnection;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.WrongPassword;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowTableCurrentDB;
import com.cofii2.mysql.MSQLP;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class VFController implements Initializable {

    private VLController vl;
    private Stage stage;

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuOptions;
    @FXML
    private Menu menuSelection;
    @FXML
    private Menu menuTable;

    @FXML
    private GridPane gridPane;
    private Label[] lbs = new Label[MSQL.MAX_COLUMNS];
    private TextField[] tfs = new TextField[MSQL.MAX_COLUMNS];
    private Button[] btns = new Button[MSQL.MAX_COLUMNS];
    // -----------------------------------------
    private MSQLP ms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("\nVFController STARTS");

        for(int a = 0; a < MSQL.MAX_COLUMNS; a++){
            lbs[a] = new Label("Column " + (a + 1));
            tfs[a] = new TextField();
            btns[a] = new Button();

            lbs[a].setMaxWidth(60);
            tfs[a].setMaxWidth(Short.MAX_VALUE);
            btns[a].setMaxWidth(20);

            lbs[a].setVisible(false);
            tfs[a].setVisible(false);
            btns[a].setVisible(false);

            gridPane.add(lbs[a], 0, a);
            gridPane.add(tfs[a], 1, a);
            gridPane.add(btns[a], 2, a);
            
        }
    }
    // -------------------------------------------

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public VLController getVl() {
        return vl;
    }

    public void setVl(VLController vl) {
        this.vl = vl;
    }

    public Menu getMenuOptions() {
        return menuOptions;
    }

    public void setMenuOptions(Menu menuOptions) {
        this.menuOptions = menuOptions;
    }

    public Menu getMenuSelection() {
        return menuSelection;
    }

    public void setMenuSelection(Menu menuSelection) {
        this.menuSelection = menuSelection;
    }

    public Menu getMenuTable() {
        return menuTable;
    }

    public void setMenuTable(Menu menuTable) {
        this.menuTable = menuTable;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public Label[] getLbs() {
        return lbs;
    }

    public void setLbs(Label[] lbs) {
        this.lbs = lbs;
    }

    public TextField[] getTfs() {
        return tfs;
    }

    public void setTfs(TextField[] tfs) {
        this.tfs = tfs;
    }

    public Button[] getBtns() {
        return btns;
    }

    public void setBtns(Button[] btns) {
        this.btns = btns;
    }
    
}
