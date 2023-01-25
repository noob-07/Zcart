//$Id$
package com.Zcart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.Zcart.Dao.CartDao;
import com.Zcart.Dao.ProductDao;
import com.Zcart.model.Cart;
import com.Zcart.model.ReponseDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CartService implements CrudService {

	CartDao cartdao=new CartDao();
	ProductDao productdao=new ProductDao();
	Cart cart=null;
	
	@Override
	public String add(Object object, Integer id) throws Exception {
		int userid=id.intValue();
		cart=(Cart)object;
		cartdao.addToCart(cart, userid);
		return null;
	}

	@Override
	public String get(Object object,Integer id) throws Exception {
		// TODO Auto-generated method stub
		List productdetails=new ArrayList<>();
		int userId=(int)id;
		Cart cart=(Cart)object;
		List<Cart> list=cartdao.getCartItems(id);
		for(Cart c:list) {
		 productdetails.add(productdao.getProducts(c.getProductId(),c.getQuantity()));

		}
	    String cartitems=new Gson().toJson(productdetails);
		return cartitems;
	}

	@Override
	public ReponseDTO delete(Object object,Integer uid,Integer cid) {
		System.out.println(cid);
		// TODO Auto-generated method stub
		cart=(Cart)object;
		int userId=uid.intValue();
		int cartId=cid.intValue();
		cartdao.deleteFromCart(cart, userId);
		return null;
	}

	@Override
	public ReponseDTO update(Object object, Map<String, Object> map, Integer id, Integer uid) {
		// TODO Auto-generated method stub
		return null;
	}

}
