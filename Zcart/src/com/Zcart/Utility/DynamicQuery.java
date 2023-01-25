//$Id$
package com.Zcart.Utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.google.gson.JsonElement;

public class DynamicQuery {
	public static final String INSERT_QUERY = "insert into ";
	public static final String DELETE_QUERY = "delete from ";
	public static final String UPDATE_QUERY = "Update ";
	public static final String SELECT_QUERY = "select ";
	String query = "";

	public String insertquery(String tablename, ArrayList<String> columns) {
		int i = 0;
		String tablecolumns = String.join(",", columns);
		String placeholders = "";
		for (i = 0; i < columns.size() - 1; i++) {
			placeholders += "?,";
		}
		placeholders += "?";
		query = INSERT_QUERY + tablename + "(" + tablecolumns + ") values(" + placeholders + ")";
		return query;
	}

	public String updatequery(String tablename, ArrayList<String> columns,ArrayList<String> values) {
		String tablecolumns="";
		String conditions="";
		if(columns.size()>0)
		for(int i=0;i<columns.size()-1;i++) {
				tablecolumns+=columns.get(i)+"= ?,";
		}
		tablecolumns+=columns.get(columns.size()-1)+"=?";
		if(values.size()>0) {
		for(int i=0;i<values.size()-1;i++) {
			conditions+=values.get(i)+"=? and ";
		}
		conditions+=values.get(values.size()-1)+" =?";
		}
		if(conditions.isEmpty())
		query=UPDATE_QUERY+" "+tablename+" set "+tablecolumns+" where "+"id=?";
		else {
			query=UPDATE_QUERY+" "+tablename+" set "+tablecolumns+" where "+ conditions;
		}
		return query;
	}

	public String selectquery(String tablename, ArrayList<String> columns, ArrayList<String> values) {
		String conditions = "";
		String tablecolumns = "*";
		if(values.isEmpty())
			return SELECT_QUERY+" *"+" from "+tablename;
		int i = 0;
		if (columns.size() > 0) {
			tablecolumns = String.join(",", columns);
		}
		for (i = 0; i < values.size() - 1; i++) {
			if(values.get(i).equalsIgnoreCase("count(*)")) {
				conditions+=values.get(i);
				break;
			}
			conditions += values.get(i) + "=? and ";
		}
		conditions += values.get(i) + "=?";
		query = SELECT_QUERY + tablecolumns + " from " + tablename + " where " + conditions;
		return query;
	}

	public String deletequery(String tablename, ArrayList<String> values) {
		String conditions = "";
		int i=0;
		for(i=0;i<values.size()-1;i++) {
			conditions+=values.get(i)+"=? and ";
		}
		conditions += values.get(i) + "=?";
		query=DELETE_QUERY+" "+tablename+" where "+conditions;
		return query;
	}
}
