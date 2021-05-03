package com.cofii.ts.other;

import com.cofii2.components.javafx.TextFieldAutoC;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class MultipleValuesSelectedImpl implements ActionForEachNode{

    private static final String MULTIPLE_VALUES_SELECTED = "(Multiple values selected)";

    @Override
    public void forTFS(TextField tf, int a) {
        tf.setText(MULTIPLE_VALUES_SELECTED);
        
    }

    @Override
    public void forTFAS(TextFieldAutoC tfa, int a) {
        tfa.getTf().setText(MULTIPLE_VALUES_SELECTED);
        
    }

    @Override
    public void either(int a) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void getLength(int length) {
        // TODO Auto-generated method stub
        
    }
    
}
