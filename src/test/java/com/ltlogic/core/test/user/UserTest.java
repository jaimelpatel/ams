/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.user;

import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.RegistrationPojo;
import com.ltlogic.service.core.UserService;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
//import org.testng.annotations.Test;

/**
 *
 * @author Hoang
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TransactionConfiguration(defaultRollback = false)
@Service
public class UserTest {

    @Autowired
    UserService userService;
    @Autowired
    UserIWS userIWS;
    
    @Autowired
    UserRepository userRepo;
    @Autowired
    UserComponent userComponent;

    private static final Logger LOG = LoggerFactory.getLogger(UserTest.class);
    
    
    @Ignore
    @Test
    public void createDefaultUsers() {
        RegistrationPojo bRegistration = new RegistrationPojo();
        RegistrationPojo jRegistration = new RegistrationPojo();
        RegistrationPojo hRegistration = new RegistrationPojo();

        bRegistration.setEmail("bouncebytheouncecrew@gmail.com");
        bRegistration.setPassword("password123");
        bRegistration.setPasswordConfirm("password123");
        bRegistration.setRegionId("0");
        bRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
        bRegistration.setUsername("bishisthas");

        jRegistration.setEmail("jaimelpatel@yahoo.com");
        jRegistration.setPassword("password123");
        jRegistration.setPasswordConfirm("password123");
        jRegistration.setRegionId("0");
        jRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
        jRegistration.setUsername("jaimelp");

        hRegistration.setEmail("novacyclone@yahoo.com");
        hRegistration.setPassword("password123");
        hRegistration.setPasswordConfirm("password123");
        hRegistration.setRegionId("0");
        hRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
        hRegistration.setUsername("hoangn");


        User user1 = userIWS.registerUser(bRegistration);
        User user2 = userIWS.registerUser(jRegistration);
        User user3 = userIWS.registerUser(hRegistration);

        Assert.assertNotNull(user1);
        Assert.assertNotNull(user2);
        Assert.assertNotNull(user3);

        Assert.assertEquals("bouncebytheouncecrew@gmail.com", user1.getEmail());
        Assert.assertEquals("jaimelpatel@yahoo.com", user2.getEmail());
        Assert.assertEquals("novacyclone@yahoo.com", user3.getEmail());

        LOG.info("Created user 1 with username: " + user1.getUsername());
        LOG.info("Created user 2 with username: " + user2.getUsername());
        LOG.info("Created user 3 with username: " + user3.getUsername());

    }
    
    @Ignore
    @Test
    public void createListOftUsers() {
        List<User> userList = userComponent.createListOfUsers(0, 10);


        for (User u : userList) {
            Assert.assertNotNull(u);
        }
    }
}
