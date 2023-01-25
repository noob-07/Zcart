//$Id$
package com.Zcart.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.JSONParser;
import com.Zcart.Utility.ConnectionUtil;
import com.Zcart.Utility.DBUtil;
import com.Zcart.Utility.DynamicQuery;
import com.Zcart.Utility.Utility;
import com.Zcart.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import com.sun.jna.*;

public class UserDao {
	DBUtil dbutil = new DBUtil();
	ConnectionUtil connection = new ConnectionUtil();
	Utility utility = new Utility();
	DynamicQuery generator = new DynamicQuery();
	ArrayList<String> columns = new ArrayList<>();
	ArrayList<String> conditions = new ArrayList<>();
	String query = null;
	int i = 0;

	public String getUser(String email) throws SQLException {
		String result = "";
		try {
		Connection con = connection.getDbConnection();
		columns.add("Email");
		conditions.add("Email");
		query = generator.selectquery(dbutil.USER, columns, conditions);
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString(1, email);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			result = rs.getString("Email");
		}
		columns.removeAll(columns);
		conditions.removeAll(conditions);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public User getUserdetails(int id) {
		User user=new User();
		int userid=0,addressId=0;
		try {
			Connection con = connection.getDbConnection();
	        conditions.add("id");
			query = generator.selectquery(dbutil.USER, columns, conditions);
			conditions.removeAll(conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				user.setName(rs.getString("Name"));
				user.setEmail(rs.getString("Email"));
				user.setMobile(rs.getLong("Mobile"));
				addressId=rs.getInt("address_id");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public int add(User user) {
		int affectedrows = 0,userId=0;
		try {
			Connection con = connection.getDbConnection();
			con.getAutoCommit();
			// get address_id and add address to address table//
			int addressId = addAddress(user);
			// get role_id//
			int roleId = 0;
			if (!user.getEmail().contains("zoho")) {
				roleId = 1;
			} else {
				roleId = 2;
			}
			if(checkValidPassword(user.getPassword())) {
			columns.add("Name");
			columns.add("Email");
			columns.add("Mobile");
			columns.add("role_id");
			columns.add("CreatedAt");
			columns.add("ModifiedAt");
			columns.add("address_id");
			query = generator.insertquery(dbutil.USER, columns);
			PreparedStatement stmt = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, user.getName());
			System.out.println(user.getName());
			stmt.setString(2, user.getEmail());
			System.out.println(user.getEmail());
			stmt.setLong(3, user.getMobile());
			stmt.setInt(4, roleId);
			stmt.setLong(5, utility.CURRENT_TIME);
			stmt.setLong(6, utility.CURRENT_TIME);
			stmt.setInt(7, addressId);
			affectedrows = stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			while (rs.next()) {
				userId=rs.getInt("id");
			}
			columns.removeAll(columns);
			// insert password//
			insertPassword(user,userId);
			System.out.println(affectedrows);
			System.out.println("Added user successfully");
			}
			else {
				System.out.println("set a new password");
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return affectedrows;
	}

	private int getAddressId(int u_id) throws SQLException {
		// TODO Auto-generated method stub
		Connection con = connection.getDbConnection();
		conditions.add("id");
		String query = generator.selectquery(dbutil.USER, columns, conditions);
		PreparedStatement stmt = con.prepareStatement(query);
		conditions.removeAll(conditions);
		stmt.setInt(1, u_id);
		// int affectedrows = stmt.executeUpdate();
		ResultSet rs = stmt.executeQuery();
		int result = 0;
		while (rs.next()) {
			result = rs.getInt("address_id");
		}
		columns.removeAll(columns);
		conditions.removeAll(conditions);
		return result;
	}

	public void insertPassword(User user, int userId) throws SQLException {
		String u_id = String.valueOf(userId);
//		JsonArray jsonarray=new Gson().fromJson(u_id, JsonArray.class);
//		JsonObject obj=jsonarray.get(0).getAsJsonObject();
		int user_id= userId;
		if (user_id == 0) {
			user_id = user.getId();
		}
		if(checkValidPassword(user.getPassword())) {
		String hashedpassword = encryptPassword(user.getPassword());
		Connection con = connection.getDbConnection();
		columns.add("password");
		columns.add("createdAt");
		columns.add("user_id");
		columns.add("isactive");
		String query = generator.insertquery(dbutil.PASSWORD, columns);
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString(1, hashedpassword);
		stmt.setLong(2, utility.CURRENT_TIME);
		stmt.setLong(3, user_id);
		stmt.setBoolean(4, true);
		int affectedrows = stmt.executeUpdate();
		}else {
			System.out.println("Password does not adhere complexity, set a new password");
		}
		columns.removeAll(columns);
	}

	private boolean checkValidPassword(String password) {
		String regex= "(?=(.*[a-z]){2})(?=(.*[A-Z]){2}).{6,16}";
		Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(password);
		return m.matches();
	}

	public String getUserId(String email) throws SQLException {
		List listofusers = new ArrayList<>();
		Connection con = connection.getDbConnection();
		int result = 0;
		columns.add("id");
		conditions.add("Email");
		String query = generator.selectquery(dbutil.USER, columns, conditions);
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString(1, email);
		ResultSet rs = stmt.executeQuery();
		ResultSetMetaData metaData = rs.getMetaData();
		int numColumns = metaData.getColumnCount();
		while (rs.next()) {
			Map row = new HashMap();
			for (int i = 1; i < numColumns + 1; i++) {
				row.put(metaData.getColumnName(i),rs.getObject(i));
			}
			listofusers.add(row);
		}
		columns.removeAll(columns);
		conditions.removeAll(conditions);
		String res=new Gson().toJson(listofusers);
		return res;
	}

	public int addAddress(User user) throws SQLException {
		int addressId=0;
		try {
		Connection con = connection.getDbConnection();
		columns.add("Address1");
		columns.add("Address2");
		columns.add("City");
		columns.add("State");
		columns.add("Country");
		columns.add("Pincode");
		columns.add("CreatedAt");
		columns.add("ModifiedAt");
		String query = generator.insertquery(dbutil.ADDRESS, columns);
		PreparedStatement stmt = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
		ResultSet rs=stmt.getGeneratedKeys();
		stmt.setString(1, user.getAddress1());
		stmt.setString(2, user.getAddress2());
		stmt.setString(3, user.getCity());
		stmt.setString(4, user.getState());
		stmt.setString(5, user.getCountry());
		stmt.setLong(6, user.getPincode());
		stmt.setLong(7, utility.CURRENT_TIME);
		stmt.setLong(8, utility.CURRENT_TIME);
		int affectedrows = stmt.executeUpdate();
		while(rs.next()) {
		addressId=rs.getInt("id");
		}
		columns.removeAll(columns);
		}catch(Exception e) {
			e.printStackTrace();
		}
		 return addressId;
		
	}

	public int getRoleId(User user) {
		String email = user.getEmail();
		if (email.contains("zoho"))
			return 2;
		else
			return 1;
	}

	public String encryptPassword(String password) {
		Argon2 argon2 = Argon2Factory.create();
		String hash = argon2.hash(10, 65536, 1, password);
		return hash;
	}

	public Map getUserAddress(int userId) {
		Map m=new HashMap<>();
		try {
			Connection con = connection.getDbConnection();
			int addressId=getAddressId(userId);
			conditions.add("id");
			String query = generator.selectquery(dbutil.ADDRESS, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			conditions.removeAll(conditions);
			stmt.setInt(1, addressId);
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int numColumns = metaData.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i < numColumns + 1; i++) {
					m.put(metaData.getColumnName(i), rs.getObject(i));
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return m;
	}
	
	public String getPassword(String email) throws SQLException {
		Connection con = connection.getDbConnection();
		String result = "";
		String user_id = getUserId(email);
		JsonArray jsonarray=new Gson().fromJson(user_id, JsonArray.class);
		JsonObject obj=jsonarray.get(0).getAsJsonObject();
		int u_id= obj.get("id").getAsInt();
		columns.add("password");
		conditions.add("user_id");
		conditions.add("isactive");
		String query = generator.selectquery(dbutil.PASSWORD, columns, conditions);
		conditions.removeAll(conditions);
		columns.removeAll(columns);
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setLong(1, u_id);
		stmt.setBoolean(2, true);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			result = rs.getString("Password");
		}
		return result;
	}

	public boolean verifyUser(String email, String password) throws SQLException {
		Argon2 argon2 = Argon2Factory.create();
		String hashedpassword = encryptPassword(password);
		String storedpassword = getPassword(email);
		return argon2.verify(storedpassword, password);
	}
	

	public void updateDetails(User user, Map<String, Object> map, int uid) {
		ArrayList<Object> values = new ArrayList<>();
		try {
			Connection con = connection.getDbConnection();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if (entry.getKey().equalsIgnoreCase("password")) {
					updatepassword(entry.getValue().toString(), uid, user);
					continue;
				}
				columns.add(entry.getKey());
				values.add(entry.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updatepassword(String password, int uid, User user) {
		ArrayList<String> passwordlist = new ArrayList<>();
		ArrayList<Long> createdDate = new ArrayList<>();
		Argon2 argon2 = Argon2Factory.create();
		boolean flag = false;
		try {
			Connection con = connection.getDbConnection();
			conditions.add("user_id");
			String query = generator.selectquery(dbutil.PASSWORD, columns, conditions);
			columns.removeAll(columns);
			conditions.removeAll(conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, uid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				passwordlist.add(rs.getString("password"));
				createdDate.add(rs.getLong(3));
			}
			columns.add("isactive");
			conditions.add("user_id");
			query = generator.updatequery(dbutil.PASSWORD, columns, conditions);
			stmt = con.prepareStatement(query);
			stmt.setBoolean(1,false);
			stmt.setLong(2,uid);
			stmt.executeUpdate();
			columns.removeAll(columns);
			conditions.removeAll(conditions);
				for (int i = 0; i < passwordlist.size(); i++) {
					if (argon2.verify(passwordlist.get(i), password)) {
						System.out.println("Cannot update the password as the previous password");
						flag = true;
						break;
					}
				}
			 if(passwordlist.size()>=3){
				conditions.add("CreatedAt");
				conditions.add("isactive");
				conditions.add("user_id");
				query = generator.deletequery(dbutil.PASSWORD, conditions);
				stmt = con.prepareStatement(query);
				long mindate = Collections.min(createdDate);
				stmt.setLong(1, mindate);
				stmt.setBoolean(2,false);
				stmt.setLong(3, uid);
				stmt.executeUpdate();
				columns.removeAll(columns);
				conditions.removeAll(conditions);
			}
			user.setId(uid);
			insertPassword(user,uid);
			columns.removeAll(columns);
			conditions.removeAll(conditions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
