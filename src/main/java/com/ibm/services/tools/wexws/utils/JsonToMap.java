package com.ibm.services.tools.wexws.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonToMap {
	
	private String json;

	public JsonToMap(String json) {
		this.json=json;
	}

	public List<Map<String,String>> getMap() {
	   List<Map<String,String>> records = new ArrayList<Map<String,String>>();
		
		List<String> documents = XMLUtil.getChunks(json, "{ fields: [", "] }",false);
		
		for(String doc : documents){
			Map<String,String> map = new HashMap<String,String>();
			
			List<String> jsonfields = XMLUtil.getChunks(doc, "{", "}",false);
			for(String jsonfield: jsonfields){
				String[] f = jsonfield.split(":");
				String fieldName = f[0];
				String fieldValue = f[1];

				map.put(fieldName, fieldValue);
			}
			
			records.add(map);
			
		}
		
		return records;
	}

}
