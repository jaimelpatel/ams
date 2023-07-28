/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.fe.models.form.ProfileSettingsForm;
import com.ltlogic.iws.CloudMediaIWS;
import com.ltlogic.pojo.TeamInvitePojo;
import com.ltlogic.service.springsecurity.ImageValidator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.ZoneId;
import java.util.TimeZone;
import javax.imageio.ImageIO;
import javax.validation.Path;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
public class UploadController {

    @Autowired
    CloudMediaIWS cloudMediaIWS;

    @Autowired
    UserRepository userRepo;

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    ImageValidator imageValidator;

    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "F://temp//";

//    //@GetMapping("/upload")
//    @RequestMapping(value = "/upload", method = RequestMethod.GET)
//    public String index() {
//        return "upload/index";
//    }
    //@PostMapping("/upload") // //new annotation since 4.3
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String singleFileUpload(@ModelAttribute("uploadImageForm") Object uploadImageForm, BindingResult bindingResult, @RequestParam("file") MultipartFile file, @ModelAttribute("profileSettingsForm") ProfileSettingsForm profileSettingsForm, ModelMap model, Principal p) throws Exception {

        User user = userRepo.findByUsername(p.getName());

        imageValidator.validateContentTypeAndDimensionForImage(file, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "account/profile";
        }
        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            //Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            //Files.write(path, bytes);
            String displayUrl = cloudMediaIWS.updateUserProfilePicture(user.getPk(), file.getContentType(), bytes);
            model.addAttribute("successMessage", "You successfully uploaded '" + file.getOriginalFilename() + "' as your user profile picture.");
            model.addAttribute("image", displayUrl);

            ZoneId defaultZone = TimeZone.getDefault().toZoneId();
            model.addAttribute("user", user);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "account/profile";
    }

    @RequestMapping(value = "/upload/{teamPk}", method = RequestMethod.POST)
    public String updateTeamPicture(
            @ModelAttribute("uploadImageForm") Object uploadImageForm, @ModelAttribute("teamInvitePojo") TeamInvitePojo teamInvitePojo,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file,
            @PathVariable long teamPk,
            ModelMap model,
            Principal p) throws Exception {

        String username = p.getName();
        User user = userRepo.findByUsername(username);
        Team team = teamRepo.findTeamByPk(teamPk);

        if (team.getTeamPojo().getTeamType() == TeamTypeEnum.CASH && team.getTeamPojo().getGame() == GameEnum.COD_WW2) {
            model.addAttribute("isCashAndWW2", true);
        } else {
            model.addAttribute("isCashAndWW2", false);
        }

        imageValidator.validateContentTypeAndDimensionForImage(file, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("team", team);
            model.addAttribute("user", user);
            return "teams/edit";
        } else {
            try {
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                //Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
                //Files.write(path, bytes);
                String displayUrl = cloudMediaIWS.updateTeamProfilePicture(teamPk, file.getContentType(), bytes);
                model.addAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "' as your team profile picture.");
//                redirectAttributes.addFlashAttribute("message",  "You successfully uploaded '" + file.getOriginalFilename() + "'");
                //redirectAttributes.addFlashAttribute("image",displayUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("team", team);
        model.addAttribute("user", user);
        return "teams/edit";

    }

    @RequestMapping(value = "/uploadStatus", method = RequestMethod.GET)
    public String uploadStatus() {
        return "upload/upload-status";
    }

}
