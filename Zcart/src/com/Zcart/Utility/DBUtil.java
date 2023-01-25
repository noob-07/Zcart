//$Id$
package com.Zcart.Utility;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;



public class DBUtil {
	public static final String USER = "users";
	public static final String ADDRESS = "address";
	public static final String PASSWORD = "Passwords";
	public static final String USERROLE = "user_role";
	public static final String BRAND="brands";
	public static final String CATEGORY="category";
	public static final String SIZE="size";
	public static final String COLOUR="colour";
	public static final String PRODUCT="Product";
	public static final String ORDER_DETAILS="order_details";
	public static final String ORDERS="orders";
	public static final String DISCOUNT = "discount";
	StringBuilder str=new StringBuilder();
	
	public String getTableColumns(Set<Entry<String, JsonElement>> entries) {
	      for(Map.Entry<String, JsonElement> entry: entries) {
	    	  str.append(entry.getKey());
	    	  str.append(",");
	      }
		System.out.println(str);
	return str.toString();
		
	}
}
