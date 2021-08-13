package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.methods.MList;
import com.cofii2.myInterfaces.IActions;

public class ShowUsers implements IActions {

    private VLController vlc;
    private Users users;

    public ShowUsers(VLController vlc) {
        this.vlc = vlc;
    }

    @Override
    public void beforeQuery() {
        vlc.getTfUserAC().clearItems();
        users = Users.getInstance();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        int id = rs.getInt(1);
        String user = rs.getString(2);

        vlc.getTfUserAC().addItem(user);
        users.addUser(new User(id, user));

    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if (rsValue) {
            vlc.setUserOK(true);
            vlc.getTfUserAC().getLv().getSelectionModel().select(0);
        }

    }

}
