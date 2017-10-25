package com.ibm.services.tools.wexws.utils;

import java.util.ArrayList;
import java.util.List;

public class XMLUtil {

	private static String getChunk(String query, String keyword1, String keyword2) {
		String chunk="";
		
		int idx1 = -1;
		int idx2 = -1;
		
		idx1 = query.indexOf(keyword1);
		if(idx1>-1){
		   idx2 = query.indexOf(keyword2,idx1+keyword1.length());
		}
		
		if(idx1>-1 && idx1<idx2){
			chunk=query.substring(idx1+keyword1.length(),idx2).trim();
		}

		
		return chunk;
	}

	private static String getChunk(String query, String tag1, String tag2, int startIndex) {
		String chunk="";
		
		int idx1 = -1;
		int idx2 = -1;
		
		idx1 = startIndex;
		if(idx1>-1){
		   idx2 = query.indexOf(tag2,idx1+tag1.length());
		}
		
		if(idx1>-1 && idx1<idx2){
			chunk=query.substring(idx1,idx2+tag2.length()).trim();
		}
		
		return chunk;
	}
	
	
	public static String getChunk(String query, String keyword1, String... keywords) {
		String chunk="";
		for(String keyword2 : keywords){
			String test = getChunk(query, keyword1, keyword2);
			if(test!=null && test.length()>0){
				chunk=test;
				break;
			}
		}
		
		return chunk;
	}

	public static List<String> getChunks(String xml, String tag1, String tag2, boolean includeDelimiters) {
		List<String> chunks = new ArrayList<String>();
		
		int idx=0;
		while((idx=xml.indexOf(tag1,idx))>-1){
			String chunk = getChunk(xml, tag1, tag2, idx);
			if(chunk.length()<1)break;
			
			if(!includeDelimiters){
				chunk = removeTags(chunk,tag1,tag2);
			}
			
			chunks.add(chunk);
			idx+=chunk.length();
		}
		
		return chunks;
	}

	private static String removeTags(String chunk, String tag1, String tag2) {
		 if(chunk.startsWith(tag1)){
		    	chunk = chunk.substring(tag1.length());
		    }
		    if(chunk.endsWith(tag2)){
		    	chunk = chunk.substring(0,chunk.length()-tag2.length());
		    }
		return chunk;
	}

	public static long getChunkAsLong(String xml, String tag1, String tag2) {
		String xvalue = getChunk(xml, tag1, tag2);
		
		long value = 0;
		
		try {
			value = Long.parseLong(xvalue.trim());
		}catch(Exception e){
			
		}
		
		return value;
	}

	public static List<String> getChunks(String json, String string,
			String string2) {
		return getChunks(json, string, string2,true);
	}
	
	public static List<String> getChunksByTagName(String xml, String tagName) {
		List<String> chunkList = new ArrayList<String>();
		
		String tag1="<"+tagName+" ";
		
		int index=0;
		
		while(true){	
			int idx1 = index;
			int idx2 = index;
			
			idx1 = xml.indexOf(tag1,index);
			if(idx1<0){
				break;
			} else {
				
				String ftag = getFirstTag(xml,index,">","/>");
				
				String tag2;
				if(">".equals(ftag)){
					tag2="</"+tagName+">";
				}else{
					tag2="/>";
				}
				
			   idx2=xml.indexOf(tag2,idx1+tag1.length());
			   if(idx1>=index && idx1<idx2){
					String chunk=xml.substring(idx1+tag1.length(),idx2).trim();
					chunkList.add(chunk);
					index=idx2+tag2.length();
				}else{
					break;
				}
			   
			}
			
			
		}
		
		return chunkList;
	}
	
	private static String getFirstTag(String xml, int startAt, String... tags){
		int firstIdx=-1;
		String firstTag=null;
		for(String tag : tags){
			int idx = xml.indexOf(tag,startAt);
			if(firstTag==null || ( idx>-1 && idx<firstIdx ) ){
				firstIdx=idx;
				firstTag=tag;
			}
		}
		
		return firstTag;
		
	}
	
	public static String escapeXML(String value){
		if (value != null){
			return value.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
		}
		return value;
		
	}
}
