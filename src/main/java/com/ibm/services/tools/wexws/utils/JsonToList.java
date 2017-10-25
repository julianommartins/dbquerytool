package com.ibm.services.tools.wexws.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;



public class JsonToList<T> {
	
	private String json;
	private Class<T> clazz;
	private Map<String,String> fieldMap;

	public JsonToList(String json, Class<T> clazz, Map<String, String> fieldMap) {
		this.json=json;
		this.clazz=clazz;
		this.fieldMap=fieldMap;
		
	}

	public List<T> getList() {
		List<T> records = new ArrayList<T>();
		
		List<String> documents = XMLUtil.getChunks(json, "{ fields: [", "] }",false);
		
		for(String doc : documents){
			T t;
			try {
				t = this.clazz.newInstance();
				
				List<String> jsonfields = XMLUtil.getChunks(doc, "{", "}",false);
				for(String jsonfield: jsonfields){
					String[] f = jsonfield.split(":");
					String fieldName = f[0];
					String fieldValue = f[1];
					
					String classAttribute = fieldMap.get(fieldName);
					if(classAttribute!=null){
						try{
						   java.lang.reflect.Field field = t.getClass().getDeclaredField(classAttribute);
						   field.setAccessible(true);
						   if(field.getType() == String.class){
						      field.set(t, fieldValue);
						   }else if(field.getType() == Integer.class || field.getType() == int.class){
							      field.setInt(t, Integer.parseInt(fieldValue));
						   }else if(field.getType() == Double.class || field.getType() == double.class){
							      field.setDouble(t, Double.parseDouble(fieldValue));
						   }else if(field.getType() == Long.class || field.getType() == long.class){
							      field.setLong(t, Long.parseLong(fieldValue));
						   }else if(field.getType() == Byte.class || field.getType() == byte.class){
							      field.setByte(t, Byte.parseByte(fieldValue));
						   }else if(field.getType() == Short.class || field.getType() == short.class){
							      field.setShort(t, Short.parseShort(fieldValue));
						   }else if(field.getType() == Date.class ){
							      field.set(t, new Date(Long.parseLong(fieldValue)));
						   }	    
						   
						   
						}catch(Exception e){
							e.printStackTrace();
						}
					}

					
				}
				
				records.add(t);
				
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			}
			
			
		}
		
		return records;
	}

}
