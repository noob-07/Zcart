//$Id$
package com.Zcart.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Zcart.Utility.ConnectionUtil;
import com.Zcart.Utility.DBUtil;
import com.Zcart.Utility.DynamicQuery;
import com.Zcart.Utility.Utility;
import com.Zcart.model.Product;
import com.google.gson.Gson;

public class ProductDao {
	DBUtil dbutil = new DBUtil();
	ConnectionUtil connection = new ConnectionUtil();
	Utility utility = new Utility();
	DynamicQuery generator = new DynamicQuery();
	ArrayList<String> columns = new ArrayList<>();
	ArrayList<String> conditions = new ArrayList<>();
	String query = null;
	int i = 0;

	public String getAllProduct() {
		List listofproducts = new ArrayList<>();
		try {
			Connection con = connection.getDbConnection();
			String query = generator.selectquery(dbutil.PRODUCT, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int numColumns = metaData.getColumnCount();
			while (rs.next()) {
				Map row = new HashMap();
				for (int i = 1; i < numColumns + 1; i++) {
					row.put(metaData.getColumnName(i), rs.getObject(i));
				}
				int brand_id = (int) row.get("brand_id");
				String brandName = gettablecontent(dbutil.BRAND, "brandname", brand_id);
				row.remove("brand_id");
				row.put("brand", brandName);
				int colour_id = (int) row.get("colour_id");
				String colour = gettablecontent(dbutil.COLOUR, "colour", colour_id);
				row.remove("colour_id");
				row.put("colour", colour);
				int category_id = (int) row.get("category_id");
				String category = gettablecontent(dbutil.CATEGORY, "category", category_id);
				row.remove("category_id");
				row.put("category", category);
				int size_id = (int) row.get("size_id");
				String size = gettablecontent(dbutil.SIZE, "size", size_id);
				row.remove("size_id");
				row.put("size", size);
				listofproducts.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String json = new Gson().toJson(listofproducts);
		return json;
	}
	
	public Map getProducts(int pid, int quantity) {
		List listofproducts = new ArrayList<>();
		try {
			Connection con = connection.getDbConnection();
			conditions.add("id");
			String query = generator.selectquery(dbutil.PRODUCT, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			conditions.removeAll(conditions);
			stmt.setInt(1, pid);
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int numColumns = metaData.getColumnCount();
			while (rs.next()) {
				Map row = new HashMap();
				for (int i = 1; i < numColumns + 1; i++) {
					row.put(metaData.getColumnName(i), rs.getObject(i));
				}
				int brand_id = (int) row.get("brand_id");
				String brandName = gettablecontent(dbutil.BRAND, "brandname", brand_id);
				row.remove("brand_id");
				row.put("brand", brandName);
				int colour_id = (int) row.get("colour_id");
				String colour = gettablecontent(dbutil.COLOUR, "colour", colour_id);
				row.remove("colour_id");
				row.put("colour", colour);
				int category_id = (int) row.get("category_id");
				String category = gettablecontent(dbutil.CATEGORY, "category", category_id);
				row.remove("category_id");
				row.put("category", category);
				int size_id = (int) row.get("size_id");
				String size = gettablecontent(dbutil.SIZE, "size", size_id);
				row.remove("size_id");
				row.put("size", size);
				if(quantity>0)
					row.put("quantity",quantity);
				return row;
				//listofproducts.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String json = new Gson().toJson(listofproducts);
		return null;
		
	}

	public void updateProduct(Product product, Map<String, Object> map, int pid, int uid) {
		ArrayList<Object> values = new ArrayList<>();
		int i = 0;
		try {
			Connection con = connection.getDbConnection();
			columns.removeAll(columns);
			conditions.removeAll(conditions);
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if (!entry.getKey().equalsIgnoreCase("Brand") || !entry.getKey().equalsIgnoreCase("Colour") || !entry.getKey().equalsIgnoreCase("Size")) {
					columns.add(entry.getKey());
					values.add(entry.getValue());
				}
			}
			columns.add("modified_at");
			columns.add("modified_by");
			String query = generator.updatequery(dbutil.PRODUCT, columns, conditions);
			columns.removeAll(columns);
			PreparedStatement stmt = con.prepareStatement(query);
			for (i = 0; i < values.size(); i++) {
				if (values.get(i) instanceof String) {
					stmt.setString(i + 1, values.get(i).toString());
				} else if (values.get(i) instanceof Float) {
					stmt.setFloat(i + 1, (float) values.get(i));
				} else if (values.get(i) instanceof Integer) {
					stmt.setInt(i + 1, (int) values.get(i));
				}
			}
			String name = getUserName(uid);
			stmt.setLong(++i, utility.CURRENT_TIME);
			stmt.setString(++i, name);
			stmt.setInt(++i, pid);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean verifyProduct(String model, String brand) {
		int affectedrows = 0;
		try {
			Connection con = connection.getDbConnection();
			conditions.add("Model");
			String query = generator.selectquery(dbutil.PRODUCT, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, model);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				affectedrows = rs.getInt("id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		conditions.removeAll(conditions);
		if (affectedrows > 0)
			return true;
		return false;
	}

	public boolean verifyContent(String tableName, String param, String condition) {
		int result = 0;
		try {
			Connection con = connection.getDbConnection();
			columns.add("id");
			conditions.add(condition);
			String query = generator.selectquery(tableName, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, param);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getInt("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		columns.removeAll(columns);
		conditions.removeAll(conditions);
		if (result > 0)
			return true;
		return false;
	}

	public int add(Product product, int id) {
		int affectedrows = 0;
		try {
			Connection con = connection.getDbConnection();
			int brandId = addBrand(product.getBrand());
			int colourId = addColour(product.getColour());
			int sizeId = addSize(product.getSize());
			int categoryId = getCategoryId(product.getCategory());
			String createdBy = getUserName(id);
			String modifiedBy = getUserName(id);
			columns.add("Model");
			columns.add("Quantity");
			columns.add("Price");
			columns.add("category_id");
			columns.add("brand_id");
			columns.add("size_id");
			columns.add("colour_id");
			columns.add("Created_at");
			columns.add("Modified_at");
			columns.add("Created_by");
			columns.add("Modified_by");
			String query = generator.insertquery(dbutil.PRODUCT, columns);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, product.getModel());
			stmt.setInt(2, product.getQuantity());
			stmt.setFloat(3, product.getPrice());
			stmt.setInt(4, categoryId);
			stmt.setInt(5, brandId);
			stmt.setInt(6, sizeId);
			stmt.setInt(7, colourId);
			stmt.setLong(8, utility.CURRENT_TIME);
			stmt.setLong(9, utility.CURRENT_TIME);
			stmt.setString(10, createdBy);
			stmt.setString(11, modifiedBy);
			int rows = stmt.executeUpdate();
			System.out.println("Added product successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
		columns.removeAll(columns);
		return affectedrows;
	}

	private int getCategoryId(String category) {
		int id = 0;
		if (category.equalsIgnoreCase("Laptop")) {
			id = 1;
		} else if (category.equalsIgnoreCase("Mobile")) {
			id = 2;
		} else {
			id = 3;
		}
		return id;
	}

	public int addBrand(String brand) {
		try {
			Connection con = connection.getDbConnection();
			if (!verifyContent(dbutil.BRAND, brand, "brandname")) {
				columns.add("BrandName");
				String query = generator.insertquery(dbutil.BRAND, columns);
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setString(1, brand);
				int affectedrows = stmt.executeUpdate();
				columns.removeAll(columns);
			} else {
				System.out.println("Brand already exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getPkId(dbutil.BRAND, brand, "brandname");
	}

	public int addColour(String colour) {
		try {
			Connection con = connection.getDbConnection();
			if (!verifyContent(dbutil.COLOUR, colour, "colour")) {
				columns.add("colour");
				String query = generator.insertquery(dbutil.COLOUR, columns);
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setString(1, colour);
				int affectedrows = stmt.executeUpdate();
				columns.removeAll(columns);
			} else {
				System.out.println("Colour already exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getPkId(dbutil.COLOUR, colour, "colour");
	}

	public int addSize(String size) {
		try {
			Connection con = connection.getDbConnection();
			if (!verifyContent(dbutil.SIZE, size, "size")) {
				columns.add("size");
				String query = generator.insertquery(dbutil.SIZE, columns);
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setString(1, size);
				int affectedrows = stmt.executeUpdate();
				columns.removeAll(columns);
			} else {
				System.out.println("Size already exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getPkId(dbutil.SIZE, size, "size");
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

	public String getUserName(int userId) {
		String result = "";
		conditions.removeAll(conditions);
		try {
			Connection con = connection.getDbConnection();
			columns.add("Name");
			conditions.add("id");
			String query = generator.selectquery(dbutil.USER, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getString("Name");
			}

		} catch (Exception e) {

		}
		columns.removeAll(columns);
		conditions.removeAll(conditions);
		return result;
	}

	public String gettablecontent(String tablename, String param, int id) {
		String result = "";
		try {
			columns.removeAll(columns);
			Connection con = connection.getDbConnection();
			columns.add(param);
			conditions.add("id");
			String query = generator.selectquery(tablename, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getString(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		columns.removeAll(columns);
		conditions.removeAll(conditions);
		return result;
	}
	
	public float getPrice(int pid) {
		float result=0;
		try {
			Connection con = connection.getDbConnection();
			columns.add("price");
			conditions.add("id");
			String query = generator.selectquery(dbutil.PRODUCT, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, pid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getFloat("price");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
		
	}

	public List displayStocks() {
		List listofproducts = new ArrayList<>();
		try {
			int threshold=10;
			Connection con = connection.getDbConnection();
			conditions.add("Quantity<");
			String query = generator.selectquery(dbutil.PRODUCT, columns, conditions);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1,threshold);
			conditions.removeAll(conditions);
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int numColumns = metaData.getColumnCount();
			while (rs.next()) {
				Map row = new HashMap();
				for (int i = 1; i < numColumns + 1; i++) {
					row.put(metaData.getColumnName(i), rs.getObject(i));
				}
				int brand_id = (int) row.get("brand_id");
				String brandName = gettablecontent(dbutil.BRAND, "brandname", brand_id);
				row.remove("brand_id");
				row.put("brand", brandName);
				int colour_id = (int) row.get("colour_id");
				String colour = gettablecontent(dbutil.COLOUR, "colour", colour_id);
				row.remove("colour_id");
				row.put("colour", colour);
				int category_id = (int) row.get("category_id");
				String category = gettablecontent(dbutil.CATEGORY, "category", category_id);
				row.remove("category_id");
				row.put("category", category);
				int size_id = (int) row.get("size_id");
				String size = gettablecontent(dbutil.SIZE, "size", size_id);
				row.remove("size_id");
				row.put("size", size);
				listofproducts.add(row);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return listofproducts;
	}

	public void updateStocks(Product product,int pid, int userid) {
		List listofproducts = new ArrayList<>();
		try {
			int threshold=10;
			int quantity=getQuantity(pid)+product.getQuantity();
			String name = getUserName(userid);
			Connection con = connection.getDbConnection();
			columns.add("Quantity");
			columns.add("modified_at");
			columns.add("modified_by");		
			conditions.add("id");
			String query = generator.updatequery(dbutil.PRODUCT, columns, conditions);
			System.out.println(query);
			PreparedStatement stmt = con.prepareStatement(query);
			columns.removeAll(columns);
			conditions.removeAll(conditions);
			stmt.setInt(1,quantity);
			stmt.setLong(2,utility.CURRENT_TIME);
			stmt.setString(3, name);
			stmt.setInt(4, pid);
			stmt.executeUpdate();
			System.out.println("updated stocks");
	}catch(Exception e) {
		e.printStackTrace();
	}
	}
		
		public int getQuantity(int pid) {
			int result=0;
			try {
				columns.add("Quantity");
				conditions.add("id");
				Connection con = connection.getDbConnection();
				String query = generator.selectquery(dbutil.PRODUCT, columns, conditions);
				System.out.println(query);
				PreparedStatement stmt = con.prepareStatement(query);
				columns.removeAll(columns);
				conditions.removeAll(conditions);
				stmt.setInt(1,pid);
				ResultSet rs=stmt.executeQuery();
				while(rs.next()) {
					result=rs.getInt("Quantity");
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return result;
		}
}

