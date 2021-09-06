package com.myginee.customer.model;

import com.google.gson.annotations.SerializedName;

public class PaymentModel{

	@SerializedName("amount")
	private int amount;

	@SerializedName("amount_paid")
	private int amountPaid;

	@SerializedName("notes")
	private Notes notes;

	@SerializedName("created_at")
	private int createdAt;

	@SerializedName("amount_due")
	private int amountDue;

	@SerializedName("currency")
	private String currency;

	@SerializedName("receipt")
	private String receipt;

	@SerializedName("id")
	private String id;

	@SerializedName("entity")
	private String entity;

	@SerializedName("offer_id")
	private String offerId;

	@SerializedName("status")
	private String status;

	@SerializedName("attempts")
	private int attempts;

	public void setAmount(int amount){
		this.amount = amount;
	}

	public int getAmount(){
		return amount;
	}

	public void setAmountPaid(int amountPaid){
		this.amountPaid = amountPaid;
	}

	public int getAmountPaid(){
		return amountPaid;
	}

	public void setNotes(Notes notes){
		this.notes = notes;
	}

	public Notes getNotes(){
		return notes;
	}

	public void setCreatedAt(int createdAt){
		this.createdAt = createdAt;
	}

	public int getCreatedAt(){
		return createdAt;
	}

	public void setAmountDue(int amountDue){
		this.amountDue = amountDue;
	}

	public int getAmountDue(){
		return amountDue;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setReceipt(String receipt){
		this.receipt = receipt;
	}

	public String getReceipt(){
		return receipt;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setEntity(String entity){
		this.entity = entity;
	}

	public String getEntity(){
		return entity;
	}

	public void setOfferId(String offerId){
		this.offerId = offerId;
	}

	public String getOfferId(){
		return offerId;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setAttempts(int attempts){
		this.attempts = attempts;
	}

	public int getAttempts(){
		return attempts;
	}
}