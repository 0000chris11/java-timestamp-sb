package com.cofii.ts.first.nodes;

import com.cofii.ts.first.VFRow;
import com.cofii2.components.javafx.RectangleButton;

import javafx.geometry.Insets;
import javafx.scene.paint.Color;

public class RectangelButtonImpl extends RectangleButton{

    public RectangelButtonImpl(String text, Color rectangleFill) {
        super(text, rectangleFill);
        setPrefWidth(VFRow.DEFAULT_PROP_CHILDREN_WIDTH);
        getLabel().setStyle(VFRow.DEFAULT_PROP_CHILDREN_STYLE);
        setMargin(getLabel(), new Insets(0, 6, 0, 6));
    }
    
}
