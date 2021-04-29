package com.cofii.ts.other;

import com.cofii.ts.first.VFController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Timers {

    private static VFController vf;

    private Timeline lbStatusReset = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
        vf.getLbStatus().setText("Waiting for action...");
        vf.getLbStatus().setTextFill(NonCSS.TEXT_FILL);
    }));
    //--------------------------------------------------
    public void playLbStatusReset(){
        lbStatusReset.play();
    }
    //--------------------------------------------------
    private static Timers instance;
    public static Timers getInstance(VFController vf){
        Timers.vf = vf;
        if(instance == null){
            instance = new Timers();
        }
        return instance;
    }

    private Timers(){

    }
}
