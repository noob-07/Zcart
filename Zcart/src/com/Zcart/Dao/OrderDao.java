//$Id$
package com.Zcart.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;	
import com.Zcart.Utility.ConnectionUtil;
import com.Zcart.Utility.DBUtil;
import com.Zcart.Utility.DynamicQuery;
import com.Zcart.Utility.Utility;
import com.Zcart.model.Cart;
import com.Zcart.model.Order;
import com.google.gson.Gson;

public class OrderDao {
	DBUtil dbutil = new DBUtil();
	ConnectionUtil connection = new ConnectionUtil();
	Utility utility = new Utility();
	DynamicQuery generator = new DynamicQuery();
	ArrayList<String> columns = new ArrayList<>();
	ArrayList<String> conditions = new ArrayList<>();
	String query = null;
	int i = 0;
	CartDao cartdao = new CartDao();
	ProductDao productdao=new ProductDao();

	public String placeOrder(Order order, int userId) {
		List<Cart> cartitems = cartdao.getCartItems(userId);
		int count=0,discount_amount=0,percentage=0,discount_percent=0,orderId=0;
		if (cartitems.size() == 0)
			System.out.println("Cart is empty,add products to cart");
		else {
			try {
				float total_amount = 0;
				
				int finalamount=0;
				String  coupon="";
				Connection con = connection.getDbConnection();
				for (int i = 0; i < cartitems.size(); i++) {
					int product_id = cartitems.get(i).getProductId();
					int product_price=(int)productdao.getPrice(product_id);
					int quantity = cartitems.get(i).getQuantity();
					if(quantity==0)
						quantity=1;
					total_amount += getPrice(product_price, quantity);
				}
				boolean flag=checkDiscountEligiblity(total_amount,userId);
				if(flag) {
					 coupon=generateCoupon();
					 percentage=calculateDiscountPercent(total_amount);
				}
				if(order.getDiscount_code()!=null) {
					discount_percent=getDiscountPercent(order.getDiscount_code(),userId);
					if(discount_percent>1) {
						discount_amount=(int) (total_amount*(((float)discount_percent)/100));
						columns.add("discount_amount");
						count=6;
					}
				}
				finalamount=(int)(total_amount-discount_amount);
				columns.add("total_amount");
				columns.add("user_id");
				columns.add("invoice_number");
				columns.add("order_date");
				columns.add("delivery_date");
				columns.add("amountpayable");
				String query = generator.insertquery(dbutil.ORDERS, columns);
				PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				if(count==6) {
					stmt.setInt(1,discount_amount);
					stmt.setFloat(2, total_amount);
					stmt.setInt(3, userId);
					stmt.setString(4, generateInvoiceNumber());
					stmt.setLong(5, utility.CURRENT_TIME);
					stmt.setLong(6, utility.CURRENT_TIME);
					stmt.setLong(7,finalamount);
				}else {
				stmt.setFloat(1, total_amount);
				stmt.setInt(2, userId);
				stmt.setString(3, generateInvoiceNumber());
				stmt.setLong(4, utility.CURRENT_TIME);
				stmt.setLong(5, utility.CURRENT_TIME);
				stmt.setLong(6,finalamount);
				}
				stmt.executeUpdate();
				ResultSet rs = stmt.getGeneratedKeys();
				while (rs.next()) {
					orderId=rs.getInt("id");
					cartdao.updateOrderId(userId, rs.getInt("id"));
				}
				columns.removeAll(columns);
				conditions.removeAll(conditions);
				if(flag)
					addDiscount(coupon,percentage,userId,orderId);
				columns.removeAll(columns);
				conditions.removeAll(conditions);
				updateOrderCount(userId,orderId);
				System.out.println("Order placed successfully");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return getOrderDetails(orderId,userId);
	}
	
	public String getOrderDetails(int id,int userId) {
		List orderdetail=new ArrayList<>();
		try {
			Connection con = connection.getDbConnection();
			conditions.add("id");
			conditions.add("user_id");
			String query = generator.selectquery(dbutil.ORDERS, columns,conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			conditions.removeAll(conditions);
			stmt.setInt(1, id);
			stmt.setInt(2, userId);
			ResultSet rs=stmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int numColumns = metaData.getColumnCount();
			while(rs.next()) {
				Map row = new HashMap();
				for (int i = 1; i < numColumns + 1; i++) {
					row.put(metaData.getColumnName(i), rs.getObject(i));
				}
				orderdetail.add(row);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	
		return new Gson().toJson(orderdetail);
	}
	
	public String getAllOrderDetails(int userId) {
		List orderdetail=new ArrayList<>();
		int orderId=0;
		try {
			Connection con = connection.getDbConnection();
			conditions.add("user_id");
			String query = generator.selectquery(dbutil.ORDERS, columns,conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			conditions.removeAll(conditions);
			stmt.setInt(1, userId);
			ResultSet rs=stmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			while(rs.next()) {
				orderId=rs.getInt("id");
				int numColumns = metaData.getColumnCount();
				Map row = new HashMap();
				row=getProductDetail(orderId,row);
				for (int i = 1; i < numColumns + 1; i++) {
					row.put(metaData.getColumnName(i), rs.getObject(i));
				}
				orderdetail.add(row);	
				
				}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new Gson().toJson(orderdetail);
	}
	

	private Map getProductDetail(int orderId, Map row2) {
		List productdetail=new ArrayList<>();
		//Map row=new HashMap<>();
		int productId=0;
		try {
			Connection con = connection.getDbConnection();
			conditions.add("order_id");
			String query = generator.selectquery(dbutil.ORDER_DETAILS, columns,conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			conditions.removeAll(conditions);
			stmt.setInt(1, orderId);
			ResultSet rs=stmt.executeQuery();
			while(rs.next()) {
			 productId=rs.getInt("product_id");
			}
			 row2=productdao.getProducts(productId, productId);
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return row2;
	}

	public void addDiscount(String coupon, int percentage, int userId, int orderId) {
          try {
        	Connection con = connection.getDbConnection();
  			columns.add("coupon_code");
  			columns.add("percentage");
  			columns.add("user_id");
  			columns.add("isvalid");
  			columns.add("Created_At");
  			columns.add("order_id");
  			columns.add("count_orders");
  			String query = generator.insertquery(dbutil.DISCOUNT,columns);
  			PreparedStatement stmt = con.prepareStatement(query);
  			stmt.setString(1,coupon);
  			stmt.setInt(2, percentage);
  			stmt.setInt(3, userId);
  			stmt.setBoolean(4,true);
  			stmt.setLong(5,utility.CURRENT_TIME);
  			stmt.setInt(6, orderId);
  			stmt.setInt(7, 1);
  			stmt.executeUpdate();
  			columns.removeAll(columns);
          }catch(Exception e) {
        	  e.printStackTrace();
          }
	}

	private void updateOrderCount(int user_id,int orderId) {
		ArrayList<String> coupon_codes=new ArrayList<>();
		HashMap<String,Integer> map=new HashMap<>();
		try {
			columns.removeAll(columns);
			conditions.removeAll(conditions);
      	  Connection con = connection.getDbConnection();
			map=getDiscountCounts(user_id);
			  columns.add("count_orders");
	      	  conditions.add("user_id");
	      	  conditions.add("coupon_code");
			String query = generator.updatequery(dbutil.DISCOUNT,columns,conditions);
			query=query+" and order_id!=?";
			PreparedStatement stmt = con.prepareStatement(query);
			for(Map.Entry<String, Integer> entry:map.entrySet()) {
				int count=entry.getValue();
				stmt.setInt(1,(int)count+1);
				stmt.setInt(2, user_id);
				stmt.setString(3,entry.getKey());
				stmt.setInt(4, orderId);
				stmt.executeUpdate();
				if(entry.getValue()>3)
					coupon_codes.add(entry.getKey());
			}
			columns.removeAll(columns);
			conditions.removeAll(conditions);
			if(!coupon_codes.isEmpty())
			updateDiscountStatus(coupon_codes);
        }catch(Exception e) {
      	  e.printStackTrace();
        }
	}
	
	private HashMap<String, Integer> getDiscountCounts(int user_id) {
		  HashMap<String,Integer> m=new HashMap<>();
		  columns.add("count_orders");
		  columns.add("coupon_code");
    	  conditions.add("user_id");
		try {
			Connection con = connection.getDbConnection();
			String query = generator.selectquery(dbutil.DISCOUNT,columns,conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			columns.removeAll(columns);
			conditions.removeAll(conditions);
			stmt.setInt(1, user_id);
			ResultSet rs=stmt.executeQuery();
			while(rs.next()) {
				m.put(rs.getString("coupon_code"), rs.getInt("count_orders"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return m;
	}


	private void updateDiscountStatus(ArrayList<String> coupon_codes) {
		try {
		Connection con = connection.getDbConnection();
		columns.add("isvalid");
		conditions.add("coupon_code");
		query=generator.updatequery(dbutil.DISCOUNT,columns,conditions);
		columns.removeAll(columns);
		conditions.removeAll(conditions);
		PreparedStatement stmt = con.prepareStatement(query);
		for(int i=0;i<coupon_codes.size();i++) {
			stmt.setBoolean(1,true);
			stmt.setString(2,coupon_codes.get(i));
			stmt.executeUpdate();
		}
		}catch(Exception e) {
			e.printStackTrace();
		}	

	}

	private int getDiscountPercent(String discount_code, int userId) {
		int result=0;
		try {
			columns.removeAll(columns);
			conditions.removeAll(conditions);
			Connection con = connection.getDbConnection();
			conditions.add("coupon_code");
			conditions.add("user_id");
			conditions.add("isvalid");
			String query = generator.selectquery(dbutil.DISCOUNT,columns,conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1,discount_code);
			stmt.setInt(2, userId);
			stmt.setBoolean(3,true);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				result=rs.getInt("percentage");
			}
			columns.removeAll(columns);
			conditions.removeAll(conditions);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private int calculateDiscountPercent(float total_amount) {
	 return ThreadLocalRandom.current().nextInt(20,31);
	  
	}
	
	private String generateCoupon() {
	    int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 6;
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(6);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int)(random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    return buffer.toString();
	}

	private float getPrice(int product_price, int quantity) {
		return product_price * quantity;
	}

	public int getPkId(String tablename, String param, String condition) {
		int result = 0;
		try {
			Connection con = connection.getDbConnection();
			columns.add("id");
			conditions.add(condition);
			String query = generator.selectquery(tablename, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, param);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getInt("id");
			}
			columns.removeAll(columns);
			conditions.removeAll(conditions);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String generateInvoiceNumber() {
		String invoice_no = "ZC";
		Random rd = new Random();
		int numbers = (int) (100000 +(rd.nextFloat() * 900000));
		invoice_no += numbers + "A";
		return invoice_no;
	}
	
	public boolean checkDiscountEligiblity(float amount,int userid) {
		int result=0;
		try {
		Connection con = connection.getDbConnection();
		columns.add("count(*)");
		conditions.add("user_id");
		String query=generator.selectquery(dbutil.ORDERS, columns, conditions);
		columns.removeAll(columns);
		conditions.removeAll(conditions);
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setInt(1, userid);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			result = rs.getInt(1);
		}
		if(amount>20000||result==3) {
			return true;
		}
		}catch(Exception e) {
		
	}
		return false;
}
}
