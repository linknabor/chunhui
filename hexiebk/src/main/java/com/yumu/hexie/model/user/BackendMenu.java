package com.yumu.hexie.model.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.BaseModel;

@Entity
public class BackendMenu  extends BaseModel{
	private static final long serialVersionUID = 4808669460780339640L;

	private String text;
	
	private String url;
	
	private String iconCls;
	
	private int sortNo = 0;//从小到大
	
	private String permissions;//权限，逗号分隔
	
    private long parentId = 0;
	
    @JsonIgnore
	public List<String> getPermission(){
		if(StringUtil.isEmpty(permissions)) {
			return new ArrayList<String>();
		}
		else {
			return Arrays.asList(permissions.split(","));
		}
	}
    
    @JsonIgnore
    public boolean isPermit(String permission) {
    	return getPermission().contains(permission);
    }


	public Attribute getAttributes(){
		if(StringUtil.isNotEmpty(url)) {
			return new Attribute(url);
		}
		return null;
	}
	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
	public static class Attribute implements Serializable{
		private static final long serialVersionUID = -1698733941826990169L;
		public Attribute(){
			
		}
		public Attribute(String url) {
			this.url = url;
		}
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		
	}


	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getIconCls() {
		if(StringUtil.isEmpty(iconCls)){
			return "tree-file";
		}
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public int getSortNo() {
		return sortNo;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}
	
}
