package com.ibm.services.tools.wexws.domain;

public class SQLResponse {
	
	private String sql;
	private String response;
	private String error;
	
	public String getWql() {
		return sql;
	}
	public void setWql(String sql) {
		this.sql = sql;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

}
