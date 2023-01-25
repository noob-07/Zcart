//$Id$
package com.Zcart.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.Zcart.Utility.ConnectionUtil;
import com.Zcart.Utility.DBUtil;
import com.Zcart.Utility.DynamicQuery;
import com.Zcart.Utility.Utility;
import com.Zcart.model.Cart;

public class CartDao {

	DBUtil dbutil = new DBUtil();
	ConnectionUtil connection = new ConnectionUtil();
	Utility utility = new Utility();
	DynamicQuery generator = new DynamicQuery();
	ArrayList<String> columns = new ArrayList<>();
	ArrayList<String> conditions = new ArrayList<>();
	String query = null;
	int i = 0;

	public void addToCart(Cart cart, int uid) {
		try {
			Connection con = connection.getDbConnection();
			columns.add("quantity");
			columns.add("user_id");
			columns.add("product_id");
			columns.add("created_time");
			columns.add("modified_time");
			columns.add("order_confirmation");
			String query = generator.insertquery(dbutil.ORDER_DETAILS, columns);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, cart.getQuantity());
			stmt.setInt(2, uid);
			stmt.setInt(3, cart.getProductId());
			stmt.setLong(4, utility.CURRENT_TIME);
			stmt.setLong(5, utility.CURRENT_TIME);
			stmt.setBoolean(6, false);
			int rows = stmt.executeUpdate();
			System.out.println("Added to cart successfully");
			columns.removeAll(columns);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteFromCart(Cart cart, int uid) {
		try {
			Connection con = connection.getDbConnection();
			//conditions.add("id");
			conditions.add("user_id");
			conditions.add("product_id");
			conditions.add("order_confirmation");
			String query = generator.deletequery(dbutil.ORDER_DETAILS, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			//stmt.setInt(1, cid);
			stmt.setInt(1, uid);
			stmt.setInt(2, cart.getProductId());
			stmt.setBoolean(3, false);
			int rows = stmt.executeUpdate();
			System.out.println("Deleted product from cart successfully");
			conditions.removeAll(conditions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Cart> getCartItems(int userId) {
		List<Cart> cartitems = new ArrayList<>();
		try {
			Connection con = connection.getDbConnection();
			columns.add("id");
			columns.add("quantity");
			columns.add("product_id");
			conditions.add("user_id");
			conditions.add("order_confirmation");
			String query = generator.selectquery(dbutil.ORDER_DETAILS, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, userId);
			stmt.setBoolean(2, false);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Cart cart = new Cart();
				cart.setId(rs.getInt("id"));
				cart.setProductId(rs.getInt("product_id"));
				cart.setQuantity(rs.getInt("quantity"));
				cartitems.add(cart);
			}
			columns.removeAll(columns);
			conditions.removeAll(conditions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cartitems;
	}

	public void updateOrderId(int uid, int oid) {
		try {
			Connection con = connection.getDbConnection();
			columns.add("order_id");
			columns.add("order_confirmation");
			conditions.add("user_id");
			conditions.add("order_confirmation");
			String query = generator.updatequery(dbutil.ORDER_DETAILS, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, oid);
			stmt.setBoolean(2, true);
			stmt.setInt(3, uid);
			stmt.setBoolean(4, false);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
