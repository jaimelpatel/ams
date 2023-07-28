/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.common;

import com.ltlogic.constants.PrivilegeEnum;
import com.ltlogic.constants.RoleEnum;
import com.ltlogic.db.entity.Privilege;
import com.ltlogic.db.entity.Role;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.PrivilegeRepository;
import com.ltlogic.db.repository.RoleRepository;
import com.ltlogic.db.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hoang
 */
@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent e) {
//        if (alreadySetup) {
//            return;
//        }
//
//        for(PrivilegeEnum p : PrivilegeEnum.values()){
//            createPrivilegeIfNotFound(p);
//        }
//        
//        for(RoleEnum r : RoleEnum.values()){
//             createRoleIfNotFound(r);
//        }
//        
//        List<Privilege> adminPrivileges = new ArrayList<>();
//        adminPrivileges.add(privilegeRepository.findPrivilegeByEnum(PrivilegeEnum.USER_PRIVILEGE));
//        adminPrivileges.add(privilegeRepository.findPrivilegeByEnum(PrivilegeEnum.REFEREE_PRIVILEGE));
//        adminPrivileges.add(privilegeRepository.findPrivilegeByEnum(PrivilegeEnum.ADMIN_PRIVILEGE));
//        
//        List<Privilege> refereePrivileges = new ArrayList<>();
//        refereePrivileges.add(privilegeRepository.findPrivilegeByEnum(PrivilegeEnum.USER_PRIVILEGE));
//        refereePrivileges.add(privilegeRepository.findPrivilegeByEnum(PrivilegeEnum.REFEREE_PRIVILEGE));
//        
//        Privilege userPrivilege = privilegeRepository.findPrivilegeByEnum(PrivilegeEnum.USER_PRIVILEGE);
//        
//        Role adminRole = roleRepository.findRoleByEnum(RoleEnum.ROLE_ADMIN);
//        adminRole.getPrivileges().addAll(adminPrivileges);
//        for(Privilege a : adminPrivileges){
//            a.getRoles().add(adminRole);
//        }
//        
//        Role refereeRole = roleRepository.findRoleByEnum(RoleEnum.ROLE_REFEREE);
//        refereeRole.getPrivileges().addAll(refereePrivileges);
//        for(Privilege r : refereePrivileges){
//            r.getRoles().add(refereeRole);
//        }
//        
//        Role userRole = roleRepository.findRoleByEnum(RoleEnum.ROLE_USER);
//        userRole.getPrivileges().add(userPrivilege);
//        userPrivilege.getRoles().add(userRole);
//        
//        User testAdmin1 = new User();
//        testAdmin1.setFirstName("Admin");
//        testAdmin1.setLastName("User");
//        testAdmin1.setUsername("testAdmin1");
//        testAdmin1.setPassword(passwordEncoder.encode("admin"));
//        testAdmin1.setEmail("admin@test.com");
//        testAdmin1.getRoles().add(adminRole);
//
//        User testReferee1 = new User();
//        testReferee1.setFirstName("Referee");
//        testReferee1.setLastName("User");
//        testReferee1.setUsername("testReferee");
//        testReferee1.setPassword(passwordEncoder.encode("referee"));
//        testReferee1.setEmail("referee@test.com");
//        testReferee1.getRoles().add(refereeRole);
//        
//        User normalUser1 = new User();
//        normalUser1.setFirstName("Normal");
//        normalUser1.setLastName("User");
//        normalUser1.setUsername("testUser");
//        normalUser1.setPassword(passwordEncoder.encode("user"));
//        normalUser1.setEmail("user@test.com");
//        normalUser1.getRoles().add(userRole);
//        
//        userRepository.persistUser(testAdmin1);
//        userRepository.persistUser(testReferee1);
//        userRepository.persistUser(normalUser1);
//
//        alreadySetup = true;
    }

    @Transactional
    private Privilege createPrivilegeIfNotFound(PrivilegeEnum privEnum) {

        Privilege privilege = privilegeRepository.findPrivilegeByEnum(privEnum);
        if (privilege == null) {
            privilege = new Privilege(privEnum);
            privilegeRepository.persistPrivilege(privilege);
        }
        return privilege;
    }

    @Transactional
    private Role createRoleIfNotFound(RoleEnum roleEnum) {

        Role role = roleRepository.findRoleByEnum(roleEnum);
        if (role == null) {
            role = new Role(roleEnum);
            roleRepository.persistRole(role);
        }
        return role;
    }
}
