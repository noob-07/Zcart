//$Id$
package com.Zcart.Utility;

import java.time.Instant;
import java.util.*;

public class Utility {

	
public static final	long CURRENT_TIME = Instant.now().toEpochMilli();
enum mapurl{
   signin,
   signup
}
	public String regex="[0-9]+";
	
public String getClassName(String url) {
	return url.substring(0, 1).toUpperCase() + url.substring(1);
}
	
		public boolean checkNotNull(Object object) {
		
			if(object instanceof String) {
			return object!=null && !object.toString().isEmpty();
			}
			return object!=null;
		} 
		
	
}
