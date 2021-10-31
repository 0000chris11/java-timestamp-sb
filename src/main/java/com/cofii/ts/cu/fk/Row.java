package com.cofii.ts.cu.fk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.cofii.ts.cu.impl.PopupAction;
import com.cofii.ts.cu.store.VCStore;
import com.cofii.ts.sql.MSQL;
import com.cofii2.components.javafx.drag.Draggable2;
import com.cofii2.components.javafx.drag.Target;
import com.cofii2.components.javafx.popup.Message;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.components.javafx.popup.PopupMessage;
import com.cofii2.components.javafx.popup.PopupMessageControl;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Row extends VBox {

    private int rowId;
    private String rowType;
    // NODE DISPLAY ----------------------
    private HBox top;
    private Label lbN;

    private FlowPane targetPane;
    private Label lbDragHere;

    private HBox propertiesPane;
    private TextField tfConstraintName;
    private TextField tfReference;
    private PopupAutoC referenceAutoC;

    private Message constraintMessage;
    private Message referenceMessage;

    private static String[] fkReferences;

    private static final List<Message> referenceMessages = new ArrayList<>(MSQL.MAX_COLUMNS);
    private static final List<Message> constraintMessages = new ArrayList<>(MSQL.MAX_COLUMNS);
    // ----------------------------------------------------------
    private static VFKController vfkc;
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private boolean emptyRow = true;
    private boolean referenceOk = false;
    private boolean constraintOk = false;

    // CONTROL ====================================================
    static void allOkControl() {
        Predicate<? super Node> onlyNonEmptyRows = child -> !((Row) child).isEmptyRow();
        Predicate<? super Node> predicate = child -> {
            Row row = (Row) child;
            if (!row.isEmptyRow()) {
                return row.isConstraintOk() && row.isReferenceOk();
            } else {
                return false;
            }
        };

        boolean anyNonEmptyRow = vfkc.getVbSingle().getChildren().stream().anyMatch(onlyNonEmptyRows) || vfkc.getVbGroup().getChildren().stream().anyMatch(onlyNonEmptyRows);
        boolean allSingleMatchReference = vfkc.getVbSingle().getChildren().stream().filter(onlyNonEmptyRows).allMatch(predicate);
        boolean allGroupMatchReference = vfkc.getVbGroup().getChildren().stream().filter(onlyNonEmptyRows).allMatch(predicate);

        //vfkc.getVcc().setFkSelectionMatch(allSingleMatchReference && allGroupMatchReference);

        boolean allOk = anyNonEmptyRow && allSingleMatchReference && allGroupMatchReference;
        vfkc.getBtnAddFK().setDisable(!allOk);
    }

    // LISTENERS ==================================================
    private void targetChildrenChange(Change<? extends Node> c) {
        while (c.next()) {
            long delay = 0;
            if (c.wasRemoved()) {
                delay = 500;
            }
            executor.schedule(() -> {
                Platform.runLater(() -> {
                    // DISPLAY ------------------------------------------------
                    if (targetPane.getChildren().stream().anyMatch(Button.class::isInstance)) {
                        lbDragHere.setVisible(false);
                        propertiesPane.setDisable(false);
                    } else {
                        lbDragHere.setVisible(true);
                        propertiesPane.setDisable(true);
                    }
                    // BLOCK TARGET --------------------------------------------
                    /*
                     * final boolean dropSingle = targetPane.getChildren().size() == 2;
                     * Draggable2.getTargetNodes().stream().filter(target ->
                     * target.getId().equals("single-" + rowId)) .forEach(target ->
                     * target.setDropAllowed(!dropSingle));
                     */
                    // EMPTY ROW CONTROL ------------------------------------
                    emptyRow = targetPane.getChildren().size() == 1;
                    vfkc.groupsControl();

                });
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    private void constraintTextPropertyChange(ObservableValue<? extends String> obs, String oldValue, String newValue) {
        constraintOk = !newValue.trim().isEmpty();
        if(constraintOk){
            constraintMessage.getItemList().remove(VCStore.EMPTY_TEXT);
        }else{
            constraintMessage.getItemList().add(VCStore.EMPTY_TEXT);
        }

        allOkControl();
    }

    private void referenceTextPropertyChange(ObservableValue<? extends String> obs, String oldValue, String newValue) {
        if (referenceAutoC.getLv().getItems().stream().anyMatch(item -> item.equals(newValue))) {
            referenceMessage.getItemList().remove(VCStore.SELECTION_UNMATCH);
            referenceOk = true;
        } else {
            referenceMessage.getItemList().add(VCStore.SELECTION_UNMATCH);
            referenceOk = false;
        }

        allOkControl();
    }

    // CONSTRUCTOR ================================================
    public Row(String rowType, int rowId) {
        getStyleClass().add("single-or-group-vbox");
        Insets insets = new Insets(6, 6, 6, 6);
        double spacing = 6.0;
        // TOP ---------------------------------------
        top = new HBox();
        top.setPadding(insets);
        top.setSpacing(spacing);

        if (rowType.equals("Single")) {
            lbN = new Label("Single " + (rowId + 1));
        } else {
            lbN = new Label("Group " + (rowId + 1));
        }
        top.getChildren().add(lbN);
        // DROP --------------------------------------
        targetPane = new FlowPane();
        targetPane.setPadding(insets);

        lbDragHere = new Label("Drop Buttons Here");
        lbDragHere.managedProperty().bind(lbDragHere.visibleProperty());

        targetPane.getChildren().addAll(lbDragHere);
        // PROPERTIES ---------------------------------
        propertiesPane = new HBox();
        propertiesPane.setPadding(insets);
        propertiesPane.setSpacing(spacing);
        propertiesPane.setDisable(true);
        // CONSTRAINT
        tfConstraintName = new TextField();
        tfConstraintName.setPromptText("CONSTRAINT NAME");
        tfConstraintName.textProperty().addListener(this::constraintTextPropertyChange);
        HBox.setHgrow(tfConstraintName, Priority.ALWAYS);

        constraintMessage = new Message("FKCONS:" + rowId, tfConstraintName);
        new PopupMessageControl(constraintMessage);
        constraintMessages.add(rowId, constraintMessage);
        // REFERENCE
        tfReference = new TextField();
        tfReference.setPromptText("REFERENCE");
        tfReference.textProperty().addListener(this::referenceTextPropertyChange);
        HBox.setHgrow(tfReference, Priority.ALWAYS);

        referenceAutoC = new PopupAutoC(tfReference, fkReferences);
        referenceAutoC.setShowOption(PopupAutoC.WHEN_FOCUS);
        // fkPopupsControl.add(index, new PopupAction(new PopupMessage("FK:" + index,
        // hbsFK.get(index), false)));
        referenceMessage = new Message("FKREF:" + rowId, tfReference);
        new PopupMessageControl(referenceMessage);
        referenceMessages.add(rowId, referenceMessage);

        propertiesPane.getChildren().addAll(tfConstraintName, tfReference);
        // --------------------------------------------------
        targetPane.getChildren().addListener(this::targetChildrenChange);

        getChildren().addAll(top, targetPane, propertiesPane);
        if (rowType.equals("Single")) {
            Draggable2.getTargetNodes().add(new Target("single-" + rowId, targetPane, null, 2));
        } else {
            Draggable2.getTargetNodes().add(new Target("group-" + rowId, targetPane, null));
        }

        this.rowId = rowId;
        this.rowType = rowType;
    }
    // GETTERS & SETTERS ------------------------------------------------

    public static String[] getFkReferences() {
        return fkReferences;
    }

    public static void setFkReferences(String[] fkReferences) {
        Row.fkReferences = fkReferences;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public HBox getTop() {
        return top;
    }

    public void setTop(HBox top) {
        this.top = top;
    }

    public Label getLbN() {
        return lbN;
    }

    public void setLbN(Label lbN) {
        this.lbN = lbN;
    }

    public FlowPane getTargetPane() {
        return targetPane;
    }

    public void setTargetPane(FlowPane targetPane) {
        this.targetPane = targetPane;
    }

    public Label getLbDragHere() {
        return lbDragHere;
    }

    public void setLbDragHere(Label lbDragHere) {
        this.lbDragHere = lbDragHere;
    }

    public HBox getPropertiesPane() {
        return propertiesPane;
    }

    public void setPropertiesPane(HBox propertiesPane) {
        this.propertiesPane = propertiesPane;
    }

    public TextField getTfConstraintName() {
        return tfConstraintName;
    }

    public void setTfConstraintName(TextField tfConstraintName) {
        this.tfConstraintName = tfConstraintName;
    }

    public TextField getTfReference() {
        return tfReference;
    }

    public void setTfReference(TextField tfReference) {
        this.tfReference = tfReference;
    }

    public boolean isEmptyRow() {
        return emptyRow;
    }

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }

    public String getRowType() {
        return rowType;
    }

    public void setRowType(String rowType) {
        this.rowType = rowType;
    }

    public static List<Message> getReferencemessages() {
        return referenceMessages;
    }

    public static List<Message> getConstraintmessages() {
        return constraintMessages;
    }

    public static VFKController getVfkc() {
        return vfkc;
    }

    public static void setVfkc(VFKController vfkc) {
        Row.vfkc = vfkc;
    }

    public boolean isReferenceOk() {
        return referenceOk;
    }

    public void setReferenceOk(boolean referenceOk) {
        this.referenceOk = referenceOk;
    }

    public boolean isConstraintOk() {
        return constraintOk;
    }

    public void setConstraintOk(boolean constraintOk) {
        this.constraintOk = constraintOk;
    }

}
