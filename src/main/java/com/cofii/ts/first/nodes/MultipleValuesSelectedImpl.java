package com.cofii.ts.first.nodes;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MultipleValuesSelectedImpl implements ActionForEachNode{

    private static final String MULTIPLE_VALUES_SELECTED = "(Multiple values selected)";

    @Override
    public void forTFS(TextField tf, int a) {
        tf.setText(MULTIPLE_VALUES_SELECTED);
        
    }

    @Override
    public void forTAS(TextArea tfa, int a) {
        tfa.setText(MULTIPLE_VALUES_SELECTED);
        
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
