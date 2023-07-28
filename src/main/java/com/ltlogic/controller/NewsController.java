/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.fe.models.form.SupportTicketForm;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author raymond
 */
@Controller
public class NewsController {

    @RequestMapping(value = "/news", method = RequestMethod.GET)
    public String support(ModelMap model, Principal p) {

        return "news";
    }

    @RequestMapping(value = "/news/playoffs", method = RequestMethod.GET)
    public String playoffs(ModelMap model, Principal p) {

        return "playoffs";
    }

    @RequestMapping(value = "/news/features", method = RequestMethod.GET)
    public String features(ModelMap model, Principal p) {

        return "features";
    }
}
