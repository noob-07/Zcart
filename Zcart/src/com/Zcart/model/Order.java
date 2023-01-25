//$Id$
package com.Zcart.model;

public class Order {
	private int product_id;
	private float total_amount;
	private String discount_code;
	private int Quantity;
	
	public int getQuantity() {
		return Quantity;
	}
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	public float getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(float total_amount) {
		this.total_amount = total_amount;
	}
	public String getDiscount_code() {
		return discount_code;
	}
	public void setDiscount_code(String discount_code) {
		this.discount_code = discount_code;
	}
}
