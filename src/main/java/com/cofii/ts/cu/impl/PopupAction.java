package com.cofii.ts.cu.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cofii.ts.cu.store.VCStore;
import com.cofii.ts.other.CSS;
import com.cofii2.components.javafx.popup.PopupMessage;

import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;
import javafx.scene.Node;

public class PopupAction {

    private static final String TABLE_WRONG = "Table name is incorrect or already exist";
    private static final String COLUMNS_SAME_NAME = "Columns can't have the same name";

    private static final String NODES_ILLEGAL_CHARS = "Some nodes have 'Illegal Chars' errors";
    private static final String NODES_EMPTY_TEXT = "Some nodes have 'Empty Text' errors";
    private static final String NODES_SELECTION_UNMATCH = "Some nodes don't match their respective selection-list";
    private static final String NODES_TYPE_AND_DEFAULT = "Some columns types are not correct";
    private static final String NODES_AUTO_INCREMENT_AND_PK = "Some columns have AUTO_INCREMENT set but not PK";
    private static final String NODES_AUTO_INCREMENT_AND_FK = "Some columns have AUTO_INCREMENT with a FK";
    private static final String NODES_AUTO_INCREMENT_AND_DEFAULT = "Some columns have AUTO_INCREMENT with a Default value";
    private static final String NODES_AUTO_INCREMENT_AND_DIST = "Some columns have AUTO_INCREMENT with a Custom-Dist";
    // -------------------------------------------------------------------
    private PopupMessage popupMessage;
    private Node displayNode;

    private String singleId;
    private BooleanProperty booleanProperty;
    private Node singleDisplayNode;

    private static Set<String> errorDataDisplay = new HashSet<>();
    private static PopupMessage popupMaster;

    // CONSTRUCTOR --------------------------------------------------------
    public PopupAction(PopupMessage popupMessage) {
        popupMessage.getItemList().addListener(this::columnsPopupsChange);

        this.displayNode = popupMessage.getParentNode();

        // errorData.add(popupMessage.getId(), popupMessage.getItemList());
        this.popupMessage = popupMessage;

    }

    public PopupAction(String singleId, BooleanProperty booleanProperty, Node singleDisplayNode) {
        this.singleId = singleId;
        booleanProperty.addListener((obs, oldValue, newValue) -> booleanPropertyChage(newValue));
        this.booleanProperty = booleanProperty;
        this.singleDisplayNode = singleDisplayNode;
    }

    // CONTROL -----------------------------------------------------------
    private void errorDisplay() {
        if (popupMaster != null) {
            popupMaster.getItemList().removeIf(s -> !s.contains("id-"));
            errorDataDisplay.forEach(s -> {
                if (s.contains(VCStore.ILLEGAL_CHARS)) {
                    popupMaster.getItemList().add(NODES_ILLEGAL_CHARS);
                } else if (s.contains(VCStore.EMPTY_TEXT)) {
                    popupMaster.getItemList().add(NODES_EMPTY_TEXT);
                } else if (s.contains(VCStore.SELECTION_UNMATCH)) {
                    popupMaster.getItemList().add(NODES_SELECTION_UNMATCH);

                } else if (s.contains(VCStore.WRONG_LENGTH) || s.contains(VCStore.WRONG_LENGTH_2)) {
                    popupMaster.getItemList().add(NODES_TYPE_AND_DEFAULT);
                } else if (s.contains(VCStore.AUTO_INCREMENT_AND_PK)) {
                    popupMaster.getItemList().add(NODES_AUTO_INCREMENT_AND_PK);
                } else if (s.contains(VCStore.AUTO_INCREMENT_AND_FK)) {
                    popupMaster.getItemList().add(NODES_AUTO_INCREMENT_AND_FK);
                } else if (s.contains(VCStore.AUTO_INCREMENT_AND_DEFAULT)) {
                    popupMaster.getItemList().add(NODES_AUTO_INCREMENT_AND_DEFAULT);
                } else if (s.contains(VCStore.AUTO_INCREMENT_AND_DIST)) {
                    popupMaster.getItemList().add(NODES_AUTO_INCREMENT_AND_DIST);

                } else if (s.equals(TABLE_WRONG)) {
                    popupMaster.getItemList().add(TABLE_WRONG);
                } else if (s.equals(COLUMNS_SAME_NAME)) {
                    popupMaster.getItemList().add(COLUMNS_SAME_NAME);
                }
            });
        }
    }

    // LISTENERS -----------------------------------------------------------
    public void booleanPropertyChage(boolean newValue) {
        if (newValue) {
            if (singleId.equals("id-TABLE")) {
                errorDataDisplay.remove(TABLE_WRONG);
            } else if (singleId.equals("id-SAMECOLUMNS")) {
                errorDataDisplay.remove(COLUMNS_SAME_NAME);
            }
            singleDisplayNode.setStyle(null);
        } else {
            if (singleId.equals("id-TABLE")) {
                errorDataDisplay.add(TABLE_WRONG);
            } else if (singleId.equals("id-SAMECOLUMNS")) {
                errorDataDisplay.add(COLUMNS_SAME_NAME);
            }
            singleDisplayNode.setStyle(CSS.NODE_BORDER_ERROR);
        }
        errorDisplay();
    }

    // private static int count = 1;
    public void columnsPopupsChange(Change<? extends String> c) {
        if (!popupMessage.getVbox().getChildren().isEmpty()) {
            displayNode.setStyle(CSS.NODE_BORDER_ERROR);
        } else {
            displayNode.setStyle(null);
        }

        /**
         * Illegal Chars%COLUMN%id-COLUMN:1
         * 
         */
        String id = popupMessage.getId();
        // List<String> collection =
        // getItemList().stream().collect(Collectors.toList());
        List<String> collection = getItemList().stream().filter(s -> !s.contains("id-")).map(s -> s + "%id" + id)
                .collect(Collectors.toList());

        errorDataDisplay.addAll(collection);
        // System.out.println("\tadded size: " + errorDataDisplay.size());
        errorDataDisplay.removeIf(s -> s.contains("%id" + id) && !collection.stream().anyMatch(ss -> ss.contains("%id" + id)));
        // System.out.println("\tafter removeIf size: " + errorDataDisplay.size());

        // System.out.println("\nTEST ERROR-DATA (" + (count++) + ")");
        // errorDataDisplay.forEach(s -> System.out.println("\tE: " + s));
        errorDisplay();
    }

    // GETTTERS & SETTERS --------------------------------------------------
    public ObservableSet<String> getItemList() {
        return popupMessage.getItemList();
    }

    public static PopupMessage getPopupMaster() {
        return popupMaster;
    }

    public static void setPopupMaster(PopupMessage popupMaster) {
        PopupAction.popupMaster = popupMaster;
    }

    public String getSingleId() {
        return singleId;
    }

    public void setSingleId(String singleId) {
        this.singleId = singleId;
    }

    public Boolean getValue() {
        return booleanProperty.getValue();
    }

    public void setValue(Boolean booleanProperty) {
        this.booleanProperty.setValue(booleanProperty);
    }

}
