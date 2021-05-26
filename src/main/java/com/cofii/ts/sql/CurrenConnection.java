package com.cofii.ts.sql;

import com.cofii2.mysql.Connect;
import com.cofii2.mysql.DefaultConnection;

public class CurrenConnection extends Connect{

    private static final String database = MSQL.getDatabase();
    private static final String user = MSQL.getUser();
    private static final String password = MSQL.getDatabase();

    public CurrenConnection() {
        super(DefaultConnection.BASIC_URL + database + DefaultConnection.CONNECTION_PROPS, user, password);
    }


}
