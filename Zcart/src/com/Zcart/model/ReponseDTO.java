//$Id$
package com.Zcart.model;

public class ReponseDTO {
	private String status;
	private String message;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public ReponseDTO(String status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	
	
}
