package com.models;

public class Ord {

	private String stock;
	private char transactionType;
	private double amount;
	
	public String getOrdStock(){
		return stock;
	}
	
	public void setOrdStock(String stock){
		this.stock = stock;
	}
	
	public char getOrdTransaction(){
		return transactionType;
	}
	
	public void setOrdTransaction(char transactionType){
		this.transactionType = transactionType;
	}
	
	public double getOrdAmount(){
		return amount;
	}
	
	public void setOrdAmount(double amount){
		this.amount = amount;
	}
	
}
