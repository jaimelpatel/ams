/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.user;

import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.RegistrationPojo;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hoang
 */
@Component
@Transactional
public class UserComponent {

    @Autowired
    UserIWS userIWS;
    @Autowired
    TeamIWS teamIWS;
    @Autowired
    TeamInviteIWS teamInviteIWS;

    public ArrayList<User> createListOfUsers(int indexToStartAt, int numOfUsersToCreate) {

        ArrayList<User> userList = new ArrayList<>();
        ArrayList<RegistrationPojo> registrationPojoList = new ArrayList<>();
        RegistrationPojo bRegistration;
        int indexToStopAt = indexToStartAt + numOfUsersToCreate;
        for (int i = indexToStartAt; i < indexToStopAt; i++) {
            bRegistration = new RegistrationPojo();
            bRegistration.setEmail("hnbusinesscontact" + i + "@gmail.com");
            bRegistration.setPassword("pass");
            bRegistration.setPasswordConfirm("pass");
            bRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
            bRegistration.setUsername("user" + i);
            bRegistration.setRegionId("0");
            registrationPojoList.add(bRegistration);
        }
        for (RegistrationPojo rp : registrationPojoList) {
            User user = new User();
            user = userIWS.registerUser(rp);
            user.getBank().setTotalAmount(new BigDecimal(50.00));
            user.getUserInfo().setPlayStation4Name("ola" + user.getPk());
            user.getUserInfo().setXboxOneGamerTag("comostas" + user.getPk());
            userList.add(user);
        }
        return userList;
    }
}
