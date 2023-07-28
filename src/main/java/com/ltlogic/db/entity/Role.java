/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.constants.RoleEnum;
import com.ltlogic.db.superentity.SuperEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Jaimel
 */

@NamedQueries({
    @NamedQuery(
            name = "Role.getRoleByEnum",
            query = "SELECT r FROM Role r WHERE r.roleEnum = :roleEnum"
    )
})

@Entity
public class Role extends SuperEntity{
    
    private RoleEnum roleEnum;
    
    private String name;
    
    @ManyToMany
    @JoinTable(name = "roles_privileges", joinColumns = @JoinColumn(name = "rolePk"), inverseJoinColumns = @JoinColumn(name = "privilegePk"))
    private Set<Privilege> privileges = new HashSet<Privilege>(0);   
    
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "roles")
    private Set<User> users = new HashSet<User>(0);
    
     public Role(){
    }
    
    public Role(RoleEnum roleEnum){
        this.roleEnum = roleEnum;
        this.name = roleEnum.getRoleEnumDesc();
    }
    
    public Role(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoleEnum getRoleEnum() {
        return roleEnum;
    }

    public void setRoleEnum(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }
    
    
}


