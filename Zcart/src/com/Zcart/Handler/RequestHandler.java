//$Id$
package com.Zcart.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import com.Zcart.Utility.DBUtil;
import com.Zcart.exception.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestHandler {
	DBUtil dbutil = new DBUtil();

	public String getRequestType(String url, HttpServletRequest request, HttpServletResponse response,String id,String pid) throws AuthenticationFailedException,DatabaseException {
		String res=null;
		Integer val=0,productid=0;
		if(pid!=null)
			productid=Integer.valueOf(pid);
		if(id!=null)
		 val=Integer.valueOf(id);
		Gson gson = new Gson();
		Object object = null;
		JsonObject params = null,result=null;
		Map<String,Object> m = new HashMap<>();
		System.out.println(url);
		try {
			Class<?> cl = Class.forName("com.Zcart.service." + url + "Service");
			Object classobject = Class.forName("com.Zcart.service." + url + "Service").newInstance();
			Class<?> pojo = Class.forName("com.Zcart.model." + url);
			Object obj = Class.forName("com.Zcart.model." + url).newInstance();
			// System.out.println(obj);
			String method = request.getMethod();
			switch (method) {
				case "GET": {
					params = getRequestParams(request, response, obj);
					object = getPojo(obj, params);
					Method methodcall = cl.getDeclaredMethod("get",Object.class,Integer.class);
					 res=(String) methodcall.invoke(classobject,object,val);
					System.out.println(res);
//					JsonArray jsonarray=gson.fromJson(res, JsonArray.class);
//					for(int i=0;i<jsonarray.size();i++)
//					result.put()
					break;
				}
				case "POST": {
					params = getRequestParams(request, response, obj);
					object = getPojo(obj, params);
					//m = getMap(m, params);
					Method methodcall = cl.getDeclaredMethod("add",Object.class,Integer.class);
					 res=(String)methodcall.invoke(classobject,object,val);
					// for(int i=0;i<methods.length;i++)
					// System.out.println(methods[i]);
					break;
				}
				case "PUT": {
					params = getRequestParams(request, response, obj);
					object = getPojo(obj, params);
					Field[] fields=pojo.getDeclaredFields();
					for(Field f:fields) {				
						f.setAccessible(true);
					//	System.out.println(f.get(object).toString());
						if(f.get(object)!=null) {
							if(f.get(object) instanceof Integer) {
								if(((Integer) f.get(object)).intValue()==0)
									continue;
							}
							if(f.get(object) instanceof Float) {
								if(((Float) f.get(object)).floatValue()==0)
									continue;
							}
							if(f.get(object) instanceof Long) {
								if(((Long) f.get(object)).longValue()==0)
									continue;
							}
						String fieldname=f.getName();
						Object valueobj=f.get(object);
						m.put(fieldname, valueobj);
						}
					}
					Method methodcall = cl.getDeclaredMethod("update", Object.class,Map.class,Integer.class,Integer.class);
					methodcall.invoke(classobject, object,m,productid,val);
					break;
				}
				case "DELETE": {
					params = getRequestParams(request, response, obj);
					object = getPojo(obj, params);
					Method methodcall = cl.getDeclaredMethod("delete",Object.class,Integer.class,Integer.class);
					methodcall.invoke(classobject, object,val,productid);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 return res;
	}

	public JsonObject getRequestParams(HttpServletRequest request, HttpServletResponse response, Object object) {
		Gson gson = new Gson();
		String str = null;
		JsonObject convertedObject = null;
		try {
			BufferedReader reader = request.getReader();
			str = reader.lines().collect(Collectors.joining());
			convertedObject = gson.fromJson(str, JsonObject.class);
			
			// System.out.println(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertedObject;
	}

	public String getTableColumns(Set<Entry<String, JsonElement>> entries) {
		String tablecolumns = dbutil.getTableColumns(entries);
		return tablecolumns;
	}

	public Object getPojo(Object object, JsonObject obj) {
		Gson gson = new Gson();
		// System.out.println(convertedObject);
		object = gson.fromJson(obj, object.getClass());
		return object;
	}

	public Map<String, String[]> getMap(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String[]> m=request.getParameterMap();
	
		return m;
	}
}
