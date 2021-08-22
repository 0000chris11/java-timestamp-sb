package com.cofii.ts.login;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.cofii.ts.first.VF;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.LabelStatus;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.components.javafx.popup.PopupKV;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.store.KeyPassword;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VLController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private VBox vboxMain;
    // TOP----------------------------------
    @FXML
    private Label lbTitle;
    @FXML
    private Button btnGoBack;
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
    private Label lbConfirmPassword;
    private PasswordField tfConfirmPassword;
    // BOTTOM-------------------------------
    private LabelStatus lbStatus;
    @FXML
    private HBox hbBottom;
    @FXML
    private CheckBox cbRemember;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnLoginHelp;
    private Timeline timelineLoginHelp;
    private PopupKV btnLoginHelpPopup;
    private ObservableMap<String, Boolean> btnLoginHelpMap = FXCollections.observableHashMap();
    @FXML
    private Button btnCreateUser;
    @FXML
    private Button btnCreateUserHelp;
    private Timeline timeLineUserHelp;
    private PopupKV btnCreateUserHelpPopup;
    private ObservableMap<String, Boolean> btnCreateUserHelpMap = FXCollections.observableHashMap();
    // private PopupMenu btnCreatePM = new PopupMenu("User", "Database");

    // ---------------------------------------------
    private MSQLP msRoot;

    private boolean showStage = false;
    private BooleanProperty createUserState = new SimpleBooleanProperty(false);

    private Pattern patternNewUser = Pattern.compile("[\\w\\s]+");
    // -----------------------------------------------
    private BooleanProperty loginUserOK = new SimpleBooleanProperty(false);
    private BooleanProperty loginPassOK = new SimpleBooleanProperty(false);

    private BooleanProperty createUserUserOk = new SimpleBooleanProperty(false);
    private BooleanProperty createUserPassOk = new SimpleBooleanProperty(false);
    private BooleanProperty createUserConfirmPassOk = new SimpleBooleanProperty(false);

    private boolean noUser = false;

    // CONTROL---------------------------------------
    private void btnLoginControl() {
        if (loginUserOK.getValue() && loginPassOK.getValue() /* && dbOK */) {
            btnLogin.setDisable(false);
        } else {
            btnLogin.setDisable(true);
        }
    }

    private void btnCreateUserControl() {
        boolean allOk = createUserUserOk.getValue() && createUserPassOk.getValue()
                && createUserConfirmPassOk.getValue();
        btnCreateUser.setDisable(!allOk);
    }

    private void disableCenter(boolean disable) {
        lbUser.setDisable(disable);
        tfUser.setDisable(disable);

        lbPassword.setDisable(disable);
        tfPassword.setDisable(disable);
    }

    // LISTENERS=========================================
    // CENTER------------------------------------------------
    private void tfUserTextPropertyChange() {
        String text = tfUser.getText();
        boolean userMatch = tfUserAC.getLv().getItems().stream()
                .anyMatch(s -> s.equalsIgnoreCase(text.trim().replace(" ", "_")));

        if (Boolean.FALSE.equals(createUserState.getValue())) {
            if (text.isEmpty()) {
                lbUser.setTextFill(NonCSS.TEXT_FILL_ERROR);
            } else {

                if (userMatch) {
                    lbUser.setTextFill(NonCSS.TEXT_FILL);
                    loginUserOK.setValue(true);
                } else {
                    lbUser.setTextFill(NonCSS.TEXT_FILL_ERROR);
                    loginUserOK.setValue(false);
                }
            }

            btnLoginControl();
        } else {
            boolean patternOK = patternNewUser.matcher(text).matches();
            createUserUserOk.setValue(!userMatch && patternOK);

            btnCreateUserControl();
        }
    }

    private void tfUserSelectionChangeListener(ObservableValue<? extends String> object, String oldValue,
            String newValue) {
        lbUser.setTextFill(NonCSS.TEXT_FILL);
        btnLoginControl();
    }

    private void tfPasswordKeyReleased(KeyEvent e) {
        PasswordField pf = (PasswordField) e.getSource();
        int index = vboxMain.getChildren().indexOf(e.getSource()) - 1;
        Label lbPass = (Label) vboxMain.getChildren().get(index);

        if (!lbPass.getText().equals("Password")) {
            lbPass.setText("Password");
        }

        if (pf.getText().isEmpty()) {
            lbPass.setTextFill(NonCSS.TEXT_FILL_ERROR);

            if (Boolean.TRUE.equals(createUserState.getValue())) {
                if (pf.getId().equals("pass")) {
                    createUserPassOk.setValue(false);
                } else {
                    createUserConfirmPassOk.setValue(false);
                }
            } else {
                loginPassOK.setValue(false);
            }
        } else {
            lbPass.setTextFill(NonCSS.TEXT_FILL);

            if (Boolean.TRUE.equals(createUserState.getValue())) {
                if (pf.getId().equals("pass")) {
                    createUserPassOk.setValue(true);
                } else {
                    createUserConfirmPassOk.setValue(true);
                }
            } else {
                loginPassOK.setValue(true);
            }
        }

        if (Boolean.FALSE.equals(createUserState.getValue())) {
            btnLoginControl();
        } else {
            btnCreateUserControl();
        }
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

    private void loginAction() {
        String user = tfUser.getText();
        String password = tfPassword.getText();

        boolean correctPassword = msRoot.selectCorrectPassword(MSQL.TABLE_USERS, "user_password", user, password);
        if (correctPassword) {
            MSQL.setUser(user);
            MSQL.setPassword(password);
            // ADDING CURRENT USER--------------------------------------------
            Object[] valueId = msRoot.selectValues(MSQL.TABLE_USERS, "id", "user_name", user);
            if (valueId.length == 1 && valueId[0] instanceof Integer) {
                Users.getInstance().setCurrentUser(new User((int) valueId[0], user));
            } else {
                throw new IllegalArgumentException(
                        "C0FII: FATAL wrong value recived from User table (expected single id)");
            }
            // DEFAULT USER------------------------------------------
            if (cbRemember.isSelected()) {
                msRoot.executeStringUpdate(MSQL.UPDATE_TABLE_DEFAULT_USER);
            }
            // ---------------------------------------------
            disableCenter(true);
            btnLogin.setText("Connect");
            // CREATE DATABASE FIELD

            // msRoot.close();
            new VF(this);
        }
    }

    private void btnLoginAction(ActionEvent e) {
        e.consume();
        if (Boolean.FALSE.equals(createUserState.getValue())) {
            loginAction();
        } else {
            createUserState.setValue(false);
        }
    }

    private void btnLoginHelpAction(ActionEvent e) {
        btnLoginHelpPopup.showPopup();
    }

    private void btnLoginHelpMapReset() {
        btnLoginHelpMap.put("User", loginUserOK.getValue());
        btnLoginHelpMap.put("Password", loginPassOK.getValue());
    }

    private void btnCreateUserHelpMapReset() {
        btnCreateUserHelpMap.put("User", createUserUserOk.getValue());
        btnCreateUserHelpMap.put("Password", createUserPassOk.getValue());
        btnCreateUserHelpMap.put("Confirm Password", createUserConfirmPassOk.getValue());
    }

    private void btnLoginDisablePropertyChangeListener(boolean newValue) {
        if (newValue) {
            timelineLoginHelp.play();
        } else {
            timelineLoginHelp.stop();
        }
    }

    private void btnCreateUserDisablePropertyChangeListener(boolean newValue) {
        if (newValue) {
            timeLineUserHelp.play();
        } else {
            timeLineUserHelp.stop();
        }
    }

    private void btnCreateUserAction(ActionEvent e) {
        System.out.println("TEST createUserState.getValue(): " + createUserState.getValue());
        if (Boolean.TRUE.equals(createUserState.getValue())) {
            String user = tfUser.getText().trim().toLowerCase().replace(" ", "_");
            String password = tfPassword.getText().trim();
            String confirmPassword = tfConfirmPassword.getText().trim();

            if (password.equals(confirmPassword)) {
                msRoot.setKeyPassword(new KeyPassword(3, user, password));
                // TEST NEW USER AT MYSQL-WORKSPACE
                boolean insert = msRoot.insert(MSQL.TABLE_USERS, new Object[] { null, user, password });
                if (insert) {
                    lbStatus.setText("User created!", Color.GREEN, Duration.seconds(3));
                    int id = (int) msRoot.selectValues(MSQL.TABLE_USERS, "id", "user_name", user)[0];

                    Users.getInstance().addUser(new User(id, user));
                    if (tfUserAC.getLvOriginalItems().contains("NO USERS FOUND")) {
                        tfUserAC.clearItems();
                    }
                    tfUserAC.addItem(user);
                } else {
                    lbStatus.setText("User failed to be created.", Color.RED);
                }
            } else {
                lbStatus.setText("Confirm Password must be the same as the original", Color.RED);
            }
        } else {
            createUserState.setValue(true);
        }
    }

    private void createUserStateChanged(boolean createUser) {
        if (createUser) {
            btnCreateUserHelpMapReset();
            btnCreateUserHelpPopup = new PopupKV(btnCreateUserHelp, btnCreateUserHelpMap);
            btnCreateUserHelpPopup.getHeaderBar().getStyleClass().add("headerBarPopupKV");
            // LOGIN CHANGE----------------------------
            lbTitle.setText("Create User");
            btnGoBack.setVisible(true);

            tfUserAC.setTfParent(null);

            btnLogin.setVisible(false);
            btnLoginHelp.setVisible(false);
            btnCreateUser.setDisable(true);
            btnCreateUser.getStyleClass().removeAll("buttonQueryAction", "buttonQueryAction:pressed");
            btnCreateUser.getStyleClass().addAll("buttonQueryAction", "buttonQueryAction:pressed");
            btnCreateUserHelp.setVisible(true);
            cbRemember.setVisible(false);
            timelineLoginHelp.stop();
            // NEW FIELDS ------------------------------------------
            lbConfirmPassword = new Label("Confirm Password");
            tfConfirmPassword = new PasswordField();
            lbConfirmPassword.setId("lb-confirm-pass");
            tfConfirmPassword.setId("confirm-pass");
            tfConfirmPassword.setOnKeyReleased(this::tfPasswordKeyReleased);
            vboxMain.getChildren().add(6, lbConfirmPassword);
            vboxMain.getChildren().add(7, tfConfirmPassword);
        } else {
            // LOGIN CHANGE----------------------------
            lbTitle.setText("Login");
            btnGoBack.setVisible(false);

            tfUserAC.setTfParent(tfUser);

            btnLogin.setVisible(true);
            btnLoginHelp.setVisible(true);
            btnCreateUser.setDisable(false);
            btnCreateUser.getStyleClass().removeAll("buttonQueryAction", "buttonQueryAction:pressed");
            btnCreateUserHelp.setVisible(false);
            cbRemember.setVisible(true);
            timeLineUserHelp.stop();
            // DELETE FIELD IF EXIST--------------------
            vboxMain.getChildren().removeIf(n -> n.getId() != null ? n.getId().contains("confirm-pass") : false);
        }

        tfUser.setText("");
        lbPassword.setTextFill(NonCSS.TEXT_FILL);
        tfPassword.setText("");
    }

    // INIT ---------------------------------------------
    private void initNodesConfig(){
        // CENTER ----------------------------
        lbUser.setTextFill(NonCSS.TEXT_FILL);

        tfPassword.setId("pass");

        tfUserAC = new PopupAutoC(tfUser);
        tfUserAC.setShowOption(PopupAutoC.WHEN_FOCUS);
        tfUserAC.getNoSearchableItems().add("NO USERS FOUND");
        tfUserAC.addItem("NO USERS FOUND");

        lbPassword.setTextFill(NonCSS.TEXT_FILL_ERROR);
        // BOTTOM--------------------------------------------
        btnCreateUserHelp.managedProperty().bind(btnCreateUserHelp.visibleProperty());

        // btnLogin.getStyleClass().removeAll("buttonQueryAction",
        // "buttonQueryAction:pressed");
        btnLogin.getStyleClass().addAll("buttonQueryAction", "buttonQueryAction:pressed");

        btnLoginHelpMapReset();
        btnLoginHelpPopup = new PopupKV(btnLoginHelp, btnLoginHelpMap);
        btnLoginHelpPopup.getHeaderBar().getStyleClass().add("headerBarPopupKV");

        lbStatus = new LabelStatus("Waiting for action...", LabelStatus.RIGHT);
        vboxMain.getChildren().add(vboxMain.getChildren().indexOf(hbBottom), lbStatus);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initNodesConfig();
        // LISTENERS ---------------------------------------------
        // TOP--------------------------------------
        btnGoBack.setOnAction(e -> createUserState.setValue(false));
        // CENTER-----------------------------------
        loginUserOK.addListener((obs, oldValue, newValue) -> btnLoginHelpMapReset());
        loginPassOK.addListener((obs, oldValue, newValue) -> btnLoginHelpMapReset());

        createUserUserOk.addListener((obs, oldValue, newValue) -> btnCreateUserHelpMapReset());
        createUserPassOk.addListener((obs, oldValue, newValue) -> btnCreateUserHelpMapReset());
        createUserConfirmPassOk.addListener((obs, oldValue, newValue) -> btnCreateUserHelpMapReset());

        tfUserAC.getLv().getSelectionModel().selectedItemProperty().addListener(this::tfUserSelectionChangeListener);
        // tfUser.setOnKeyReleased(this::tfUserKeyReleased);
        tfUser.textProperty().addListener((obs, oldValue, newValue) -> tfUserTextPropertyChange());
        tfPassword.setOnKeyReleased(this::tfPasswordKeyReleased);
        // BOTTOM-----------------------------------
        btnLogin.disableProperty()
                .addListener((obs, oldValue, newValue) -> btnLoginDisablePropertyChangeListener(newValue));
        btnLogin.setOnAction(this::btnLoginAction);
        btnLoginHelp.setOnAction(this::btnLoginHelpAction);

        btnCreateUser.disableProperty()
                .addListener((obs, oldValue, newValue) -> btnCreateUserDisablePropertyChangeListener(newValue));
        btnCreateUser.setOnAction(this::btnCreateUserAction);
        btnCreateUserHelp.setOnAction(e -> btnCreateUserHelpPopup.showPopup());

        createUserState.addListener((obs, oldValue, newValue) -> createUserStateChanged(newValue));
        // AFTER LISTENERS--------------------------
        btnLogin.setDisable(true);
        // SOME THREADS-----------------------------
        timelineLoginHelp = new Timeline(
                new KeyFrame(Duration.seconds(2), new KeyValue(btnLoginHelp.textFillProperty(), Color.RED)));
        timeLineUserHelp = new Timeline(
                new KeyFrame(Duration.seconds(2), new KeyValue(btnCreateUserHelp.textFillProperty(), Color.RED)));
        // timeline.setAutoReverse(true);
        timelineLoginHelp.setCycleCount(Animation.INDEFINITE);
        timeLineUserHelp.setCycleCount(Animation.INDEFINITE);

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
        return loginUserOK.getValue();
    }

    public void setUserOK(boolean userOK) {
        this.loginUserOK.setValue(userOK);
    }

}
