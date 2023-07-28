package com.ltlogic.service.core;

import com.ltlogic.db.entity.Privilege;
import com.ltlogic.db.entity.Role;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameLowercase(username.toLowerCase());

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        try{
        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleEnum().getRoleEnumDesc()));
            for (Privilege p : role.getPrivileges()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(p.getPrivilegeEnum().getPrivilegeEnumDesc()));
            }
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);

        }
        catch(NullPointerException ex){
            LOG.info("Non-existent user attempting to log in.");
            throw new UsernameNotFoundException("Username does not exist.");
        }
    }
}
