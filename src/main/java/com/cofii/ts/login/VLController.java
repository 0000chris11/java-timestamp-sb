package com.cofii.ts.login;

import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.first.VF;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDefaultUser;
import com.cofii.ts.sql.querys.ShowDatabases;
import com.cofii.ts.sql.querys.ShowTables;
import com.cofii.ts.sql.querys.ShowUsers;
import com.cofii2.mysql.DefaultConnection;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.RootConfigConnection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VLController implements Initializable {

    @FXML
    private HBox layout;
    private Stage stage;

    @FXML
    private Label lbUser;
    @FXML
    private Label lbPassword;
    @FXML
    private Label lbDB;

    @FXML
    private ComboBox<String> cbUser;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private ComboBox<String> cbDB;

    @FXML
    private CheckBox cbRemember;
    // ---------------------------------------------
    private MSQLP msInit = new MSQLP(new DefaultConnection());
    private MSQLP msRoot;
    private String initOption = "Login";

    // ---------------------------------------------
    private void initQuerys() {
        msInit.selectDatabases(new ShowDatabases());
        if (!MSQL.isDbRootconfigExist()) {
            msInit.executeUpdate(MSQL.CREATE_DB_ROOTCONFIG);// NOT TESTED
        }
        msInit.close();
    }

    private void rootQuerys() {
        msRoot = new MSQLP(new RootConfigConnection());
        msRoot.selectTables(new ShowTables());
        if (!MSQL.isTableDefaultUserExist()) {
            msRoot.executeUpdate(MSQL.CREATE_TABLE_DEFAULT_USER);
            msRoot.executeUpdate(MSQL.INSERT_TABLE_DEFAULT_USER);
        }

        msRoot.selectUsers(new ShowUsers(this));

        msRoot.executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT_USER, new SelectDefaultUser());
        if (MSQL.getUser().equals("NONE")) {
            stage = (Stage) layout.getScene().getWindow();
            stage.show();
        }else{
            
            new VF();
        }

    }

    // ---------------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // DO QUERYS
        System.out.println("location: " + location.toString());
        if (initOption.equalsIgnoreCase("Login")) {
            initQuerys();
            // -----------------------
            rootQuerys();
        }
    }
    // ---------------------------------------------

    public Label getLbUser() {
        return lbUser;
    }

    public void setLbUser(Label lbUser) {
        this.lbUser = lbUser;
    }

    public Label getLbPassword() {
        return lbPassword;
    }

    public void setLbPassword(Label lbPassword) {
        this.lbPassword = lbPassword;
    }

    public Label getLbDB() {
        return lbDB;
    }

    public void setLbDB(Label lbDB) {
        this.lbDB = lbDB;
    }

    public ComboBox<String> getCbUser() {
        return cbUser;
    }

    public void setCbUser(ComboBox<String> cbUser) {
        this.cbUser = cbUser;
    }

    public PasswordField getTfPassword() {
        return tfPassword;
    }

    public void setTfPassword(PasswordField tfPassword) {
        this.tfPassword = tfPassword;
    }

    public ComboBox<String> getCbDB() {
        return cbDB;
    }

    public void setCbDB(ComboBox<String> cbDB) {
        this.cbDB = cbDB;
    }

    public CheckBox getCbRemember() {
        return cbRemember;
    }

    public void setCbRemember(CheckBox cbRemember) {
        this.cbRemember = cbRemember;
    }

}
