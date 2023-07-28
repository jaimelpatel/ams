/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.transaction;

import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.core.test.user.UserComponent;
import com.ltlogic.core.test.user.UserTest;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.RegistrationPojo;
import com.ltlogic.service.core.BankService;
import com.ltlogic.service.core.UserService;
import java.math.BigDecimal;
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
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 *
 * @author Bishistha
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TransactionConfiguration(defaultRollback = false)
@Service
public class TransactionTest {
    
    @Autowired
    UserService userService;
    @Autowired
    UserIWS userIWS;
    
    @Autowired
    UserRepository userRepo;
    @Autowired
    UserComponent userComponent;
    
    @Autowired
    private BankService bankService;

    private static final Logger LOG = LoggerFactory.getLogger(TransactionTest.class);
    
    
    @Ignore
    @Test
    public void createDefaultUsers() {
        RegistrationPojo bRegistration = new RegistrationPojo();
        RegistrationPojo jRegistration = new RegistrationPojo();
        RegistrationPojo hRegistration = new RegistrationPojo();

        bRegistration.setEmail("bouncebytheouncecrew@gmail.com");
        bRegistration.setPassword("password123");
        bRegistration.setPasswordConfirm("password123");
        bRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
        bRegistration.setUsername("bishisthas");

        jRegistration.setEmail("jaimelpatel@yahoo.com");
        jRegistration.setPassword("password123");
        jRegistration.setPasswordConfirm("password123");
        jRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
        jRegistration.setUsername("jaimelp");

        hRegistration.setEmail("novacyclone@yahoo.com");
        hRegistration.setPassword("password123");
        hRegistration.setPasswordConfirm("password123");
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
        
        bankService.depositCash(user1, new BigDecimal(5555), null);
        bankService.depositCash(user2, new BigDecimal(6666), null);
        
    }
}
