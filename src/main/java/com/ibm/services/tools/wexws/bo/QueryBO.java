package com.ibm.services.tools.wexws.bo;

import java.sql.ResultSet;

import com.ibm.services.tools.wexws.dao.AccessLayerDAO;

public class QueryBO {

	private AccessLayerDAO wexDAO;
	
	public QueryBO(AccessLayerDAO wexRestDAO){
		this.wexDAO = wexRestDAO;
	}
	
	/**
	 * Query Database
	 * @param wqlStatement
	 * @return
	 * @throws Exception 
	 */
	public ResultSet executeQueryDB(String wqlStatement) throws Exception {
		ResultSet response = null;
		response = wexDAO.executeQueryDB(wqlStatement);
		return response;
	}

}
