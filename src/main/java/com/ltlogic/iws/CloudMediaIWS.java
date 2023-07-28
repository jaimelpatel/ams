/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.service.core.CloudMediaService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Bishistha
 */
@Service
public class CloudMediaIWS {
    
    @Autowired
    private CloudMediaService cloudMediaService;
    
    /* Uploads profile pic for a user and returns the URL that can be used to display the image*/
    public String updateUserProfilePicture(long userPk, String contentType, byte[] fileContent) throws Exception {
        String displayUrl = cloudMediaService.updateUserProfilePicture(userPk, contentType, fileContent);
        return displayUrl;
    }
   
    
    /* Uploads profile pic for a team and returns the URL that can be used to display the image*/
    public String updateTeamProfilePicture(long teamPk, String contentType, byte[] fileContent) throws Exception {
        String displayUrl = cloudMediaService.updateTeamProfilePicture(teamPk, contentType, fileContent);
        return displayUrl;
    }
    
    /* Gets the URL that is used to display profile picture of a user*/
    public String getProfilePicDisplyUrlorUserPk(long userPk) throws Exception{
        return cloudMediaService.findProfilePicDisplayUrlForUserPk(userPk);
    }
}
