/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.fe.models.form.PasswordResetForm;
import com.ltlogic.configs.WebSecurityConfig;
import com.ltlogic.constants.PasswordResetStatusEnum;
import com.ltlogic.constants.TokenVerificationEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.iws.EmailIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.service.core.SecurityServiceImpl;
import com.ltlogic.fe.models.form.PasswordResetFormValidator;
import com.ltlogic.service.springsecurity.SecurityContextAccessorImpl;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Hoang
 */
@Controller
public class PasswordResetController {

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    WebSecurityConfig webSecurityConfig;

    @Autowired
    EmailIWS emailIWS;

    @Autowired
    UserIWS userIWS;

    @Autowired
    PasswordResetFormValidator passwordResetFormValidator;

    private static final Logger LOG = LoggerFactory.getLogger(PasswordResetController.class);

    private boolean isAnonymous() {
        SecurityContextAccessorImpl securityContextAccessor = webSecurityConfig.securityContextAccessorImpl();
        return securityContextAccessor.isCurrentAuthenticationAnonymous();
    }

    @RequestMapping(value = "/password/forgot", method = RequestMethod.GET)
    public String forgotPassword(Model model) {
        if (!isAnonymous()) {
            return "redirect:/";
        }

        return "forgot-password";
    }

    @RequestMapping(value = "/password/forgot", method = RequestMethod.POST)
    public String confirmForgotPassword(Model model, @RequestParam("email") String email, HttpServletRequest request) {
        if (!isAnonymous()) {
            return "redirect:/";
        }

        if (email == null || email.isEmpty()) {
            model.addAttribute("error", "Please enter an email address.");
            return "forgot-password";
        }

        User user = userIWS.getUserByEmail(email);
        if (user == null) {
            model.addAttribute("error", "We could not find an account associated with that email address.");
            return "forgot-password";
        }

        try {
            emailIWS.sendPasswordResetEmail(email, request);
            model.addAttribute("hasSentResetLink", true);
            model.addAttribute("message", "We have emailed you your password reset link.");

            return "forgot-password";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "forgot-password";
        }
    }

    @RequestMapping(value = "/password/reset", method = RequestMethod.GET)
    public String passwordReset(@ModelAttribute("passwordResetForm") PasswordResetForm passwordResetForm, Model model, @RequestParam("token") String token) throws UnsupportedEncodingException {
        if (!isAnonymous()) {
            return "redirect:/";
        }

        TokenVerificationEnum tokenValidation = userIWS.validatePasswordResetToken(token);

        if (tokenValidation != null) {
            if (tokenValidation == TokenVerificationEnum.TOKEN_EXPIRED || tokenValidation == TokenVerificationEnum.TOKEN_INVALID) {
                model.addAttribute("invalid_token", true);
                model.addAttribute("message", "Your password reset token has expired. Please send another password reset request.");
                return "password-reset";
            }
        }

        model.addAttribute("token", token);
        return "password-reset";
    }

    @RequestMapping(value = "/password/reset", method = RequestMethod.POST)
    public String confirmPasswordReset(@Valid @ModelAttribute("passwordResetForm") PasswordResetForm passwordResetForm, BindingResult bindingResult,
            Model model) throws UnsupportedEncodingException {

        passwordResetFormValidator.validate(passwordResetForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "password-reset";
        }

        TokenVerificationEnum tokenValidation = userIWS.validatePasswordResetToken(passwordResetForm.getToken());

        if (tokenValidation != null) {
            if (tokenValidation == TokenVerificationEnum.TOKEN_EXPIRED || tokenValidation == TokenVerificationEnum.TOKEN_INVALID) {
                return "redirect/password-reset?token=" + passwordResetForm.getToken();
            }
        }

        try {
            PasswordResetStatusEnum result = userIWS.resetPasswordByPasswordResetToken(passwordResetForm.getToken(), passwordResetForm.getNewPassword());
            if (result != null && result == PasswordResetStatusEnum.PASSWORD_MISMATCH) {
                model.addAttribute("error", "Old password does not match.");
                return "password-reset";
            }
            model.addAttribute("successMessage", "Your password has been reset. Please login.");
            model.addAttribute("loginSuccess", true);
            return "login";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "password-reset";
        }

    }

    @RequestMapping(value = "/password/reset/successful", method = RequestMethod.GET)
    public String resetPasswordSuccessful(Locale locale, Model model) throws UnsupportedEncodingException {

        return "password-reset-successful";

    }

    @RequestMapping(value = "/username/forgot", method = RequestMethod.GET)
    public String forgotUsername(Model model) {
        if (!isAnonymous()) {
            return "redirect:/";
        }

        return "forgot-username";
    }

    @RequestMapping(value = "/username/forgot", method = RequestMethod.POST)
    public String forgotUsernameEmail(Model model, @RequestParam("email") String email, HttpServletRequest request) {
        if (!isAnonymous()) {
            return "redirect:/";
        }

        if (email == null || email.isEmpty()) {
            model.addAttribute("error", "Please enter an email address.");
            return "forgot-username";
        }

        User user = userIWS.getUserByEmail(email);
        if (user == null) {
            model.addAttribute("error", "We could not find an account associated with that email address.");
            return "forgot-username";
        }

        try {
            emailIWS.sendForgottenUsernameEmail(email, request);
            model.addAttribute("hasSentResetLink", true);
            model.addAttribute("message", "We have emailed you your username.");
            return "forgot-username";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "forgot-username";
        }
    }
}
