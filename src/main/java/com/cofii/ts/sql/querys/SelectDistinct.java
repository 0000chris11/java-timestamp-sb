package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.cofii.ts.first.VF;
import com.cofii.ts.first.VFController;
import com.cofii2.components.javafx.TextFieldAutoC;
import com.cofii2.methods.MList;
import com.cofii2.myInterfaces.IActions;

import org.controlsfx.control.textfield.TextFields;

public class SelectDistinct implements IActions {

    public static final String NO_DISTINCT_ELEMENTS = "No distinct elements";

    private List<String> list = new ArrayList<>();

    private VFController vfc;
    private int index;

    public SelectDistinct(VFController vfc, int index) {
        this.vfc = vfc;
        this.index = index;
    }

    @Override
    public void beforeQuery() {
        //vfc.getTfsAutoC().get(index).clearItems();
        vfc.getRows().get(index).getTfAutoC().clearItems();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String element = rs.getString(1);
        if (element.contains("; ")) {
            String[] tags = element.split("; ");
            list.addAll(Arrays.asList(tags));
        } else {
            if (!element.isEmpty()) {
                list.add(element);
            }
        }

    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if (rsValue) {
            List<String> list2 = new ArrayList<>(new LinkedHashSet<>(list));
            //vf.getTfsAutoC().get(index).getLv().getItems().addAll(list2);
            //vf.getTfsAutoC().set(index, TextFields.bindAutoCompletion(vf.getTfs()[index], list2));
            vfc.getRows().get(index).getTfAutoC().addAllItems(list2);
        } else {
            vfc.getRows().get(index).getTfAutoC().addItem(NO_DISTINCT_ELEMENTS);
            vfc.getRows().get(index).getTfAutoC().getNoSearchableItems().add(NO_DISTINCT_ELEMENTS);
        }
    }

}
