package com.cofii.ts.cu.store;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ImageCRow {
    
    private TextField tfPath;
    private Button btnSelectPath;
    private Button btnAdd;
    private Button btnRemove;
    
    public ImageCRow(TextField tfPath, Button btnSelectPath, Button btnAdd, Button btnRemove) {
        this.tfPath = tfPath;
        this.btnSelectPath = btnSelectPath;
        this.btnAdd = btnAdd;
        this.btnRemove = btnRemove;
    }

    public TextField getTfPath() {
        return tfPath;
    }

    public void setTfPath(TextField tfPath) {
        this.tfPath = tfPath;
    }

    public Button getBtnSelectPath() {
        return btnSelectPath;
    }

    public void setBtnSelectPath(Button btnSelectPath) {
        this.btnSelectPath = btnSelectPath;
    }

    public Button getBtnAdd() {
        return btnAdd;
    }

    public void setBtnAdd(Button btnAdd) {
        this.btnAdd = btnAdd;
    }

    public Button getBtnRemove() {
        return btnRemove;
    }

    public void setBtnRemove(Button btnRemove) {
        this.btnRemove = btnRemove;
    }
    
    
}
