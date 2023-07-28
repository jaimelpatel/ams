/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.constants.PasswordResetStatusEnum;
import com.ltlogic.constants.TokenVerificationEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.pojo.RegistrationPojo;
import com.ltlogic.pojo.UserPojo;
import com.ltlogic.service.core.EmailService;
import com.ltlogic.service.core.SecurityService;
import com.ltlogic.service.core.UserService;
import com.ltlogic.service.springsecurity.UserValidator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

/**
 *
 * @author jaimel
 */
@Service
public class UserIWS {
    
    @Autowired
    SecurityService securityService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    UserValidator userValidator;
    
    @Autowired
    EmailService emailService;
    
    
    public Boolean isEmailExists(String email) {
        return userService.emailExist(email);
    }

    public void persistUser(User user) {
        userService.persistUser(user);
    }
    
    public User registerUser(RegistrationPojo registrationPojo){
        return userService.registerUser(registrationPojo);
    }
    
    public void autologin(String username, String password){
        securityService.autologin(username, password);
    }
    
    public void editUserInfo(UserPojo userInfo, long pk){
        userService.editUserInfo(userInfo, pk);
    }
    
    public String editUserPassword(long pk, String oldPassword, String newPassword) throws Exception{
        return userService.editUserPassword(pk, oldPassword, newPassword);
    }
    
    public void resendVerificationEmail(String email, HttpServletRequest servletRequest){
        emailService.resendVerificationEmail(email, servletRequest);
    }
    
    public TokenVerificationEnum validateEmailVerificationToken(String token){
        return userService.validateVerificationToken(token);
    }
    
    public User getUserFromVerificationToken(String token){
        return userService.getUserFromVerificationToken(token);
    }
    
    public TokenVerificationEnum validatePasswordResetToken(String token){
        return userService.validatePasswordResetToken(token);
    }
    
    public User getUserFromPasswordResetToken(String token){
        return userService.getUserFromPasswordResetToken(token);
    }
    
    public User getUserByEmail(String email){
        return userService.findByEmail(email);
    }
    
    public User getUserByUsername(String username) {
        return userService.findByUsername(username);
    }
    
    public User getUserByPk(long pk){
        return userService.findByPk(pk);
    }
    
    public PasswordResetStatusEnum resetPasswordByPasswordResetToken(String passwordResetToken, String newPassword) throws Exception {
        PasswordResetStatusEnum result = userService.resetUserPasswordByPasswordResetToken(passwordResetToken, newPassword);
        return result;
    }
    
    public TokenVerificationEnum validateVerificationToken(String token){
       TokenVerificationEnum verificationEnum = userService.validateVerificationToken(token);
       return verificationEnum;
    }
    

    public List<User> getAllUsersOnTeam(long teamPk){
        return userService.getAllUsersOnTeam(teamPk);
    }
    
    public List<User> getUsersOnTeamOrderByMatchAcceptor(long teamPk, long matchAcceptorPk){
        return userService.getUsersOnTeamOrderByMatchAcceptor(teamPk, matchAcceptorPk);
    }
    
    public List<User> getUsersBySearchedUsername(String username) {
        return userService.getUsersBySearchedUsername(username);
    }
    
    public void setUserNotificationNumber(User user, int notificationNumber){
        userService.setUserNotificationNumber(user, notificationNumber);
    }
}



