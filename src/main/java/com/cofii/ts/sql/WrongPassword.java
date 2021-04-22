package com.cofii.ts.sql;

import java.sql.SQLException;

import com.cofii2.mysql.interfaces.IConnectionException;

public class WrongPassword implements IConnectionException{

    @Override
    public void exception(SQLException ex) {
        System.out.println(ex.getMessage());
        
    }

    @Override
    public void succes() {
        System.out.println("SUCCES");
        
    }
    
}
