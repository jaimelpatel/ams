/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.springsecurity;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.service.core.UserService;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.pojo.LoginDetailsPojo;
import com.ltlogic.pojo.RegistrationPojo;
import com.ltlogic.pojo.TeamPojo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 * @author Jaimel
 */
@Component
public class UserValidator implements Validator {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private TeamRepository teamRepo;

    private static final Logger LOG = LoggerFactory.getLogger(UserValidator.class);

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
            errors.rejectValue("username", "Size.userForm.username");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }


//     Can password and passwordConfirm field be checked for sameness from the frontend? Would rather do it from there
        
        
//        if (!user.getPasswordConfirm().equals(user.getPassword())) {
//            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
//        }
    }

    public String loginValidation(Object o, Errors errors) {
        LoginDetailsPojo ldp = (LoginDetailsPojo) o;
        String errorMessage = "";
        String userName = ldp.getUsername();
        //String password = ldp.getPassword();
        
        if (userName.length() < 6 || userName.length() > 32 || !containsOnlyAlphaNumeric(userName)) {
            errors.rejectValue("username", "Size.userForm.username");
            errorMessage = "Username invalid.";
            return errorMessage;
        }
        return errorMessage;
    }
    
    public void registrationValidation(RegistrationPojo rp, Errors errors) {
        //User user = (User) o;
        String userName = rp.getUsername();
        String password = rp.getPassword();

        String email = rp.getEmail();

        if(userRepo.userWithEmailExists(email)){
            errors.reject("email", "This email is already in use.");
        }
        if (!containsOnlyAlphaNumeric(userName)) {
            errors.reject("username", "Username may only contain letters, digits and underscores.");
        }
        if (userName.length() > 32) {
            errors.reject("username", "Please enter a username with less than 32 characters.");
        }
        if (userRepo.userWithUsernameExists(userName.toLowerCase())) {
            errors.reject("username", "This username already exists.");
        }
        if(password.isEmpty()){
            errors.reject("username", "Password cannot be empty.");
        }
        if (password.contains("<") || password.contains(">") || password.contains(";")) {
            errors.reject("username", "Password may not contain the following special characters: < > ;");
        }
        

//        if (!isStrongPassword(password) || password.length() > 32 ) {
//            errors.rejectValue("password", "Size.userForm.password");
//            errorMessage = "Password must contain 1 lowercase, 1 uppercase, 1 numeric, 1 special character (!@#\\$%\\^&\\*), length > 7 and < 32";
//            return errorMessage;
//        }
        if (!rp.getPasswordConfirm().equals(rp.getPassword())) {
            errors.reject("passwordConfirm", "The password and password confirmation entered do not match.");
        }
        
//        if(rp.getRegionId() = null){
//            errors.rejectValue("password", "Size.userForm.password");
//            errorMessage = "Please select a region.";
//            return errorMessage;
//        }
    }
    
    public boolean isStrongPassword(String toExamine){
        /*
        ^	The password string will start this way
        (?=.*[a-z])	The string must contain at least 1 lowercase alphabetical character
        (?=.*[A-Z])	The string must contain at least 1 uppercase alphabetical character
        (?=.*[0-9])	The string must contain at least 1 numeric character
        (?=.*[!@#\$%\^&\*])	The string must contain at least one special character, but we are escaping reserved RegEx characters to avoid conflict
        (?=.{8,})	The string must be eight characters or longer
        */
        
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }
    
    public boolean containsOnlyAlphaNumeric(String toExamine) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }
    
    public boolean containsOnlyAlphaNumericAndSpaces(String toExamine) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\s]*$");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }
    //if i == -1, str has no uppercase characters
    public int lastIndexOfUCL(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (Character.isUpperCase(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
    
    //if i == -1, str has no lowercase characters
    public int lastIndexOfLCL(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (Character.isLowerCase(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
    
    //if i == -1, str has no digits
    public int lastIndexOfNumber(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (Character.isDigit(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

}
