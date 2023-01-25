//$Id$
package com.Zcart.service;

import java.util.Map;

import com.Zcart.Dao.OrderDao;
import com.Zcart.model.Order;
import com.Zcart.model.ReponseDTO;
import com.google.gson.JsonObject;

public class OrderService implements CrudService {
	
	OrderDao orderdao=new OrderDao();

	@Override
	public String add(Object object, Integer uid) throws Exception {
		int userId=uid.intValue();
		Order order=(Order) object;
		return orderdao.placeOrder(order,userId);
	}

	@Override
	public String get(Object object,Integer id) throws Exception {
		// TODO Auto-generated method stub
		int userId=id.intValue();
		return orderdao.getAllOrderDetails(userId);
	}

	@Override
	public ReponseDTO delete(Object object, Integer uid, Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReponseDTO update(Object object, Map<String, Object> map, Integer id, Integer uid) {
		// TODO Auto-generated method stub
		return null;
	}

}
