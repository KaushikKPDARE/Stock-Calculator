package com.models;

public class Holding {

	private String stock;
	private double amount;
	
	public String getHoldingStock(){
		return stock;
	}
	
	public void setHoldingStock(String stock){
		this.stock = stock;
	}
	
	public double getHoldingAmount(){
		return amount;
	}
	
	public void setHoldingAmount(double amount){
		this.amount = amount;
	}
	
}
