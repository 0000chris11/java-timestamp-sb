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

    private VFController vf;
    private int index;

    public SelectDistinct(VFController vf, int index) {
        this.vf = vf;
        this.index = index;
    }

    @Override
    public void beforeQuery() {
        vf.getTfsAutoC().get(index).setLvOriginalItems(null);
        vf.getTfsAutoC().get(index).getLv().getItems().clear();
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
            vf.getTfsAutoC().get(index).getLv().getItems().addAll(list2);
            //vf.getTfsAutoC().set(index, TextFields.bindAutoCompletion(vf.getTfs()[index], list2));
            vf.getTfsAutoC().get(index).setLvOriginalItems(list2.toArray(new String[list2.size()]));
        } else {
            vf.getTfsAutoC().get(index).getLv().getItems().add(NO_DISTINCT_ELEMENTS);
        }
    }

}
