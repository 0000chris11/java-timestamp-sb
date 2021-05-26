package com.cofii.ts.other;

import com.cofii.ts.cu.VCController;
import com.cofii.ts.first.VFController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.stage.Popup;
import javafx.util.Duration;

public class Timers {

    private static VFController vf;
    private VCController vc;
    private Node tooltipNode;

    private Popup popup;

    private Timeline lbStatusReset = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
        vf.getLbStatus().setText("Waiting for action...");
        vf.getLbStatus().setTextFill(NonCSS.TEXT_FILL);
    }));
    private Timeline tooltipManualShow = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
        Tooltip tt = ((Control) tooltipNode).getTooltip();
        if (tt != null) {
            tt.hide();
        }
    }));
    private Timeline popupHide = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
        popup.hide();
    }));

    // --------------------------------------------------
    public void playLbStatusReset() {
        lbStatusReset.play();
    }

    public void playTooltipManualShow(Node tooltipNode, VCController vc) {
        this.vc = vc;
        this.tooltipNode = tooltipNode;

        tooltipManualShow.play();
    }

    public void playPopupHide(Popup popup) {
        this.popup = popup;

        popupHide.play();
    }

    public void playPopupHide(Popup popup, Duration duration) {
        this.popup = popup;

        popupHide.setDelay(duration);
        popupHide.play();
    }

    // --------------------------------------------------
    private static Timers instance;

    public static Timers getInstance(VFController vf) {
        Timers.vf = vf;
        if (instance == null) {
            instance = new Timers();
        }
        return instance;
    }

    private Timers() {

    }
}
