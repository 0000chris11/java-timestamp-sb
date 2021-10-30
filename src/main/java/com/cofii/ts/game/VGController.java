package com.cofii.ts.game;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.CustomTransitions;
import com.cofii2.components.javafx.LabelStatus;
import com.cofii2.components.javafx.TimeLabel;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.methods.MOthers;

import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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
    private ListView<ToggleButton> lvGuessColumns;
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

    // GAME-------------------
    private TimeLabel lbTimer = new TimeLabel();

    private Label lbGuess = new Label();
    private CustomTransitions lbGuessTransition = new CustomTransitions(lbGuess);
    private ObservableList<ObservableList<Object>> data;

    private Label lbMessage = new Label();
    private CustomTransitions lbMessageTransition = new CustomTransitions(lbMessage);

    private HBox hboxAnswer;
    private TextField tfAnswer = new TextField();
    private Button btnAnswer = new Button("Submit");

    private Button btnStop = new Button("Stop");
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

    // CONTROL-------------------------------------------
    private Database currentDatabase;
    private Table currentTable;

    private boolean guessMatch = false;
    private boolean answerMatch = false;
    private boolean matchOptions = true;

    private String answer;

    private void btnStartControl() {
        if (guessMatch && answerMatch && matchOptions) {
            String answerText = tfAnswerColumn.getText();
            boolean noneMatch = lvGuessColumns.getItems().stream().noneMatch(btn -> btn.getText().equals(answerText));
            if (noneMatch) {
                btnStart.setDisable(false);
                lbMainStatus.reset();
            } else {
                btnStart.setDisable(true);
                lbMainStatus.setText("Column Guess and Answer have to be diferent", lbMainStatus.getTextFillError());
            }
        } else {
            btnStart.setDisable(true);
            lbMainStatus.setText("Column Guess or Answer are incorrect", lbMainStatus.getTextFillError());
        }
    }

    private void randomStart() {
        int randomIndex = MOthers.getRandomNumber(1, data.size());
        ObservableList<Object> randomRow = data.get(randomIndex - 1);
        String randomGuess = randomRow.get(currentTable.getColumnIndex(lvGuessColumns.getItems().get(0).getText()))
                .toString();
        answer = randomRow.get(currentTable.getColumnIndex(tfAnswerColumn.getText())).toString();

        lbGuess.setText(randomGuess);
        lbGuessTransition.play();
    }

    // LISTENERS-----------------------------------------
    private void lvSelectedItemsListener() {
        guessMatch = !lvGuessColumns.getSelectionModel().getSelectedItems().isEmpty();
        btnStartControl();
    }

    private void tfAnswerColumnTextProperty(String newValue) {
        answerMatch = tfAnswerColumnAutoC.getLvOriginalItems().stream().anyMatch(s -> s.equals(newValue));
        btnStartControl();
    }

    private void btnsMatchOptionsAction(ActionEvent e) {
        boolean equalsSelected = btnEquals.isSelected();
        boolean ignoreCaseSelected = btnIgnoreCase.isSelected();
        boolean containsSelected = btnContains.isSelected();

        if (!equalsSelected && !ignoreCaseSelected && !containsSelected) {
            lbMatchOptionsStatus.setText("At least one option has to be selected", lbMatchOptionsStatus.getTextFillError());
            matchOptions = false;
        } else {
            lbMatchOptionsStatus.reset();
            matchOptions = true;
        }

        btnStartControl();
    }

    private void btnStartAction(ActionEvent e) {
        vboxStart.getChildren().clear();

        vboxStart.getChildren().addAll(lbTimer, lbGuess, lbMessage, hboxAnswer, btnStop);

        randomStart();

        lbTimer.start();
    }

    private void btnAnswerAction(ActionEvent e) {
        String text = tfAnswer.getText();
        StringBuilder sb = new StringBuilder();

        boolean correct = false;
        if (btnEquals.isSelected()) {
            if (btnIgnoreCase.isSelected()) {
                if (text.equalsIgnoreCase(answer)) {
                    sb.append("Equals: Correct!");
                    correct = true;
                } else {
                    sb.append("Equals: Wrong!");
                }
            } else {
                if (text.equals(answer)) {
                    sb.append("Equals: Correct");
                    correct = true;
                } else {
                    sb.append("Equals: Wrong!");
                }
            }
        }

        if (btnContains.isSelected()) {
            if (btnIgnoreCase.isSelected()) {
                answer = answer.toLowerCase();
                text = text.toLowerCase();
                if (answer.contains(text)) {
                    sb.append(" - Contains: Correct!");
                    correct = true;
                } else {
                    sb.append(" - Contains: Wrong!");
                }
            } else {
                if (answer.contains(text)) {
                    sb.append(" - Contains: Correct");
                    correct = true;
                } else {
                    sb.append(" - Contains: Wrong!");
                }
            }
        }

        String message = sb.toString();
        if (message.startsWith(" - ")) {
            message = message.replaceFirst(" - ", "");
        }
        lbMessage.setText(message);
        lbMessageTransition.play();

        if (correct) {
            randomStart();
        }
    }

    private void btnStopAction(ActionEvent e) {
        lbTimer.resetTimer();

        vboxStart.getChildren().clear();
        vboxStart.getChildren().add(btnStart);
    }

    // INIT----------------------------------------------
    private void initGameNodes() {
        lbGuess.getStyleClass().add("lbGuess");

        Duration duration = Duration.millis(400);
        lbGuessTransition.addFadeIn(duration);
        lbGuessTransition.addTranslateToRight(duration);
        lbMessageTransition.addFadeIn(duration);

        tfAnswer.setPromptText("Type Answer");

        btnAnswer.setOnAction(this::btnAnswerAction);
        btnStop.setOnAction(this::btnStopAction);

        hboxAnswer = new HBox(tfAnswer, btnAnswer);
        hboxAnswer.setPrefWidth(-1);
        hboxAnswer.setPrefHeight(-1);
        hboxAnswer.setAlignment(Pos.CENTER);
        hboxAnswer.setPadding(new Insets(10, 0, 10, 0));
        hboxAnswer.setSpacing(6);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // NODES---------------------------------
        lbMainStatus = new LabelStatus(null, LabelStatus.LEFT);
        vboxMain.getChildren().add(2, lbMainStatus);

        lbMatchOptionsStatus = new LabelStatus(null, LabelStatus.LEFT);
        hbMatch.getChildren().add(lbMatchOptionsStatus);

        initGameNodes();
        // -----------------------------------------
        currentDatabase = Users.getInstance().getCurrenUser().getCurrentDatabase();
        currentTable = currentDatabase.getCurrentTable();
        lbDatabaseTable.setText(currentDatabase.getName() + "." + currentTable.getName());

        String[] columns = currentTable.getColumnNames().toArray(new String[currentTable.getColumns().size()]);
        ToggleButton[] btns = Arrays.asList(columns).stream().map(s -> {
            ToggleButton toggleButton = new ToggleButton(s);
            toggleButton.minWidthProperty().bind(lvGuessColumns.widthProperty().subtract(18));
            return toggleButton;
        }).toArray(size -> new ToggleButton[size]);

        lvGuessColumns.getItems().addAll(btns);
        tfAnswerColumnAutoC = new PopupAutoC(tfAnswerColumn, columns);
        // LISTENERS----------------------------------
        lvGuessColumns.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvGuessColumns.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> lvSelectedItemsListener());
        tfAnswerColumn.textProperty().addListener((obs, oldValue, newValue) -> tfAnswerColumnTextProperty(newValue));

        btnEquals.setOnAction(this::btnsMatchOptionsAction);
        btnIgnoreCase.setOnAction(this::btnsMatchOptionsAction);
        btnContains.setOnAction(this::btnsMatchOptionsAction);

        btnStart.setOnAction(this::btnStartAction);
    }

    // --------------------------------------------------
    public ObservableList<ObservableList<Object>> getData() {
        return data;
    }

    public void setData(ObservableList<ObservableList<Object>> data) {
        this.data = data;
    }

}
