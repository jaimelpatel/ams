/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.configs.WebSecurityConfig;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.Transaction;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.helpers.Paginator;
import com.ltlogic.fe.models.form.GeneralAccountSettingsForm;
import com.ltlogic.fe.models.form.GeneralAccountSettingsFormValidator;
import com.ltlogic.fe.models.form.ProfileSettingsForm;
import com.ltlogic.fe.models.form.ProfileSettingsFormValidator;
import com.ltlogic.fe.models.form.TransferGameCashForm;
import com.ltlogic.iws.BankIWS;
import com.ltlogic.iws.UserIWS;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author raymond
 */
@Controller
public class AccountController {

    @Autowired
    UserIWS userIWS;

    @Autowired
    WebSecurityConfig webSecurityConfig;

    @Autowired(required = true)
    GeneralAccountSettingsFormValidator generalAccountSettingsFormValidator;

    @Autowired(required = true)
    ProfileSettingsFormValidator profileSettingsFormValidator;

    @Autowired
    BankIWS bankIWS;
    
    @Autowired
    Paginator paginator;

    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String account(ModelMap model, Principal p, @ModelAttribute("generalAccountForm") GeneralAccountSettingsForm generalAccountForm) {

        try {
            User user = userIWS.getUserByUsername(p.getName());
            if (user == null) {
                throw new Exception("User could not be found.");
            }

            generalAccountForm = GeneralAccountSettingsForm.fromUserEntityToGeneralAccountSettingsForm(user);

            model.addAttribute("generalAccountForm", generalAccountForm);
            model.addAttribute("timeZoneEnumList", TimeZoneEnum.values());
            return "account/account";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "account/account";
        }
    }

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public String updateAccount(@Valid @ModelAttribute("generalAccountForm") GeneralAccountSettingsForm generalAccountForm, BindingResult bindingResult,
            ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        generalAccountSettingsFormValidator.validate(generalAccountForm, bindingResult);

        if (bindingResult != null && bindingResult.hasErrors()) {
            model.addAttribute("generalAccountForm", generalAccountForm);
            model.addAttribute("timeZoneEnumList", TimeZoneEnum.values());
            return "account/account";
        }

        try {
            User user = userIWS.getUserByUsername(p.getName());
            if (user == null) {
                throw new Exception("User could not be found.");
            }

            user = GeneralAccountSettingsForm.toUserEntity(generalAccountForm, user);

            //Save user here
            userIWS.editUserInfo(user.getUserInfo(), user.getPk());

            //update user password
            if (generalAccountForm != null) {
                if ((generalAccountForm.getOldPassword() != null && !generalAccountForm.getOldPassword().isEmpty())
                        && (generalAccountForm.getPassword() != null && !generalAccountForm.getPassword().isEmpty())) {

                    userIWS.editUserPassword(user.getPk(), generalAccountForm.getOldPassword(), generalAccountForm.getPassword());
                }
            }

            redirectAttributes.addFlashAttribute("message", "Your account information has been saved.");
            return "redirect:/account";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "account/account";
        }
    }

    @RequestMapping(value = "/account/profile", method = RequestMethod.GET)
    public String accountProfile(@ModelAttribute("profileSettingsForm") ProfileSettingsForm profileSettingsForm, @ModelAttribute("uploadImageForm") Object uploadImageForm, BindingResult bindingResult, ModelMap model, Principal p) {

        try {
            User user = userIWS.getUserByUsername(p.getName());

            if (user == null) {
                throw new Exception("User could not be found.");
            }

            profileSettingsForm = ProfileSettingsForm.fromUserEntityToProfileSettingsForm(user);

            model.addAttribute("user", user);
            model.addAttribute("profileSettingsForm", profileSettingsForm);
            return "account/profile";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "account/profile";
        }
    }

    @RequestMapping(value = "/account/profile", method = RequestMethod.POST)
    public String updateAccountProfile(@ModelAttribute("profileSettingsForm") ProfileSettingsForm profileSettingsForm, @ModelAttribute("uploadImageForm") Object uploadImageForm, BindingResult bindingResult,
            ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        profileSettingsFormValidator.validate(profileSettingsForm, bindingResult);

        if (bindingResult != null && bindingResult.hasErrors()) {
            model.addAttribute("profileSettingsForm", profileSettingsForm);
            return "account/profile";
        }

        try {
            User user = userIWS.getUserByUsername(p.getName());

            if (user == null) {
                throw new Exception("User could not be found.");
            }

            user = ProfileSettingsForm.toUserEntity(profileSettingsForm, user);

            //Save user here
            userIWS.editUserInfo(user.getUserInfo(), user.getPk());

            redirectAttributes.addFlashAttribute("message", "Your profile has been saved.");
            return "redirect:/account/profile";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "account/profile";
        }
    }

    @RequestMapping(value = "/account/bank", method = RequestMethod.GET)
    public String accountBank(ModelMap model, Principal p, ProfileSettingsForm profileSettingsForm,
            @ModelAttribute("transferGameCashForm") TransferGameCashForm transferGameCashForm,
            @RequestParam(value = "page", required = false) Integer page) {

        try {

            Integer currentPage = 1;
            if (page != null && page > 1) {
                currentPage = page;
            }

            User user = userIWS.getUserByUsername(p.getName());
            List<Transaction> transactions = bankIWS.getAllTransactionsForUser(user.getPk(), currentPage - 1);
            model.addAttribute("user", user);

            model.addAttribute("transactions", transactions);
            model.addAttribute("bankAmount", user.getBank().getTotalAmount());

            Integer totalTransactions = bankIWS.getTotalTransactionsForUser(user.getPk());

            paginator.setPagination(null,currentPage, totalTransactions, model);
            
            return "account/bank";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "account/bank";
        }
    }
}
