package com.cofii.ts.first;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.cofii.ts.other.CSS;
import com.cofii2.components.javafx.RectangleButton;
import com.cofii2.components.javafx.popup.PopupAutoC;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VFRow {

    public static final double DEFAULT_PROP_CHILDREN_WIDTH = 60;
    public static final String DEFAULT_PROP_CHILDREN_STYLE = "-fx-text-fill: black";
    // OTHERS ----------------------------
    private static VFController vfc;
    // LEFT -------------------------------
    private final Label lb = new Label();
    // CENTER -----------------------------
    private final VBox vbCenter = new VBox();
    private final HBox hbProperty = new HBox();

    private final TextField tf = new TextField();
    private final PopupAutoC tfAutoC = new PopupAutoC(tf);
    private final ObservableList<String> fkRefList = FXCollections.observableArrayList();
    // RIGHT ------------------------------
    private final HBox hbBtns = new HBox();
    private final ToggleButton btnHideProperties = new ToggleButton("^");
    private final ToggleButton btnMain = new ToggleButton();

    // QOL ========================================================
    public static int getIndexByColumnName(String columnName) {
        int[] indexs = { -1 };
        final String columnNamee = columnName.replace(" ", "_");

        vfc.getRows().stream().anyMatch(row -> {
            indexs[0]++;
            return row.getLb().getText().replace(" ", "_").equals(columnNamee);
        });
        return indexs[0];
    }

    public static VFRow getRowByColumnName(String columnName) {
        final String columnNamee = columnName.replace(" ", "_");

        List<VFRow> returnValue = vfc.getRows().stream()
                .filter(row -> row.getLb().getText().replace(" ", "_").equals(columnNamee))
                .collect(Collectors.toList());

        return !returnValue.isEmpty() ? returnValue.get(0) : null;
    }

    public static Predicate<? super Node> getRemoveIfPredicate(String nodeText) {
        return node -> {
            if (node instanceof RectangleButton) {
                return ((RectangleButton) node).getLabel().getText().equals(nodeText);
            } else {
                return false;
            }
        };
    }

    // LISTENERS ==================================================
    private final ChangeListener<? super String> tfFKTextPropertyListener = this::tfFKTextProperty;

    // CENTER -------------------------------
    private void hbPropertyChildrenChange(Change<? extends Node> c) {
        while (c.next()) {
            boolean visible = !c.getList().isEmpty();
            if (visible) {
                btnHideProperties.setSelected(true);
                btnHideProperties.setVisible(true);
            } else {
                btnHideProperties.setVisible(false);
            }

        }
    }

    private void fkListChange(Change<? extends String> c) {
        while (c.next()) {
            if (c.wasAdded() && c.getList().size() == 1) {
                tf.textProperty().removeListener(tfFKTextPropertyListener);
                tf.textProperty().addListener(tfFKTextPropertyListener);
            } else if (c.getList().isEmpty()) {
                tf.textProperty().removeListener(tfFKTextPropertyListener);
            }
        }

    }

    private void tfFKTextProperty(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        boolean match = fkRefList.stream().anyMatch(s -> tf.getText().equals(s));
        tf.setStyle(CSS.TFS_FK_LOOK + "; " + (match ? null : CSS.NODE_BORDER_ERROR));
        vfc.btnAddUpdateControl();
    }

    // RIGHT -------------------------------
    private void btnsHidePropertiesAction(ActionEvent e) {
        hbProperty.setVisible(btnHideProperties.isSelected());
    }

    private void btnsOnAction(ActionEvent e) {
        // int index = Integer.parseInt(((ToggleButton) e.getSource()).getId());
        if (vfc != null) { // FOR TESTING ONLY
            int index = vfc.getRows().indexOf(this);
            if (!btnMain.isSelected()) {
                tf.setText("");
            } else {
                if (vfc.getSelectedRow() != null) {
                    tf.setText(vfc.getSelectedRow()[index] != null ? vfc.getSelectedRow()[index].toString() : "");
                } else {
                    tf.setText("");
                }
            }
        }
    }

    // CONSTRUCTOR ================================================
    public VFRow(String columnName) {
        // LEFT ======================================================
        lb.setText(columnName);
        // CENTER ======================================================
        fkRefList.addListener(this::fkListChange);
        hbProperty.setSpacing(4.0);
        hbProperty.managedProperty().bind(hbProperty.visibleProperty());
        hbProperty.getChildren().addListener(this::hbPropertyChildrenChange);

        vbCenter.getChildren().addAll(hbProperty, tf);
        // RIGHT ======================================================
        // BTN HIDE -------------------------

        // GLITCHING GRAPHICS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // ImageView arrow = new ImageView(new Image(
        // VFRow.class.getResource("/com/cofii/ts/icons/play
        // icon.png").toExternalForm(), 18, 20, false, true));

        // arrow.setFitWidth(18);
        // arrow.fitHeightProperty().bind(btnMain.heightProperty());
        // arrow.setRotate(270);
        // btnHideProperties = new Button(null, arrow);
        // btnHideProperties.setPadding(Insets.EMPTY);

        // btnHideProperties.setGraphic(arrow);
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // ----------------------------------------------------------
        btnHideProperties.setPadding(Insets.EMPTY);
        btnHideProperties.setMinWidth(18);
        btnHideProperties.setPrefHeight(-1);

        btnHideProperties.managedProperty().bind(btnHideProperties.visibleProperty());
        btnHideProperties.setVisible(false);

        btnHideProperties.setOnAction(this::btnsHidePropertiesAction);
        // BTN MAIN --------------------------
        btnMain.minHeightProperty().bind(tf.heightProperty());
        btnMain.setMinWidth(18);

        btnMain.setSelected(true);
        btnMain.setOnAction(this::btnsOnAction);
        // LAYOUT ----------------------------
        hbBtns.setAlignment(Pos.BOTTOM_LEFT);
        hbBtns.getChildren().addAll(btnMain, btnHideProperties);
    }

    // GETTERS & SETTERS ==========================================
    public Label getLb() {
        return lb;
    }

    public HBox getHbProperty() {
        return hbProperty;
    }

    public TextField getTf() {
        return tf;
    }

    public ToggleButton getBtnMain() {
        return btnMain;
    }

    public VBox getVbCenter() {
        return vbCenter;
    }

    public PopupAutoC getTfAutoC() {
        return tfAutoC;
    }

    public ObservableList<String> getFkRefList() {
        return fkRefList;
    }

    public static VFController getVfc() {
        return vfc;
    }

    public static void setVfc(VFController vfc) {
        VFRow.vfc = vfc;
    }

    public HBox getHbBtns() {
        return hbBtns;
    }

    public ToggleButton getBtnHideProperties() {
        return btnHideProperties;
    }

}
