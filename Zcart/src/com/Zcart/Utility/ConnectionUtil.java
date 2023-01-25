//$Id$
package com.Zcart.Utility;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionUtil {
	
    public Connection getDbConnection() {
    	Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + "ecart");

		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception");
		}catch (Exception e) {
			e.printStackTrace();
		}

		return connection;
    }
}
