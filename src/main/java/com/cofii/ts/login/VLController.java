package com.cofii.ts.login;

import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.first.VF;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDefaultUser;
import com.cofii.ts.sql.querys.ShowDatabases;
import com.cofii.ts.sql.querys.ShowTablesRootConfig;
import com.cofii.ts.sql.querys.ShowUsers;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.methods.MList;
import com.cofii2.mysql.DefaultConnection;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.RootConfigConnection;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class VLController implements Initializable {

    @FXML
    private HBox layout;
    private Stage stage;
    private Scene scene;

    @FXML
    private Label lbUser;
    @FXML
    private Label lbPassword;
    @FXML
    private Label lbDB;

    @FXML
    private TextField tfUser;
    private PopupAutoC tfUserAC;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private TextField tfDB;
    private PopupAutoC tfDBAC;

    @FXML
    private CheckBox cbRemember;
    @FXML
    private Button btnLogin;

    // LISTENERS---------------------------------------------
    // NON-FXML
    void sceneKR(KeyEvent e) {
        if (e != null) {
            if (e.getCode() == KeyCode.CAPS) {
                boolean capsOn = Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
                if (capsOn) {
                    lbPassword.setText("Password ~ CAPS On");
                    lbPassword.setTextFill(NonCSS.TEXT_FILL_HINT);
                } else {
                    tfPasswordKR();
                }
            }
        } else {
            if (Toolkit.getDefaultToolkit().getLockingKeyState(20)) {
                lbPassword.setText(lbPassword.getText() + " ~ CAPS On");
                lbPassword.setTextFill(NonCSS.TEXT_FILL_HINT);
            }
        }

    }

    @FXML
    private void tfPasswordKR() {
        if (!lbPassword.getText().equals("Password")) {
            lbPassword.setText("Password");
        }

        if (tfPassword.getText().isEmpty()) {
            lbPassword.setTextFill(NonCSS.TEXT_FILL_ERROR);
        } else {
            lbPassword.setTextFill(NonCSS.TEXT_FILL);
        }

        btnLoginEnableControl();
    }

    // NON-FXML
    private void cbUserKR(KeyEvent e) {
        String text = tfUser.getText();
        if (text.isEmpty()) {
            lbUser.setTextFill(NonCSS.TEXT_FILL_ERROR);
        } else {
            if (MList.isOnThisList(tfUserAC.getLv().getItems(), text, true)) {
                lbUser.setTextFill(NonCSS.TEXT_FILL);
            } else {
                lbUser.setTextFill(NonCSS.TEXT_FILL_ERROR);
            }
        }

        btnLoginEnableControl();

    }

    // NON-FXML
    private void cbUserSelection(ObservableValue<? extends String> object, String oldValue, String newValue) {
        lbUser.setTextFill(NonCSS.TEXT_FILL);
        btnLoginEnableControl();
    }

    // NON-FXML
    private void cbDBKR(KeyEvent e) {
        String text = tfDB.getText();
        if (text.isEmpty()) {
            lbDB.setTextFill(NonCSS.TEXT_FILL_ERROR);
        } else {
            if (MList.isOnThisList(tfDBAC.getLv().getItems(), text, true)) {
                lbDB.setTextFill(NonCSS.TEXT_FILL);
            } else {
                lbDB.setTextFill(NonCSS.TEXT_FILL_ERROR);
            }

        }

        btnLoginEnableControl();
    }

    // NON-FXML
    private void cbDBSelection(ObservableValue<? extends String> object, String oldValue, String newValue) {
        lbDB.setTextFill(NonCSS.TEXT_FILL);
        btnLoginEnableControl();
    }

    @FXML
    private void btnLoginAction() {
        String user = tfUser.getText();
        String password = tfPassword.getText();
        String database = tfDB.getText();

        MSQL.setUser(user);
        MSQL.setPassword(password);
        MSQL.setDatabase(database);

        if (cbRemember.isSelected()) {
            msRoot.executeStringUpdate(MSQL.UPDATE_TABLE_DEFAULT_USER);
        }

        msRoot.close();

        new VF(this);
    }

    // BTNLOGIN ENABLE
    private void btnLoginEnableControl() {
        if (lbUser.getTextFill().equals(NonCSS.TEXT_FILL) && lbPassword.getTextFill().equals(NonCSS.TEXT_FILL)
                && lbDB.getTextFill().equals(NonCSS.TEXT_FILL)) {
            btnLogin.setDisable(false);
        } else {
            btnLogin.setDisable(true);
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
            msInit.executeStringUpdate(MSQL.CREATE_DB_ROOTCONFIG);// NOT TESTED
        }
        msInit.close();
    }

    private void rootQuerys() {
        msRoot = new MSQLP(new RootConfigConnection());
        msRoot.selectTables(new ShowTablesRootConfig());
        if (!MSQL.isTableDefaultUserExist()) {
            msRoot.executeStringUpdate(MSQL.CREATE_TABLE_DEFAULT_USER);
            msRoot.executeStringUpdate(MSQL.INSERT_TABLE_DEFAULT_USER);
        }

        msRoot.executeQuery(MSQL.SELECT_TABLE_ROW_DEFAULT_USER, new SelectDefaultUser());
        if (MSQL.getUser().equals("NONE")) {
            msRoot.selectUsers(new ShowUsers(this));

            showStage = true;
        }

    }

    // ---------------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // DO QUERYS
        if (initOption.equalsIgnoreCase("Login")) {
            // -----------------------
            tfUserAC = new PopupAutoC(tfUser);
            tfDBAC = new PopupAutoC(tfDB);
            // -----------------------
            initQuerys();
            // -----------------------
            rootQuerys();
            // -----------------------
            lbUser.setTextFill(NonCSS.TEXT_FILL);
            lbPassword.setTextFill(NonCSS.TEXT_FILL);
            lbDB.setTextFill(NonCSS.TEXT_FILL);

            tfUserAC.getLv().getSelectionModel().selectedItemProperty().addListener(this::cbUserSelection);
            tfUser.setOnKeyReleased(this::cbUserKR);
            tfDBAC.getLv().getSelectionModel().selectedItemProperty().addListener(this::cbDBSelection);
            tfDB.setOnKeyReleased(this::cbDBKR);

        }
    }
    // ---------------------------------------------

    public Label getLbUser() {
        return lbUser;
    }

    public Button getBtnLogin() {
        return btnLogin;
    }

    public void setBtnLogin(Button btnLogin) {
        this.btnLogin = btnLogin;
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

    public TextField getTfUser() {
        return tfUser;
    }

    public void setTfUser(TextField tfUser) {
        this.tfUser = tfUser;
    }

    public PasswordField getTfPassword() {
        return tfPassword;
    }

    public void setTfPassword(PasswordField tfPassword) {
        this.tfPassword = tfPassword;
    }

    public TextField getTfDB() {
        return tfDB;
    }

    public void setTfDB(TextField tfDB) {
        this.tfDB = tfDB;
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

    public HBox getLayout() {
        return layout;
    }

    public void setLayout(HBox layout) {
        this.layout = layout;
    }

    public PopupAutoC getTfUserAC() {
        return tfUserAC;
    }

    public void setTfUserAC(PopupAutoC tfUserAC) {
        this.tfUserAC = tfUserAC;
    }

    public PopupAutoC getTfDBAC() {
        return tfDBAC;
    }

    public void setTfDBAC(PopupAutoC tfDBAC) {
        this.tfDBAC = tfDBAC;
    }

}
