//$Id$
package com.Zcart.service;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.Zcart.Dao.ProductDao;
import com.Zcart.Dao.UserDao;
import com.Zcart.Utility.Utility;
import com.Zcart.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserService implements CrudService {
	
	Utility valid=new Utility();
	User user=new User();
	UserDao userdao=new UserDao();
	ProductDao productdao=new ProductDao();
	
//		public ReponseDTO signup(Object object,String columns) {
//			ReponseDTO response=new ReponseDTO("success","added successfully");
//			user=(User) object;
//			if(valid.checkNotNull(userdao.getUser(user.getEmail()))){
//				System.out.println("User already Exists, Try signing in");
//			}
//			else {
//				if(valid.checkNotNull(user.getEmail())) {
//					userdao.add(user,columns);
//					System.out.println("User Added successfully");
//				}
//			}
//			return response;
//		}
	
	public String add(Object object,Integer id) throws SQLException {
		int val=id.intValue();
		JsonObject result=null;
		ReponseDTO response=new ReponseDTO("success","added successfully");
		user=(User) object;
		if(valid.checkNotNull(userdao.getUser(user.getEmail()))){
			System.out.println("User already Exists, Try signing in");
		}
		else {
			if(valid.checkNotNull(user.getEmail())) {
				userdao.add(user);
			}
		}
		return null;
	}
	
		public ReponseDTO signin(Object object) {
			ReponseDTO response=new ReponseDTO("success","added successfully");
			return response;
		}
		@Override
		public String get(Object object,Integer id) {
			List userdetail=new ArrayList<>();
			// TODO Auto-generated method stub
			String products="";
			int userId=id.intValue();
			
			user=(User)userdao.getUserdetails(userId);
			if(user.getEmail().contains("@zoho")) {
				userdetail=productdao.displayStocks();
			}
			else {
				Map row = userdao.getUserAddress(userId);
				row.put("Name", user.getName());
				row.put("Email", user.getEmail());
				row.put("Mobile", user.getMobile());
//				row.put("Address1", user.getAddress1());
//				row.put("Address2", user.getAddress2());
//				row.put("City", user.getCity());
//				row.put("State", user.getState());
//				row.put("Country", user.getCountry());
				userdetail.add(row);
			}
			return new Gson().toJson(userdetail);
		}
		@Override
		public ReponseDTO delete(Object object,Integer uid,Integer cid) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public ReponseDTO update(Object object,Map<String,Object> map,Integer id,Integer uid) throws SQLException {
			// TODO Auto-generated method stub
			user=(User)object;
			int userid=uid.intValue();
			User user2=(User)userdao.getUserdetails(uid);
			if(user.getCurrentPassword()!=null) {
			if(!userdao.verifyUser(user2.getEmail(),user.getCurrentPassword()))
			    throw new SQLException();
			}
			userdao.updateDetails(user,map,userid);
			return null;
		}
		
}
