package com.cofii.ts.options;

import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.first.VFController;
import com.cofii.ts.store.main.Users;
import com.cofii2.xml.ResourceXML;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class VOController implements Initializable{

    @FXML
    private HBox hboxMain;
    //TOP---------------------------
    @FXML
    private HBox hbTop;
    @FXML
    private Label lbTitle;
    //CENETER------------------------
    @FXML
    private CheckBox ckInsertClear;
    @FXML
    private CheckBox ckUpdateClear;
    //BOTTOM-----------------------
    @FXML
    private HBox hbBottom;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSaveChanges;
    //OTHERS-------------------------------
    private VFController vfc;
    //BOOLEANS--------------------------------
    private boolean ckInsertClearValue = false;
    private boolean ckUpdateClearValue = false;
    //CONTROL-------------------------------------
    private void btnSaveChangesControl(){
        boolean allOk = ckInsertClearValue && ckUpdateClearValue;
        btnSaveChanges.setDisable(!allOk);
    }
    //LISTENERS-------------------------------------
    private void ckInsertClearSelectedProperty(boolean newValue){
        ckInsertClearValue = newValue;
        btnSaveChangesControl();
    }
    private void ckUpdateClearSelectedProperty(boolean newValue){
        ckUpdateClearValue = newValue;
        btnSaveChangesControl();
    }

    private void btnSaveChangesAction(ActionEvent e){
        //SET VARIABLES vfc;
        new ResourceXML(Users.DEFAULT_RESOURCE, ResourceXML.UPDATE_XML, doc -> {
            Element defaultUser = (Element) doc.getDocumentElement().getElementsByTagName("defaultUser").item(0);
            Element options = (Element) defaultUser.getElementsByTagName("options").item(0);
            
            Node insertClearValue = options.getElementsByTagName("insertClear").item(0).getAttributes().item(0);
            boolean insertClear = Boolean.getBoolean(insertClearValue.getTextContent());
            if(ckInsertClearValue != insertClear){
                insertClearValue.setTextContent(Boolean.toString(ckInsertClearValue));
            }

            Node updateClearValue = options.getElementsByTagName("updateClear").item(0).getAttributes().item(0);
            boolean updateClear = Boolean.getBoolean(updateClearValue.getTextContent());
            if(ckUpdateClearValue != updateClear){
                updateClearValue.setTextContent(Boolean.toString(ckUpdateClearValue));
            }
            
            return doc;
        });
    }
    //INIT------------------------------------------
    private void initListeners(){
        //CENTER---------------
        ckInsertClear.selectedProperty().addListener((obs, oldValue, newValue) -> ckInsertClearSelectedProperty(newValue));
        ckUpdateClear.selectedProperty().addListener((obs, oldValue, newValue) -> ckUpdateClearSelectedProperty(newValue));
        //BOTTOM---------------
        btnSaveChanges.setOnAction(this::btnSaveChangesAction);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initListeners();
        
    }
    //GETTERS & SETTERS--------------------------------
    public VFController getVfc() {
        return vfc;
    }
    public void setVfc(VFController vfc) {
        this.vfc = vfc;
    }
    
}
