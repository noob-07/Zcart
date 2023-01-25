//$Id$
package com.Zcart.service;

import java.sql.SQLException;
import java.util.Map;

import com.Zcart.Dao.UserDao;
import com.Zcart.Utility.Utility;
import com.Zcart.exception.*;
import com.Zcart.model.ReponseDTO;
import com.Zcart.model.Signin;
import com.Zcart.model.User;
import com.google.gson.JsonObject;

public class SigninService implements CrudService {
	
	UserDao userdao=new UserDao();
	Utility utility=new Utility();
	
	@Override
	public String add(Object object,Integer id) throws AuthenticationFailedException, DatabaseException{
		Signin signin=(Signin)object;
		String res="";
		JsonObject obj=null;
		int user_id=0;
		try {
			if(utility.checkNotNull(userdao.getUser(signin.getEmail()))) {
			boolean result=userdao.verifyUser(signin.getEmail(),signin.getPassword());
			    if(result) {
			    	res=userdao.getUserId(signin.getEmail());
			    	System.out.println("Successfully logged in");
			    }
			    else {
			    	throw new AuthenticationFailedException("Wrong password, user Siginin Failed");
			    }
			}
			else {
				throw new AuthenticationFailedException("User does not exists in DB");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DatabaseException("Couldn't connect with the DB");
		} catch (AuthenticationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public String get(Object object,Integer id) throws SQLException {
		
		return null;
	}

	@Override
	public ReponseDTO delete(Object object,Integer uid,Integer cid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReponseDTO update(Object object,Map<String,Object> map,Integer id,Integer uid) {
		// TODO Auto-generated method stub
		return null;
	}

}
