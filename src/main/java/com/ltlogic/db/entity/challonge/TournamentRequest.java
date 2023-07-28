/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.challonge;

import java.math.BigDecimal;

/**
 * 
 * @author Hoang
 */
public class TournamentRequest {

    private String name;
    private Boolean allow_participant_match_reporting;
    private Boolean quick_advance;
    private String tournament_type;
    private String url;
    private String subdomain;
    private String description;
    private Boolean open_signup;
    private Boolean hold_third_place_match;
    private BigDecimal pts_for_match_win;
    private BigDecimal pts_for_match_tie;
    private BigDecimal pts_for_game_win;
    private BigDecimal pts_for_game_tie;
    private BigDecimal pts_for_bye;
    private Integer swiss_rounds;
    private String rankedBy;
    private BigDecimal rr_pts_for_match_win;
    private BigDecimal rr_pts_for_match_tie;
    private BigDecimal rr_pts_for_game_win;
    private BigDecimal rr_pts_for_game_ti;
    private Boolean accept_attachments;
    private Boolean hide_forum;
    private Boolean show_rounds;
    private Boolean Private;
    private Boolean notify_users_when_matches_open;
    private Boolean notify_users_when_the_tournament_ends;
    private Boolean sequential_pairings;
    private Integer signup_cap;
    private String start_at;
    private Integer check_in_duration;
    private String grand_finals_modifier;

    public Boolean getQuick_advance() {
        return quick_advance;
    }

    public void setQuick_advance(Boolean quick_advance) {
        this.quick_advance = quick_advance;
    }

    public Boolean getAllow_participant_match_reporting() {
        return allow_participant_match_reporting;
    }

    public void setAllow_participant_match_reporting(Boolean allow_participant_match_reporting) {
        this.allow_participant_match_reporting = allow_participant_match_reporting;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTournament_type() {
        return tournament_type;
    }

    public void setTournament_type(String tournament_type) {
        this.tournament_type = tournament_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOpen_signup() {
        return open_signup;
    }

    public void setOpen_signup(Boolean open_signup) {
        this.open_signup = open_signup;
    }

    public Boolean getHold_third_place_match() {
        return hold_third_place_match;
    }

    public void setHold_third_place_match(Boolean hold_third_place_match) {
        this.hold_third_place_match = hold_third_place_match;
    }

    public BigDecimal getPts_for_match_win() {
        return pts_for_match_win;
    }

    public void setPts_for_match_win(BigDecimal pts_for_match_win) {
        this.pts_for_match_win = pts_for_match_win;
    }

    public BigDecimal getPts_for_match_tie() {
        return pts_for_match_tie;
    }

    public void setPts_for_match_tie(BigDecimal pts_for_match_tie) {
        this.pts_for_match_tie = pts_for_match_tie;
    }

    public BigDecimal getPts_for_game_win() {
        return pts_for_game_win;
    }

    public void setPts_for_game_win(BigDecimal pts_for_game_win) {
        this.pts_for_game_win = pts_for_game_win;
    }

    public BigDecimal getPts_for_game_tie() {
        return pts_for_game_tie;
    }

    public void setPts_for_game_tie(BigDecimal pts_for_game_tie) {
        this.pts_for_game_tie = pts_for_game_tie;
    }

    public BigDecimal getPts_for_bye() {
        return pts_for_bye;
    }

    public void setPts_for_bye(BigDecimal pts_for_bye) {
        this.pts_for_bye = pts_for_bye;
    }

    public Integer getSwiss_rounds() {
        return swiss_rounds;
    }

    public void setSwiss_rounds(Integer swiss_rounds) {
        this.swiss_rounds = swiss_rounds;
    }

    public String getRankedBy() {
        return rankedBy;
    }

    public void setRankedBy(String rankedBy) {
        this.rankedBy = rankedBy;
    }

    public BigDecimal getRr_pts_for_match_win() {
        return rr_pts_for_match_win;
    }

    public void setRr_pts_for_match_win(BigDecimal rr_pts_for_match_win) {
        this.rr_pts_for_match_win = rr_pts_for_match_win;
    }

    public BigDecimal getRr_pts_for_match_tie() {
        return rr_pts_for_match_tie;
    }

    public void setRr_pts_for_match_tie(BigDecimal rr_pts_for_match_tie) {
        this.rr_pts_for_match_tie = rr_pts_for_match_tie;
    }

    public BigDecimal getRr_pts_for_game_win() {
        return rr_pts_for_game_win;
    }

    public void setRr_pts_for_game_win(BigDecimal rr_pts_for_game_win) {
        this.rr_pts_for_game_win = rr_pts_for_game_win;
    }

    public BigDecimal getRr_pts_for_game_ti() {
        return rr_pts_for_game_ti;
    }

    public void setRr_pts_for_game_ti(BigDecimal rr_pts_for_game_ti) {
        this.rr_pts_for_game_ti = rr_pts_for_game_ti;
    }

    public Boolean getAccept_attachments() {
        return accept_attachments;
    }

    public void setAccept_attachments(Boolean accept_attachments) {
        this.accept_attachments = accept_attachments;
    }

    public Boolean getHide_forum() {
        return hide_forum;
    }

    public void setHide_forum(Boolean hide_forum) {
        this.hide_forum = hide_forum;
    }

    public Boolean getShow_rounds() {
        return show_rounds;
    }

    public void setShow_rounds(Boolean show_rounds) {
        this.show_rounds = show_rounds;
    }

    public Boolean getPrivate() {
        return Private;
    }

    public void setPrivate(Boolean Private) {
        this.Private = Private;
    }

    public Boolean getNotify_users_when_matches_open() {
        return notify_users_when_matches_open;
    }

    public void setNotify_users_when_matches_open(Boolean notify_users_when_matches_open) {
        this.notify_users_when_matches_open = notify_users_when_matches_open;
    }

    public Boolean getNotify_users_when_the_tournament_ends() {
        return notify_users_when_the_tournament_ends;
    }

    public void setNotify_users_when_the_tournament_ends(Boolean notify_users_when_the_tournament_ends) {
        this.notify_users_when_the_tournament_ends = notify_users_when_the_tournament_ends;
    }

    public Boolean getSequential_pairings() {
        return sequential_pairings;
    }

    public void setSequential_pairings(Boolean sequential_pairings) {
        this.sequential_pairings = sequential_pairings;
    }

    public Integer getSignup_cap() {
        return signup_cap;
    }

    public void setSignup_cap(Integer signup_cap) {
        this.signup_cap = signup_cap;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public Integer getCheck_in_duration() {
        return check_in_duration;
    }

    public void setCheck_in_duration(Integer check_in_duration) {
        this.check_in_duration = check_in_duration;
    }

    public String getGrand_finals_modifier() {
        return grand_finals_modifier;
    }

    public void setGrand_finals_modifier(String grand_finals_modifier) {
        this.grand_finals_modifier = grand_finals_modifier;
    }

    
          
} 
