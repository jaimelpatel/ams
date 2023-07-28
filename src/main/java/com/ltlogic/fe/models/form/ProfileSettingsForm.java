/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import autovalue.shaded.org.apache.commons.lang.StringUtils;
import com.ltlogic.db.entity.User;
import javax.validation.constraints.Pattern;

/**
 *
 * @author raymond
 */
public class ProfileSettingsForm {
//    protected String playStation4Name;
//    protected String xboxOneGamerTag;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]$", message = "Twitch URL may only contain letters, digits, underscores, dashes and dots.")
    protected String twitchUrl;
    @Pattern(regexp = "^[A-Za-z0-9+_.-]$", message = "Youtube URL may only contain letters, digits, underscores, dashes and dots.")
    protected String youtubeUrl;
    @Pattern(regexp = "^[A-Za-z0-9+_.-]$", message = "Twitter URL may only contain letters, digits, underscores, dashes and dots.")
    protected String twitterUrl;
    @Pattern(regexp = "^[A-Za-z0-9+_.-]$", message = "Facebook URL may only contain letters, digits, underscores, dashes and dots.")
    protected String facebookUrl;

//    /**
//     * @return the playStation4Name
//     */
//    public String getPlayStation4Name() {
//        return playStation4Name;
//    }
//
//    /**
//     * @param playStation4Name the playStation4Name to set
//     */
//    public void setPlayStation4Name(String playStation4Name) {
//        this.playStation4Name = playStation4Name;
//    }
//
//    /**
//     * @return the xboxOneGamerTag
//     */
//    public String getXboxOneGamerTag() {
//        return xboxOneGamerTag;
//    }
//
//    /**
//     * @param xboxOneGamerTag the xboxOneGamerTag to set
//     */
//    public void setXboxOneGamerTag(String xboxOneGamerTag) {
//        this.xboxOneGamerTag = xboxOneGamerTag;
//    }
    /**
     * @return the twitchUrl
     */
    public String getTwitchUrl() {
        return twitchUrl;
    }

    /**
     * @param twitchUrl the twitchUrl to set
     */
    public void setTwitchUrl(String twitchUrl) {
        this.twitchUrl = twitchUrl;
    }

    /**
     * @return the youtubeUrl
     */
    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    /**
     * @param youtubeUrl the youtubeUrl to set
     */
    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    /**
     * @return the twitterUrl
     */
    public String getTwitterUrl() {
        return twitterUrl;
    }

    /**
     * @param twitterUrl the twitterUrl to set
     */
    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    /**
     * @return the facebookUrl
     */
    public String getFacebookUrl() {
        return facebookUrl;
    }

    /**
     * @param facebookUrl the facebookUrl to set
     */
    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public static User toUserEntity(ProfileSettingsForm formData, User user) {

        if (formData != null && user != null) {
            if (!StringUtils.isBlank(formData.getFacebookUrl())) {
                if (!formData.getFacebookUrl().contains("http") && !formData.getFacebookUrl().contains("https")) {
                    String newUrl = "http://" + formData.getFacebookUrl();
                    user.getUserInfo().setFacebookUrl(newUrl);
                } else {
                    user.getUserInfo().setFacebookUrl(formData.getFacebookUrl());
                }
            }

            if (!StringUtils.isBlank(formData.getYoutubeUrl())) {
                if (!formData.getYoutubeUrl().contains("http") && !formData.getYoutubeUrl().contains("https")) {
                    String newUrl = "http://" + formData.getYoutubeUrl();
                    user.getUserInfo().setYoutubeUrl(newUrl);
                } else {
                    user.getUserInfo().setYoutubeUrl(formData.getYoutubeUrl());
                }
            }

            if (!StringUtils.isBlank(formData.getTwitchUrl())) {
                if (!formData.getTwitchUrl().contains("http") && !formData.getTwitchUrl().contains("https")) {
                    String newUrl = "http://" + formData.getTwitchUrl();
                    user.getUserInfo().setTwitchUrl(newUrl);
                } else {
                    user.getUserInfo().setTwitchUrl(formData.getTwitchUrl());
                }
            }

            if (!StringUtils.isBlank(formData.getTwitterUrl())) {
                if (!formData.getTwitterUrl().contains("http") && !formData.getTwitterUrl().contains("https")) {
                    String newUrl = "http://" + formData.getTwitterUrl();
                    user.getUserInfo().setTwitterUrl(newUrl);
                } else {
                    user.getUserInfo().setTwitterUrl(formData.getTwitterUrl());
                }
            }
//            user.getUserInfo().setXboxOneGamerTag(formData.getXboxOneGamerTag());
//            user.getUserInfo().setPlayStation4Name(formData.getPlayStation4Name());
        }

        return user;
    }

    public static ProfileSettingsForm fromUserEntityToProfileSettingsForm(User user) {
        ProfileSettingsForm form = new ProfileSettingsForm();

        if (user != null) {
            form.setFacebookUrl(user.getUserInfo().getFacebookUrl());
//            form.setXboxOneGamerTag(user.getUserInfo().getXboxOneGamerTag());
//            form.setPlayStation4Name(user.getUserInfo().getPlayStation4Name());
            form.setTwitchUrl(user.getUserInfo().getTwitchUrl());
            form.setYoutubeUrl(user.getUserInfo().getYoutubeUrl());
            form.setTwitterUrl(user.getUserInfo().getTwitterUrl());
        }

        return form;
    }
}
