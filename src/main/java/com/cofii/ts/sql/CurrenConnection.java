package com.cofii.ts.sql;

import com.cofii2.mysql.Connect;
import com.cofii2.mysql.DefaultConnection;

public class CurrenConnection extends Connect{

    private String database;
    private String user;
    private String password;

    public CurrenConnection(String database, String user, String password) {
        super(DefaultConnection.BASIC_URL + database + DefaultConnection.CONNECTION_PROPS, user, password);
    }


}
