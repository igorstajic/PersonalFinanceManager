package com.personalfinancemanager.model;

import java.util.HashMap;
import java.util.UUID;

public class Group {

	private String id;
	private String name;
	private HashMap<String, Transaction> transactions = new HashMap<String, Transaction>();
	
	public Group() {
		
	}
	
	public Group(String name) {
		super();
		this.name = name;
		this.id = UUID.randomUUID().toString();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Integer calculateBalance() {
		int balance = 0;
		for (Transaction t : transactions.values()){
			balance += t.getAmount();
		}
		return balance;
	}


	public HashMap<String, Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(HashMap<String, Transaction> transactions) {
		this.transactions = transactions;
	}
	
	
}
