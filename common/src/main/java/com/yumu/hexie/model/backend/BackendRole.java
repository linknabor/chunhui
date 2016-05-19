package com.yumu.hexie.model.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.BaseModel;

@Entity
public class BackendRole  extends BaseModel{
	private static final long serialVersionUID = 4808669460780339640L;

	private String name;//角色名
	
	private String permissions;//权限，逗号分隔
	
	

	@JsonIgnore
    @OneToMany(targetEntity = BackendUser.class, fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH}, mappedBy = "role")
    @Fetch(FetchMode.SUBSELECT)
    private List<BackendUser> users;
	
	public List<String> getPermission(){
		if(StringUtil.isEmpty(permissions)) {
			return new ArrayList<String>();
		}
		else {
			return Arrays.asList(permissions.split(","));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public List<BackendUser> getUsers() {
		return users;
	}

	public void setUsers(List<BackendUser> users) {
		this.users = users;
	}
	
	
}
