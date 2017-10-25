package com.ibm.services.tools.wexws.dao;

import java.sql.ResultSet;

public interface AccessLayerDAO { // WexRestfulDAO implements it
	ResultSet executeQueryDB(String sql) throws Exception;
}
