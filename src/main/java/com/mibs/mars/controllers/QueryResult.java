package com.mibs.mars.controllers;

public class QueryResult{
	 private String message;
	 public QueryResult(String message){
		 this.message = message;
	 }
	 public String getMessage(){
		 return this.message;
	 }
}