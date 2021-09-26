package com.cofii.ts.cu.fk;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class VFKController implements Initializable{

    private Stage stage;
    // INIT ====================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    // GETTERS & SETTERS =========================================
    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
}
