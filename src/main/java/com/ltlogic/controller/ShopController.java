/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author raymond
 */

@Controller
public class ShopController {
    
    @RequestMapping(value = "/shop", method = RequestMethod.GET)
    public String shop() {
       return "shop/index";
    }
    
    @RequestMapping(value = "/shop/authenticate", method = RequestMethod.GET)
    public String shopAuthenticate() {
       return "redirect:/shop";
    }
}
