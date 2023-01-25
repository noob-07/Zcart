//$Id$
package com.Zcart.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.Zcart.model.ReponseDTO;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface CrudService {
		public String add(Object object,Integer id) throws Exception;
		public String get(Object object,Integer id) throws Exception;
		public ReponseDTO delete(Object object,Integer uid,Integer id);
		public ReponseDTO update(Object object,Map<String,Object> map,Integer id,Integer uid) throws SQLException;
}
