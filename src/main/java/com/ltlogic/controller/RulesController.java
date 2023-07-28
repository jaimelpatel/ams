/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.db.entity.User;
import com.ltlogic.fe.models.form.ProfileSettingsForm;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Jaimel
 */
@Controller
public class RulesController {
    
    @RequestMapping(value = "/rules/general", method = RequestMethod.GET)
    public String generalRules(ModelMap model, Principal p) {       
            return "rules/general-rules";        
    }
    @RequestMapping(value = "/rules/xp", method = RequestMethod.GET)
    public String ww2Rules(ModelMap model, Principal p) {       
            return "rules/ww2-rules";        
    }
    @RequestMapping(value = "/rules/cash", method = RequestMethod.GET)
    public String iwRules(ModelMap model, Principal p) {       
            return "rules/iw-rules";        
    }
        @RequestMapping(value = "/rules/game", method = RequestMethod.GET)
    public String mwrRules(ModelMap model, Principal p) {       
            return "rules/mwr-rules";        
    }
}
