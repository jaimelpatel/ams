/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller.admin;

import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.models.form.TournamentForm;
import com.ltlogic.fe.models.form.TournamentFormValidator;
import com.ltlogic.iws.TournamentServiceIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.TournamentPojo;
import com.ltlogic.web.exception.CustomErrorException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author raymond
 */
@Controller
public class AdminTournamentsController {

    @Autowired
    TournamentFormValidator createTournamentFormValidator;

    @Autowired
    TournamentServiceIWS tournamentIWS;

    @Autowired
    UserIWS userIWS;

    @RequestMapping(value = "/admin/tournaments", method = RequestMethod.GET)
    public String getTournaments(ModelMap model, Principal p) {
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
//            User user = userIWS.getUserByUsername(p.getName());
//            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }
        String username = p.getName();
        User user = userIWS.getUserByUsername(username);
        List<Tournament> tournaments = tournamentIWS.getAllTournaments();

        model.addAttribute("user", user);
        model.addAttribute("tournaments", tournaments);

        return "admin/tournaments/index";
    }

    @RequestMapping(value = "/admin/tournaments/create", method = RequestMethod.GET)
    public String createTournamentForm(@ModelAttribute("createTournamentForm") TournamentForm createTournamentForm,
            BindingResult bindingResult, ModelMap model, Principal p) {
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
//            User user = userIWS.getUserByUsername(p.getName());
//            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }
        return "admin/tournaments/create-tournament";
    }

    @RequestMapping(value = "/admin/tournaments/create", method = RequestMethod.POST)
    public String saveTournament(@Valid @ModelAttribute("createTournamentForm") TournamentForm createTournamentForm,
            BindingResult bindingResult, ModelMap model, Principal p) {
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
//            User user = userIWS.getUserByUsername(p.getName());
//            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }
        String username = p.getName();
        User user = userIWS.getUserByUsername(username);
        createTournamentFormValidator.validate(createTournamentForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/tournaments/create-tournament";
        }

        try {

            TournamentPojo tournamentPojo = TournamentForm.toTournamentPojo(createTournamentForm);
            tournamentIWS.createTournament(tournamentPojo);
            //redirect somewhere
            model.addAttribute("user", user);

            return "redirect:/admin/tournaments";
        } catch (Exception ex) {
            model.addAttribute("user", user);
            model.addAttribute("error", ex.getMessage());
            ex.printStackTrace();
            return "admin/tournaments/create-tournament";
        }
    }
}
