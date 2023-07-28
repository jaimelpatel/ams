/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.db.entity.Transaction;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.helpers.Paginator;
import com.ltlogic.fe.models.form.MatchForm;
import com.ltlogic.fe.models.form.TransferGameCashForm;
import com.ltlogic.fe.models.form.TransferGameCashFormValidator;
import com.ltlogic.fe.models.paypal.PaypalTransaction;
import com.ltlogic.iws.BankIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.LoginDetailsPojo;
import com.ltlogic.service.core.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author raymond
 */
@Controller
public class BankController {

    @Autowired
    UserIWS userIWS;

    @Autowired
    BankIWS bankIWS;
    
    @Autowired
    UserService userService;

    @Autowired
    TransferGameCashFormValidator validator;
    
    @Autowired
    Paginator paginator;

    @RequestMapping(value = "/bank/account/amount", method = RequestMethod.GET)
    public @ResponseBody
    BigDecimal test(ModelMap model, Principal p) {
        if (p == null) {
            //do nothing
        } else {
            User user = userIWS.getUserByUsername(p.getName());
            user.getBank().getTotalAmount();
            return user.getBank().getTotalAmount();
        }
        return null;
    }

    @RequestMapping(value = "/bank/account/deposit", method = RequestMethod.POST)
    public String depositeNLGCash(PaypalTransaction paypalTransaction, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        if (p != null) {
            User user = userIWS.getUserByUsername(p.getName());
            if (user != null) {

                BigDecimal amount = getRealCashAmountToNLGCashAmount(paypalTransaction.getAmount());
                System.out.println("ID: " + paypalTransaction.getId());
                //put paypal transaction id where null is below
                bankIWS.depositCash(user, amount, paypalTransaction.getId());
                redirectAttributes.addFlashAttribute("message", "You have deposited $" + amount + " NLG cash into your account.");

            }
            return "redirect:/account/bank";
        }

        model.addAttribute("error", "There was an error depositing cash into your account because you were not logged in. Please contact support.");
        return "/login";
    }

    @RequestMapping(value = "/bank/account/withdraw", method = RequestMethod.GET)
    public String withdrawNLGCash(@ModelAttribute("transferGameCashForm") TransferGameCashForm transferGameCashForm, BindingResult bindingResult, ModelMap model, Principal p, @RequestParam(value = "page", required = false) Integer page) {
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
            
            return "bank/withdraw";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "bank/withdraw";
        }
    }

    @RequestMapping(value = "/bank/account/transfer", method = RequestMethod.GET)
    public String transferNLGCash(@ModelAttribute("transferGameCashForm") TransferGameCashForm transferGameCashForm, ModelMap model, Principal p, @RequestParam(value = "page", required = false) Integer page) {

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

            return "bank/transfer";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "bank/transfer";
        }
    }

    @RequestMapping(value = "/bank/account/transfer/confirm", method = RequestMethod.POST)
    public String confirmTransferNLGCash(@Valid @ModelAttribute("transferGameCashForm") TransferGameCashForm transferGameCashForm, BindingResult bindingResult,
            ModelMap model, Principal p) {
        User user = userIWS.getUserByUsername(p.getName());

        if (transferGameCashForm.getAmount() == null) {
            transferGameCashForm.setAmount(new BigDecimal(0.00));
        } else {
            BigDecimal rounded = transferGameCashForm.getAmount().setScale(2, RoundingMode.HALF_UP);
            transferGameCashForm.setAmount(rounded);
        }

        if (transferGameCashForm.getRecipientUsername() == null || transferGameCashForm.getRecipientUsername().isEmpty()) {
            model.addAttribute("bankAmount", user.getBank().getTotalAmount());
            model.addAttribute("error", "Please enter the username of the user you would like to transfer cash to.");
            return "bank/transfer";
        }

        if (user.getUsername().equals(transferGameCashForm.getRecipientUsername())) {
            model.addAttribute("bankAmount", user.getBank().getTotalAmount());
            model.addAttribute("error", "You cannot transfer cash to yourself.");
            return "bank/transfer";
        }
        validator.validateForm(transferGameCashForm, user, bindingResult);

        model.addAttribute("bankAmount", user.getBank().getTotalAmount());
        model.addAttribute("user", user);

        if (bindingResult.hasErrors()) {
            model.addAttribute("bankAmount", user.getBank().getTotalAmount());
            return "bank/transfer";
        }

        return "bank/confirm-transfer";
    }

    @RequestMapping(value = "/bank/account/transfer/process", method = RequestMethod.POST)
    public String processTransferNLGCash(@Valid @ModelAttribute("transferGameCashForm") TransferGameCashForm transferGameCashForm, BindingResult bindingResult, ModelMap model,
            Principal p, RedirectAttributes redirectAttributes) {

        User user = userIWS.getUserByUsername(p.getName());
        model.addAttribute("bankAmount", user.getBank().getTotalAmount());
        model.addAttribute("user", user);

        if (transferGameCashForm.getRecipientUsername() == null || transferGameCashForm.getRecipientUsername().isEmpty()) {
            model.addAttribute("bankAmount", user.getBank().getTotalAmount());
            model.addAttribute("error", "Please enter the username of the user you would like to transfer cash to.");
            return "bank/transfer";
        }

        if (transferGameCashForm.getAmount() == null) {
            model.addAttribute("bankAmount", user.getBank().getTotalAmount());
            model.addAttribute("error", "You must enter the amount of cash you would like to transfer.");
            return "bank/transfer";
        }

        if (user.getUsername().equals(transferGameCashForm.getRecipientUsername())) {
            model.addAttribute("bankAmount", user.getBank().getTotalAmount());
            model.addAttribute("error", "You cannot transfer cash to yourself.");
            return "bank/transfer";
        }

        validator.validateForm(transferGameCashForm, user, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("bankAmount", user.getBank().getTotalAmount());
            return "bank/transfer";
        }

        try {
            User recipient = userService.findByUsernameLowercase(transferGameCashForm.getRecipientUsername().toLowerCase());
            bankIWS.transferCash(user, recipient, transferGameCashForm.getAmount());
            redirectAttributes.addFlashAttribute("message", "You have transferred $" + transferGameCashForm.getAmount().setScale(2, RoundingMode.HALF_UP) + " to " + transferGameCashForm.getRecipientUsername() + ".");
            return "redirect:/account/bank";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "bank/transfer";
        }
    }

    @RequestMapping(value = "/bank/account/withdraw/confirm", method = RequestMethod.POST)
    public String confirmWithdrawNLGCash(@RequestParam(value = "amount", defaultValue = "0.00") BigDecimal amount, ModelMap model, Principal p) {
        User user = userIWS.getUserByUsername(p.getName());

        model.addAttribute("bankAmount", user.getBank().getTotalAmount());
        model.addAttribute("amount", amount.setScale(2, RoundingMode.HALF_UP));
        return "bank/confirm-withdraw";
    }

    @RequestMapping(value = "/bank/account/withdraw/process", method = RequestMethod.POST)
    public String processWithdrawNLGCash(@ModelAttribute("transferGameCashForm") TransferGameCashForm transferGameCashForm, @RequestParam(value = "wamount", defaultValue = "0.00") BigDecimal amount, ModelMap model, Principal p,
            RedirectAttributes redirectAttributes) {

        User user = userIWS.getUserByUsername(p.getName());
        model.addAttribute("bankAmount", user.getBank().getTotalAmount());

        try {
            BigDecimal amountActuallyWithdrawn = bankIWS.withdrawCash(user, amount);
            redirectAttributes.addFlashAttribute("message", "You have withdrawn $" + amountActuallyWithdrawn.setScale(2, RoundingMode.HALF_UP) + " from your account.");
            return "redirect:/account/bank";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("amount", amount);
            return "bank/withdraw";
        }
    }

    /*
        Convert the paypal amount in real cash into NLG cash amount
     */
    private BigDecimal getRealCashAmountToNLGCashAmount(BigDecimal realCashAmount) {
        BigDecimal finalAmount = new BigDecimal(0);

        if (realCashAmount.compareTo(new BigDecimal("5.45")) == 0) {
            finalAmount = new BigDecimal(5.00);
        } else if (realCashAmount.compareTo(new BigDecimal("10.60")) == 0) {
            finalAmount = new BigDecimal(10.00);
        } else if (realCashAmount.compareTo(new BigDecimal("20.90")) == 0) {
            finalAmount = new BigDecimal(25.00);
        } else if (realCashAmount.compareTo(new BigDecimal("51.80")) == 0) {
            finalAmount = new BigDecimal(50.00);
        } else if (realCashAmount.compareTo(new BigDecimal("103.30")) == 0) {
            finalAmount = new BigDecimal(100.00);
        }else if (realCashAmount.compareTo(new BigDecimal("206.30")) == 0) {
            finalAmount = new BigDecimal(200.00);
        }

        return finalAmount;
    }

}
