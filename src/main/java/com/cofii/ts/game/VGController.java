package com.cofii.ts.game;

import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.CustomTransitions;
import com.cofii2.components.javafx.LabelStatus;
import com.cofii2.components.javafx.TimeLabel;
import com.cofii2.components.javafx.popup.PopupAutoC;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VGController implements Initializable {

    @FXML
    private VBox vboxMain;
    // MAIN--------------------------------------------
    @FXML
    private Label lbDatabaseTable;

    @FXML
    private Label lbGuessColumn;
    @FXML
    private TextField tfGuessColumn;
    private PopupAutoC tfGuessColumnAutoC;
    @FXML
    private Label lbAnswerColumn;
    @FXML
    private TextField tfAnswerColumn;
    private PopupAutoC tfAnswerColumnAutoC;
    // TAB-PANE-----------------------------
    @FXML
    private TabPane tabPane;
    // START------------------------
    @FXML
    private Tab tabStart;
    @FXML
    private VBox vboxStart;
    @FXML
    private Button btnStart;
    //GAME-------------------
    private TimeLabel lbTimer = new TimeLabel();
    private Label lbGuess = new Label();

    private HBox hboxAnswer;
    private TextField tfAnswer = new TextField();
    private Button btnAnswer = new Button();
    // OPTIONS----------------------
    @FXML
    private Tab tabOptions;
    @FXML
    private HBox hbMatch;
    @FXML
    private Label lbMatch;

    @FXML
    private ToggleButton btnEquals;
    @FXML
    private ToggleButton btnIgnoreCase;
    @FXML
    private ToggleButton btnContains;

    private LabelStatus lbMainStatus;
    private LabelStatus lbMatchOptionsStatus;

    //CONTROL-------------------------------------------
    private boolean guessMatch = false;
    private boolean answerMatch = false;
    private boolean matchOptions = true;

    private void btnStartControl(){
        if(guessMatch && answerMatch && matchOptions){
            String guessText = tfGuessColumn.getText();
            String answerText = tfAnswerColumn.getText();
            if(!guessText.equals(answerText)){
                btnStart.setDisable(false);
                lbMainStatus.reset();
            }else{
                btnStart.setDisable(true);
                lbMainStatus.setText("Column Guess and Answer have to be diferent", Color.RED);
            }
        }else{
            btnStart.setDisable(true);
            lbMainStatus.setText("Column Guess or Answer are incorrect", Color.RED);
        }
    }
    //LISTENERS-----------------------------------------
    private void tfGuessColumnTextProperty(String newValue){
        guessMatch = tfGuessColumnAutoC.getLvOriginalItems().stream().anyMatch(s -> s.equals(newValue));
        btnStartControl();
    }
    private void tfAnswerColumnTextProperty(String newValue){
        answerMatch = tfGuessColumnAutoC.getLvOriginalItems().stream().anyMatch(s -> s.equals(newValue));
        btnStartControl();
    }

    private void btnsMatchOptionsAction(ActionEvent e){
        boolean equalsSelected = btnEquals.isSelected();
        boolean ignoreCaseSelected = btnIgnoreCase.isSelected();
        boolean containsSelected = btnContains.isSelected();

        if(!equalsSelected && !ignoreCaseSelected && !containsSelected){
            lbMatchOptionsStatus.setText("At least one option has to be selected", Color.RED);
            matchOptions = false;
        }else{
            lbMatchOptionsStatus.reset();
            matchOptions = true;
        }

        btnStartControl();
    }
    
    private void btnStartAction(ActionEvent e){
        vboxStart.getChildren().clear();

        vboxStart.getChildren().addAll(lbTimer, lbGuess, hboxAnswer);
    }

    private void btnAnswerAction(ActionEvent e){
        
    }
    // INIT----------------------------------------------
    private void initGameNodes(){
        CustomTransitions.translateToRightAndFadeIn(lbGuess, Duration.millis(400));
        CustomTransitions.getFadeTransition().play();
        CustomTransitions.getTranslateTransition().play();

        tfAnswer.setPromptText("Type Answer");

        btnAnswer.setOnAction(this::btnAnswerAction);

        hboxAnswer = new HBox(tfAnswer, btnAnswer);
        hboxAnswer.setPadding(new Insets(6, 10, 10, 10));
        hboxAnswer.setSpacing(6);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //NODES---------------------------------
        lbMainStatus = new LabelStatus(null, LabelStatus.LEFT);
        vboxMain.getChildren().add(2, lbMainStatus);

        lbMatchOptionsStatus = new LabelStatus(null, LabelStatus.LEFT);
        hbMatch.getChildren().add(lbMatchOptionsStatus);

        initGameNodes();
        //-----------------------------------------
        Database currentDatabase = Users.getInstance().getCurrenUser().getCurrentDatabase();
        Table currenTable = currentDatabase.getCurrentTable();
        lbDatabaseTable.setText(currentDatabase.getName() + "." + currenTable.getName());

        String[] columns = currenTable.getColumnNames().toArray(new String[currenTable.getColumns().size()]);
        tfGuessColumnAutoC = new PopupAutoC(tfGuessColumn, columns);
        tfAnswerColumnAutoC = new PopupAutoC(tfAnswerColumn, columns);
        //LISTENERS----------------------------------
        tfGuessColumn.textProperty().addListener((obs, oldValue, newValue) -> tfGuessColumnTextProperty(newValue));
        tfAnswerColumn.textProperty().addListener((obs, oldValue, newValue) -> tfAnswerColumnTextProperty(newValue));

        btnEquals.setOnAction(this::btnsMatchOptionsAction);
        btnIgnoreCase.setOnAction(this::btnsMatchOptionsAction);
        btnContains.setOnAction(this::btnsMatchOptionsAction);

        btnStart.setOnAction(this::btnStartAction);
    }

}
