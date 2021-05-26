package com.cofii.ts.sql;

import com.cofii2.mysql.Connect;
import com.cofii2.mysql.DefaultConnection;

public class CurrenConnection extends Connect{

    public CurrenConnection() {
        super(DefaultConnection.BASIC_URL + MSQL.getDatabase() + DefaultConnection.CONNECTION_PROPS, MSQL.getUser(), MSQL.getPassword());
    }


}
