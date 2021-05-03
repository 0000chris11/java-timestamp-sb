package com.cofii.ts.other;

import com.cofii2.components.javafx.TextFieldAutoC;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class GetRowSelectedImpl implements ActionForEachNode {

    private Object[] values;
    public GetRowSelectedImpl(Object[] values){

        this.values = values;
    }

    @Override
    public void forTFS(TextField tf, int c) {
        tf.setText(values[c].toString());

    }

    @Override
    public void forTFAS(TextFieldAutoC tfa, int c) {
        tfa.getTf().setText(values[c].toString());
    }

    @Override
    public void either(int c) {
        //NOT IMPL
    }

    @Override
    public void getLength(int length) {
        // TODO Auto-generated method stub
        
    }

    
}
