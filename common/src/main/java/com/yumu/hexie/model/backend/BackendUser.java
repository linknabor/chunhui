package com.yumu.hexie.model.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yumu.hexie.model.BaseModel;

@Entity
public class BackendUser  extends BaseModel implements  UserDetails {

	private static final long serialVersionUID = 4808669460780339640L;

	public BackendUser(){
		
	}
	public BackendUser(Long id,String username,String password,long roleId){
		if(id!=null){
			this.setId(id);
		}
		this.username = username;
		this.password = password;
		BackendRole role = new BackendRole();
		role.setId(roleId);
		this.role = role;
	}
	
	private String username;
	private String password;
	
	@ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.REFRESH }, optional = true)
    @JoinColumn(name = "roleId")
    private BackendRole role;
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    @Transient
    @JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

    	if(role!=null) {
    		List<GrantedAuthority> ga = new ArrayList<GrantedAuthority>();
    		for(String r : role.getPermission()){
    			ga.add(new SimpleGrantedAuthority(r));
    		}
    		return ga;
    	}
		return null;
	}
	@Override
    @JsonIgnore
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    @Transient
    public boolean isEnabled() {
        return true;
    }
	public BackendRole getRole() {
		return role;
	}
	public void setRole(BackendRole role) {
		this.role = role;
	}
}
