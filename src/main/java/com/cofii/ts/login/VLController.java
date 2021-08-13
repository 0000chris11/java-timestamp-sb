package com.cofii.ts.login;

import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import com.cofii.ts.first.VF;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDefaultUser;
import com.cofii.ts.sql.querys.RootConfigExist;
import com.cofii.ts.sql.querys.ShowTablesRootConfig;
import com.cofii.ts.sql.querys.ShowUsers;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.components.javafx.popup.PopupKV;
import com.cofii2.components.javafx.popup.PopupMenu;
import com.cofii2.methods.MList;
import com.cofii2.mysql.DefaultConnection;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.RootConfigConnection;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VLController implements Initializable {

    private Stage stage;
    private Scene scene;
    // TOP----------------------------------
    @FXML
    private Label lbTitle;
    // CENTER-------------------------------
    @FXML
    private Label lbUser;
    @FXML
    private TextField tfUser;
    private PopupAutoC tfUserAC;

    @FXML
    private Label lbPassword;
    @FXML
    private PasswordField tfPassword;
    // BOTTOM-------------------------------
    @FXML
    private HBox hbBottom;
    @FXML
    private CheckBox cbRemember;
    @FXML
    private Button btnLogin;
    private Timeline tlHelp;
    @FXML
    private Button btnLoginHelp;
    private PopupKV btnLoginHelpPopup;
    private ObservableMap<String, Boolean> btnLoginHelpMap = FXCollections.observableHashMap();
    @FXML
    private Button btnCreate;
    private PopupMenu btnCreatePM = new PopupMenu("User", "Database");
    // ---------------------------------------------
    private MSQLP msInit = new MSQLP(new DefaultConnection());
    private MSQLP msRoot;
    private String initOption = "Login";

    private boolean showStage = false;
    // -----------------------------------------------
    private BooleanProperty userOK = new SimpleBooleanProperty(false);
    private BooleanProperty passOK = new SimpleBooleanProperty(false);

    private boolean noUser = false;

    // CONTROL---------------------------------------
    private void btnLoginControl() {
        userOK.setValue(lbUser.getTextFill().equals(NonCSS.TEXT_FILL));
        passOK.setValue(lbPassword.getTextFill().equals(NonCSS.TEXT_FILL));
        // boolean dbOK = lbDB.getTextFill().equals(NonCSS.TEXT_FILL);

        if (!noUser && userOK.getValue() && passOK.getValue() /* && dbOK */) {
            btnLogin.setDisable(false);
        } else {
            btnLogin.setDisable(true);
        }
    }

    private void disableCenter(boolean disable){
        lbUser.setDisable(disable);
        tfUser.setDisable(disable);

        lbPassword.setDisable(disable);
        tfPassword.setDisable(disable);
    }
    // LISTENERS=========================================
    // CENTER------------------------------------------------
    private void tfUserKeyReleased(KeyEvent e) {
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

        btnLoginControl();

    }

    private void tfUserSelectionChangeListener(ObservableValue<? extends String> object, String oldValue,
            String newValue) {
        lbUser.setTextFill(NonCSS.TEXT_FILL);
        btnLoginControl();
    }

    private void tfPasswordKeyReleased(KeyEvent e) {
        e.consume();
        if (!lbPassword.getText().equals("Password")) {
            lbPassword.setText("Password");
        }

        if (tfPassword.getText().isEmpty()) {
            lbPassword.setTextFill(NonCSS.TEXT_FILL_ERROR);
        } else {
            lbPassword.setTextFill(NonCSS.TEXT_FILL);
        }

        btnLoginControl();
    }

    private void tfDBKeyReleased(KeyEvent e) {
        TextField tfDB = (TextField) e.getSource();
        String text = tfDB.getText();
        if (text.isEmpty()) {
            // lbDB.setTextFill(NonCSS.TEXT_FILL_ERROR);
        } else {
            /*
             * if (MList.isOnThisList(tfDBAC.getLv().getItems(), text, true)) {
             * lbDB.setTextFill(NonCSS.TEXT_FILL); } else {
             * lbDB.setTextFill(NonCSS.TEXT_FILL_ERROR); }
             */
        }

        btnLoginControl();
    }

    private void tfDBSelectionChangeListener(ObservableValue<? extends String> object, String oldValue,
            String newValue) {
        // lbDB.setTextFill(NonCSS.TEXT_FILL);
        btnLoginControl();
    }

    // BOTTOM----------------------------------------------
    private void btnLoginAction(ActionEvent e) {
        e.consume();
        String user = tfUser.getText();
        String password = tfPassword.getText();

        boolean correctPassword = msRoot.selectCorrectPassword(MSQL.TABLE_USERS, "user_password", user, password);
        if (correctPassword) {
            MSQL.setUser(user);
            MSQL.setPassword(password);
            //ADDING CURRENT USER--------------------------------------------
            Object[] valueId = msRoot.selectValues(MSQL.TABLE_USERS, "id", "user_name", user);
            if(valueId.length == 1 &&  valueId[0] instanceof Integer){
            Users.getInstance().setCurrenUser(new User((int) valueId[0], user));
            }else{
                throw new IllegalArgumentException("C0FII: FATAL wrong value recived from User table (expected single id)");
            }
            //DEFAULT USER------------------------------------------
            if (cbRemember.isSelected()) {
                msRoot.executeStringUpdate(MSQL.UPDATE_TABLE_DEFAULT_USER);
            }
            //---------------------------------------------
            disableCenter(true);
            btnLogin.setText("Connect");
            //CREATE DATABASE FIELD

            //msRoot.close();
            new VF(this);
        }
    }

    private void btnLoginHelpAction(ActionEvent e) {
        btnLoginHelpPopup.showPopup();
    }

    private void btnLoginHelpMapReset() {
        btnLoginHelpMap.put("User", userOK.getValue());
        btnLoginHelpMap.put("Password", passOK.getValue());
    }

    private void btnLoginDisablePropertyChangeListener(boolean newValue) {
        if (newValue) {
            tlHelp.play();
        } else {
            tlHelp.stop();
        }
    }

    private void btnCreateAction(ActionEvent e) {
        btnCreatePM.showPopup((Button) e.getSource());
    }

    // INIT---------------------------------------------
    private void initQuery() {
        msInit.selectDatabases(new RootConfigExist(this));// AND ADDING TO cbDB
        if (!MSQL.isDbRootconfigExist()) {
            msInit.executeStringUpdate(MSQL.CREATE_DB_ROOTCONFIG);// NOT TESTED
        }
        msInit.close();
    }

    private void rootQuerys() {
        msRoot = new MSQLP(new RootConfigConnection());
        msRoot.selectTables(new ShowTablesRootConfig());
        if (!MSQL.isTableUsersExist()) {
            msRoot.executeStringUpdate(MSQL.CREATE_TABLE_USERS);
            noUser = true;
        }
        if (!MSQL.isTableDatabasesExist()) {
            msRoot.executeStringUpdate(MSQL.CREATE_TABLE_DATABASES);
        }
        if (!MSQL.isTableDefaultUserExist()) {
            msRoot.executeStringUpdate(MSQL.CREATE_TABLE_DEFAULT_USER);
            msRoot.executeStringUpdate(MSQL.INSERT_TABLE_DEFAULT_USER);
        }
    }

    // ---------------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (initOption.equalsIgnoreCase("Login")) {
            // -----------------------
            lbUser.setTextFill(NonCSS.TEXT_FILL);            
            tfUserAC = new PopupAutoC(tfUser);
            tfUserAC.setShowOption(PopupAutoC.WHEN_FOCUS);
            tfUserAC.getNoSearchableItems().add("NO USERS FOUND");
            tfUserAC.addItem("NO USERS FOUND");
            
            lbPassword.setTextFill(NonCSS.TEXT_FILL_ERROR);
            // tfDBAC = new PopupAutoC(tfDB);
            // tfDBAC.setShowOption(PopupAutoC.WHEN_FOCUS);
            btnLoginHelpMapReset();
            btnLoginHelpPopup = new PopupKV(btnLoginHelp, btnLoginHelpMap);
            // -----------------------
            initQuery();
            // -----------------------
            rootQuerys();
            // -----------------------
            userOK.addListener((obs, oldValue, newValue) -> btnLoginHelpMapReset());
            passOK.addListener((obs, oldValue, newValue) -> btnLoginHelpMapReset());

            tfUserAC.getLv().getSelectionModel().selectedItemProperty()
                    .addListener(this::tfUserSelectionChangeListener);
            tfUser.setOnKeyReleased(this::tfUserKeyReleased);
            // tfDBAC.getLv().getSelectionModel().selectedItemProperty().addListener(this::cbDBSelection);
            // tfDB.setOnKeyReleased(this::cbDBKR);
            tfPassword.setOnKeyReleased(this::tfPasswordKeyReleased);

            btnLogin.disableProperty()
                    .addListener((obs, oldValue, newValue) -> btnLoginDisablePropertyChangeListener(newValue));
            btnLogin.setOnAction(this::btnLoginAction);

            btnLoginHelp.setOnAction(this::btnLoginHelpAction);

            btnCreate.setContextMenu(btnCreatePM);
            btnCreate.setOnAction(this::btnCreateAction);
            // AFTER LISTENERS--------------------------
            btnLogin.setDisable(true);
            // ------------------------
            tlHelp = new Timeline(
                    new KeyFrame(Duration.seconds(2), new KeyValue(btnLoginHelp.textFillProperty(), Color.RED)));
            // timeline.setAutoReverse(true);
            tlHelp.setCycleCount(Animation.INDEFINITE);

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
        return hbBottom;
    }

    public void setLayout(HBox layout) {
        this.hbBottom = layout;
    }

    public PopupAutoC getTfUserAC() {
        return tfUserAC;
    }

    public void setTfUserAC(PopupAutoC tfUserAC) {
        this.tfUserAC = tfUserAC;
    }

    public MSQLP getMsRoot() {
        return msRoot;
    }

    public void setMsRoot(MSQLP msRoot) {
        this.msRoot = msRoot;
    }

    public boolean isUserOK() {
        return userOK.getValue();
    }

    public void setUserOK(boolean userOK) {
        this.userOK.setValue(userOK);
    }

}
