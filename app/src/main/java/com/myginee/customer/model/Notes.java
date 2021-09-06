package com.myginee.customer.model;

import com.google.gson.annotations.SerializedName;

public class Notes{

	@SerializedName("notes_key_1")
	private String notesKey1;

	@SerializedName("notes_key_2")
	private String notesKey2;

	public void setNotesKey1(String notesKey1){
		this.notesKey1 = notesKey1;
	}

	public String getNotesKey1(){
		return notesKey1;
	}

	public void setNotesKey2(String notesKey2){
		this.notesKey2 = notesKey2;
	}

	public String getNotesKey2(){
		return notesKey2;
	}
}