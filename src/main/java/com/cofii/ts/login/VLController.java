package com.cofii.ts.login;

import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.first.VF;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDefaultUser;
import com.cofii.ts.sql.querys.ShowDatabases;
import com.cofii.ts.sql.querys.ShowTablesRootConfig;
import com.cofii.ts.sql.querys.ShowUsers;
import com.cofii2.methods.MList;
import com.cofii2.mysql.DefaultConnection;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.RootConfigConnection;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    @FXML
    private Button btnLogin;

    // LISTENERS---------------------------------------------
    @FXML
    private void tfPasswordKR() {
        if (tfPassword.getText().isEmpty()) {
            lbPassword.setTextFill(NonCSS.TEXT_FILL_ERROR);
        } else {
            lbPassword.setTextFill(NonCSS.TEXT_FILL);
        }

        btnLoginEnableControl();
    }

    // NON-FXML
    private void cbUserKR(KeyEvent e) {
        String text = cbUser.getEditor().getText();
        if (text.isEmpty()) {
            lbUser.setTextFill(NonCSS.TEXT_FILL_ERROR);
        } else {
            if (MList.isOnThisList(cbUser.getItems(), text, true)) {
                lbUser.setTextFill(NonCSS.TEXT_FILL);
            } else {
                lbUser.setTextFill(NonCSS.TEXT_FILL_ERROR);
            }
        }

        btnLoginEnableControl();

    }

    // NON-FXML
    private void cbDBKR(KeyEvent e) {
        String text = cbDB.getEditor().getText();
        if (text.isEmpty()) {
            lbDB.setTextFill(NonCSS.TEXT_FILL_ERROR);
        } else {
            if (MList.isOnThisList(cbDB.getItems(), text, true)) {
                lbDB.setTextFill(NonCSS.TEXT_FILL);
            } else {
                lbDB.setTextFill(NonCSS.TEXT_FILL_ERROR);
            }

        }

        btnLoginEnableControl();
    }

    @FXML
    private void btnLoginAction() {
        String user = cbUser.getEditor().getText();
        String password = tfPassword.getText();
        String database = cbDB.getEditor().getText();

        MSQL.setUser(user);
        MSQL.setPassword(password);
        MSQL.setDatabase(database);

        if(cbRemember.isSelected()){
            msRoot.executeUpdate(MSQL.UPDATE_TABLE_DEFAULT_USER);   
        }

        msRoot.close();

        new VF();
        stage.close();
    }

    // BTNLOGIN ENABLE
    private void btnLoginEnableControl() {

        System.out.println("NonCSS.TEXT_FILL: " + NonCSS.TEXT_FILL.toString());
        System.out.println("LBUser: " + lbUser.getTextFill().toString());
        System.out.println("lbPassword: " + lbPassword.getTextFill().toString());
        System.out.println("lbDB: " + lbDB.getTextFill().toString());

        if (lbUser.getTextFill().equals(NonCSS.TEXT_FILL) && lbPassword.getTextFill().equals(NonCSS.TEXT_FILL)
                && lbDB.getTextFill().equals(NonCSS.TEXT_FILL)) {
            btnLogin.setDisable(true);
            System.out.println("\tTRUE");
        }else{
            btnLogin.setDisable(false);
            System.out.println("\tFALSE");
        }
    }

    // ---------------------------------------------
    private MSQLP msInit = new MSQLP(new DefaultConnection());
    private MSQLP msRoot;
    private String initOption = "Login";

    private boolean showStage = false;

    // ---------------------------------------------
    private void initQuerys() {
        msInit.selectDatabases(new ShowDatabases(this));// AND ADDING TO cbDB
        if (!MSQL.isDbRootconfigExist()) {
            msInit.executeUpdate(MSQL.CREATE_DB_ROOTCONFIG);// NOT TESTED
        }
        msInit.close();
    }

    private void rootQuerys() {
        msRoot = new MSQLP(new RootConfigConnection());
        msRoot.selectTables(new ShowTablesRootConfig());
        if (!MSQL.isTableDefaultUserExist()) {
            msRoot.executeUpdate(MSQL.CREATE_TABLE_DEFAULT_USER);
            msRoot.executeUpdate(MSQL.INSERT_TABLE_DEFAULT_USER);
        }

        msRoot.executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT_USER, new SelectDefaultUser());
        if (MSQL.getUser().equals("NONE")) {
            msRoot.selectUsers(new ShowUsers(this));

            showStage = true;
        } else {
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
            // -----------------------
            cbUser.getEditor().setOnKeyReleased(this::cbUserKR);
            cbDB.getEditor().setOnKeyReleased(this::cbDBKR);

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

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean isShowStage() {
        return showStage;
    }

    public void setShowStage(boolean showStage) {
        this.showStage = showStage;
    }

}
