package com.cofii.ts.login;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class VLController implements Initializable{

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // DO QUERYS
        
    }
    
}
