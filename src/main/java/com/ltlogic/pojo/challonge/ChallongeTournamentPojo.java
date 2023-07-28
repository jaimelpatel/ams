/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.pojo.challonge;

import java.math.BigDecimal;
import java.util.Arrays;
import javax.persistence.Embeddable;

/**
 *
 * @author jaimel
 */
@Embeddable
public class ChallongeTournamentPojo {
    
    private boolean accept_attachments;
    private boolean allow_participant_match_reporting;
    private boolean anonymous_voting;
    private String category;
    private String check_in_duration;
    private String completed_at;
    private String created_at;
    private boolean created_by_api;
    private boolean credit_capped;
    private String description;
    private int game_id;
    private boolean group_stages_enabled;
    private boolean hide_forum;
    private boolean hide_seeds;
    private boolean hold_third_place_match;
    private long id;
    private int max_predictions_per_user;
    private String name;
    private boolean notify_users_when_matches_open;
    private boolean notify_users_when_the_tournament_ends;
    private boolean open_signup;
    private int participants_count;
    private int prediction_method;
    private String predictions_opened_at;
    private String Private;
    private int progress_meter;
    private BigDecimal pts_for_bye;
    private BigDecimal pts_for_game_tie;
    private BigDecimal pts_for_game_win;
    private BigDecimal pts_for_match_tie;
    private BigDecimal pts_for_match_win;
    private boolean quick_advance;
    private String ranked_by;
    private boolean require_score_agreement;
    private BigDecimal rr_pts_for_game_tie;
    private BigDecimal rr_pts_for_game_win;
    private BigDecimal rr_pts_for_match_tie;
    private BigDecimal rr_pts_for_match_win;
    private boolean sequential_pairings;
    private boolean show_rounds;
    private String signup_cap;
    private String start_at;
    private String started_at;
    private String started_checking_in_at;
    private String state;
    private int swiss_rounds;
    private boolean teams;
    private String tie_breaks[];
    private String tournament_type;
    private String updated_at;
    private String url;
    private String description_source;
    private String subdomain;
    private String full_challonge_url;
    private String live_image_url;
    private String sign_up_url;
    private boolean review_before_finalizing;
    private boolean accepting_predictions;
    private boolean participants_locked;
    private boolean game_name;
    private boolean participants_swappable;
    private boolean team_convertable;
    private boolean group_stages_were_started;

    public boolean isAccept_attachments() {
        return accept_attachments;
    }

    public void setAccept_attachments(boolean accept_attachments) {
        this.accept_attachments = accept_attachments;
    }

    public boolean isAllow_participant_match_reporting() {
        return allow_participant_match_reporting;
    }

    public void setAllow_participant_match_reporting(boolean allow_participant_match_reporting) {
        this.allow_participant_match_reporting = allow_participant_match_reporting;
    }

    public boolean isAnonymous_voting() {
        return anonymous_voting;
    }

    public void setAnonymous_voting(boolean anonymous_voting) {
        this.anonymous_voting = anonymous_voting;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCheck_in_duration() {
        return check_in_duration;
    }

    public void setCheck_in_duration(String check_in_duration) {
        this.check_in_duration = check_in_duration;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isCreated_by_api() {
        return created_by_api;
    }

    public void setCreated_by_api(boolean created_by_api) {
        this.created_by_api = created_by_api;
    }

    public boolean isCredit_capped() {
        return credit_capped;
    }

    public void setCredit_capped(boolean credit_capped) {
        this.credit_capped = credit_capped;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public boolean isGroup_stages_enabled() {
        return group_stages_enabled;
    }

    public void setGroup_stages_enabled(boolean group_stages_enabled) {
        this.group_stages_enabled = group_stages_enabled;
    }

    public boolean isHide_forum() {
        return hide_forum;
    }

    public void setHide_forum(boolean hide_forum) {
        this.hide_forum = hide_forum;
    }

    public boolean isHide_seeds() {
        return hide_seeds;
    }

    public void setHide_seeds(boolean hide_seeds) {
        this.hide_seeds = hide_seeds;
    }

    public boolean isHold_third_place_match() {
        return hold_third_place_match;
    }

    public void setHold_third_place_match(boolean hold_third_place_match) {
        this.hold_third_place_match = hold_third_place_match;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMax_predictions_per_user() {
        return max_predictions_per_user;
    }

    public void setMax_predictions_per_user(int max_predictions_per_user) {
        this.max_predictions_per_user = max_predictions_per_user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNotify_users_when_matches_open() {
        return notify_users_when_matches_open;
    }

    public void setNotify_users_when_matches_open(boolean notify_users_when_matches_open) {
        this.notify_users_when_matches_open = notify_users_when_matches_open;
    }

    public boolean isNotify_users_when_the_tournament_ends() {
        return notify_users_when_the_tournament_ends;
    }

    public void setNotify_users_when_the_tournament_ends(boolean notify_users_when_the_tournament_ends) {
        this.notify_users_when_the_tournament_ends = notify_users_when_the_tournament_ends;
    }

    public boolean isOpen_signup() {
        return open_signup;
    }

    public void setOpen_signup(boolean open_signup) {
        this.open_signup = open_signup;
    }

    public int getParticipants_count() {
        return participants_count;
    }

    public void setParticipants_count(int participants_count) {
        this.participants_count = participants_count;
    }

    public int getPrediction_method() {
        return prediction_method;
    }

    public void setPrediction_method(int prediction_method) {
        this.prediction_method = prediction_method;
    }

    public String getPredictions_opened_at() {
        return predictions_opened_at;
    }

    public void setPredictions_opened_at(String predictions_opened_at) {
        this.predictions_opened_at = predictions_opened_at;
    }

    public String getPrivate() {
        return Private;
    }

    public void setPrivate(String Private) {
        this.Private = Private;
    }

    public int getProgress_meter() {
        return progress_meter;
    }

    public void setProgress_meter(int progress_meter) {
        this.progress_meter = progress_meter;
    }

    public BigDecimal getPts_for_bye() {
        return pts_for_bye;
    }

    public void setPts_for_bye(BigDecimal pts_for_bye) {
        this.pts_for_bye = pts_for_bye;
    }

    public BigDecimal getPts_for_game_tie() {
        return pts_for_game_tie;
    }

    public void setPts_for_game_tie(BigDecimal pts_for_game_tie) {
        this.pts_for_game_tie = pts_for_game_tie;
    }

    public BigDecimal getPts_for_game_win() {
        return pts_for_game_win;
    }

    public void setPts_for_game_win(BigDecimal pts_for_game_win) {
        this.pts_for_game_win = pts_for_game_win;
    }

    public BigDecimal getPts_for_match_tie() {
        return pts_for_match_tie;
    }

    public void setPts_for_match_tie(BigDecimal pts_for_match_tie) {
        this.pts_for_match_tie = pts_for_match_tie;
    }

    public BigDecimal getPts_for_match_win() {
        return pts_for_match_win;
    }

    public void setPts_for_match_win(BigDecimal pts_for_match_win) {
        this.pts_for_match_win = pts_for_match_win;
    }

    public boolean isQuick_advance() {
        return quick_advance;
    }

    public void setQuick_advance(boolean quick_advance) {
        this.quick_advance = quick_advance;
    }

    public String getRanked_by() {
        return ranked_by;
    }

    public void setRanked_by(String ranked_by) {
        this.ranked_by = ranked_by;
    }

    public boolean isRequire_score_agreement() {
        return require_score_agreement;
    }

    public void setRequire_score_agreement(boolean require_score_agreement) {
        this.require_score_agreement = require_score_agreement;
    }

    public BigDecimal getRr_pts_for_game_tie() {
        return rr_pts_for_game_tie;
    }

    public void setRr_pts_for_game_tie(BigDecimal rr_pts_for_game_tie) {
        this.rr_pts_for_game_tie = rr_pts_for_game_tie;
    }

    public BigDecimal getRr_pts_for_game_win() {
        return rr_pts_for_game_win;
    }

    public void setRr_pts_for_game_win(BigDecimal rr_pts_for_game_win) {
        this.rr_pts_for_game_win = rr_pts_for_game_win;
    }

    public BigDecimal getRr_pts_for_match_tie() {
        return rr_pts_for_match_tie;
    }

    public void setRr_pts_for_match_tie(BigDecimal rr_pts_for_match_tie) {
        this.rr_pts_for_match_tie = rr_pts_for_match_tie;
    }

    public BigDecimal getRr_pts_for_match_win() {
        return rr_pts_for_match_win;
    }

    public void setRr_pts_for_match_win(BigDecimal rr_pts_for_match_win) {
        this.rr_pts_for_match_win = rr_pts_for_match_win;
    }

    public boolean isSequential_pairings() {
        return sequential_pairings;
    }

    public void setSequential_pairings(boolean sequential_pairings) {
        this.sequential_pairings = sequential_pairings;
    }

    public boolean isShow_rounds() {
        return show_rounds;
    }

    public void setShow_rounds(boolean show_rounds) {
        this.show_rounds = show_rounds;
    }

    public String getSignup_cap() {
        return signup_cap;
    }

    public void setSignup_cap(String signup_cap) {
        this.signup_cap = signup_cap;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getStarted_checking_in_at() {
        return started_checking_in_at;
    }

    public void setStarted_checking_in_at(String started_checking_in_at) {
        this.started_checking_in_at = started_checking_in_at;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getSwiss_rounds() {
        return swiss_rounds;
    }

    public void setSwiss_rounds(int swiss_rounds) {
        this.swiss_rounds = swiss_rounds;
    }

    public boolean isTeams() {
        return teams;
    }

    public void setTeams(boolean teams) {
        this.teams = teams;
    }

    public String[] getTie_breaks() {
        return tie_breaks;
    }

    public void setTie_breaks(String[] tie_breaks) {
        this.tie_breaks = tie_breaks;
    }


    public String getTournament_type() {
        return tournament_type;
    }

    public void setTournament_type(String tournament_type) {
        this.tournament_type = tournament_type;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription_source() {
        return description_source;
    }

    public void setDescription_source(String description_source) {
        this.description_source = description_source;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getFull_challonge_url() {
        return full_challonge_url;
    }

    public void setFull_challonge_url(String full_challonge_url) {
        this.full_challonge_url = full_challonge_url;
    }

    public String getLive_image_url() {
        return live_image_url;
    }

    public void setLive_image_url(String live_image_url) {
        this.live_image_url = live_image_url;
    }

    public String getSign_up_url() {
        return sign_up_url;
    }

    public void setSign_up_url(String sign_up_url) {
        this.sign_up_url = sign_up_url;
    }

    public boolean isReview_before_finalizing() {
        return review_before_finalizing;
    }

    public void setReview_before_finalizing(boolean review_before_finalizing) {
        this.review_before_finalizing = review_before_finalizing;
    }

    public boolean isAccepting_predictions() {
        return accepting_predictions;
    }

    public void setAccepting_predictions(boolean accepting_predictions) {
        this.accepting_predictions = accepting_predictions;
    }

    public boolean isParticipants_locked() {
        return participants_locked;
    }

    public void setParticipants_locked(boolean participants_locked) {
        this.participants_locked = participants_locked;
    }

    public boolean isGame_name() {
        return game_name;
    }

    public void setGame_name(boolean game_name) {
        this.game_name = game_name;
    }

    public boolean isParticipants_swappable() {
        return participants_swappable;
    }

    public void setParticipants_swappable(boolean participants_swappable) {
        this.participants_swappable = participants_swappable;
    }

    public boolean isTeam_convertable() {
        return team_convertable;
    }

    public void setTeam_convertable(boolean team_convertable) {
        this.team_convertable = team_convertable;
    }

    public boolean isGroup_stages_were_started() {
        return group_stages_were_started;
    }

    public void setGroup_stages_were_started(boolean group_stages_were_started) {
        this.group_stages_were_started = group_stages_were_started;
    }
    
    
  
}
