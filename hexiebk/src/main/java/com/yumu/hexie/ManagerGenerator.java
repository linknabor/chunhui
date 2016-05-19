package com.yumu.hexie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.yumu.hexie.model.distribution.ServiceRegion;

public class ManagerGenerator {

	private static final String webBASE = "E:/雨幕/klb/work/hexieproject/hexiebk/src/main/java/com/yumu/hexie/backend/web";
	private static final String pageBase = "E:/雨幕/klb/work/hexieproject/hexiebk/src/main/webapp/backend/page";
	private static final Object bean = new ServiceRegion();
	private static final String NAME = "维修工设置";
	private static final String getModelName() {
		return bean.getClass().getSimpleName();
	}
	private static String getFormField(){
		String row = "        <div class=\"fitem\"><label>%V%:</label><input name=\"%V%\" class=\"easyui-textbox\"></div>\n";
		return extraForFields(row);
	}
	private static String extraForFields(String row) {
		String result = "";
		Map m;
		try {
			m = PropertyUtils.describe(bean);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		for(Object x : m.keySet()) {
			if(x.equals("id") ||
					x.equals("createDate") ||
					x.equals("class")){
				continue;
			}
			result += row.replaceAll("%V%", (String)x);
		}
		return result;
	}
	public static String getProperties(){
		String row = " 	  			{field:'%V%',title:'%V%',width:100},\n";
		return extraForFields(row);
		
	}
//	public static void main(String[] args)throws Exception{
//		initManager();
//		initHTML();
//	}
	private static void initManager() {
		File f = new File(webBASE+"/"+getModelName()+"Manager.java");
		String model = getModelName().replace(getModelName().charAt(0)+"", (getModelName().charAt(0)+"").toLowerCase());
		String temp = readString(ManagerGenerator.class.getResourceAsStream("/template/magagerTemplate.txt"));
		temp = temp.replaceAll("#MODEL#", getModelName()).replaceAll("#model#", model).replace("#name#", NAME);
		
		writeString(f,temp);
	}
	
	private static void initHTML() {
		String model = getModelName().replace(getModelName().charAt(0)+"", (getModelName().charAt(0)+"").toLowerCase());
		File d = new File(pageBase+"/"+model);
		if(!d.exists()) {
			d.mkdir();
		}
		File f = new File(pageBase+"/"+model+"/list.html");
		String temp = readString(ManagerGenerator.class.getResourceAsStream("/template/list.html"));
		temp = temp.replaceAll("#MODEL#", getModelName())
				.replaceAll("#model#", model)
				.replace("#name#", NAME)
				.replace("#formfield#", getFormField());
		
		writeString(f,temp);
		
		

		f = new File(pageBase+"/"+model+"/list.js");
		temp = readString(ManagerGenerator.class.getResourceAsStream("/template/list.js"));
		temp = temp.replaceAll("#MODEL#", getModelName()).replaceAll("#model#", model).replace("#name#", NAME)
				.replace("#fields#", getProperties());
		writeString(f,temp);
	}
	
	
	
	private static void writeString(File f, String temp) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(f));
			bw.write(temp);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static String readString(InputStream is) {
		InputStreamReader inputStreamReader;
		try {
			inputStreamReader = new InputStreamReader(is
					, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);


			StringBuffer buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str+"\n");
			}
			bufferedReader.close();
			inputStreamReader.close();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
