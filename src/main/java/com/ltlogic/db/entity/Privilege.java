/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.constants.PrivilegeEnum;
import com.ltlogic.db.superentity.SuperEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Hoang
 */

@NamedQueries({
    @NamedQuery(
            name = "Privilege.getPrivilegeByEnum",
            query = "SELECT p FROM Privilege p WHERE p.privilegeEnum = :privilegeEnum"
    )
})

@Entity
public class Privilege extends SuperEntity {
    
    private PrivilegeEnum privilegeEnum;
    
    private String name;
 
    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles = new HashSet<Role>(0);

    public Privilege(){
    }
    
    public Privilege(PrivilegeEnum privEnum){
        this.privilegeEnum = privEnum;
        this.name = privEnum.getPrivilegeEnumDesc();
    }
    public Privilege(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public PrivilegeEnum getPrivilegeEnum() {
        return privilegeEnum;
    }

    public void setPrivilegeEnum(PrivilegeEnum privilegeEnum) {
        this.privilegeEnum = privilegeEnum;
    }
}
