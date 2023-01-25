//$Id$
package com.Zcart.service;

import java.util.List;
import java.util.Map;

import com.Zcart.Dao.ProductDao;
import com.Zcart.Utility.Utility;
import com.Zcart.exception.CustomException;
import com.Zcart.model.Product;
import com.Zcart.model.ReponseDTO;
import com.google.gson.JsonObject;

public class ProductService implements CrudService{
	
	
	Utility utility=new Utility();
	ProductDao productdao=new ProductDao();

	@Override
	public String add(Object object,Integer id) throws Exception {
		int val=id.intValue();
		// TODO Auto-generated method stub
		Product product=(Product)object;
		if(productdao.verifyProduct(product.getModel(),product.getBrand())) {
			throw new CustomException("Product already Exists");
		}
		else {
			productdao.add(product,val);
		}
		return null;
	}

	@Override
	public String get(Object object,Integer id) throws Exception {
		// TODO Auto-generated method stub
		Product product=(Product)object;
		return productdao.getAllProduct();
	}

	@Override
	public ReponseDTO delete(Object object,Integer uid,Integer cid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReponseDTO update(Object object,Map<String,Object> map,Integer id,Integer uid) {
		// TODO Auto-generated method stub
		int pid=id.intValue();
		int userid=uid.intValue();
		Product product=(Product)object;
		if(utility.checkNotNull(product.getQuantity())) {
			productdao.updateStocks(product,pid,userid);
		}
		else {
		productdao.updateProduct(product,map,pid,userid);
		}
		return null;
	}
	
}
