/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.pojo.challonge;

/**
 *
 * @author Bishistha
 */
public class ChallongeMatchPojo {

    private int attachment_count;
    private String created_at;
    private long group_id;
    private boolean has_attachment;
    private long id;
    private String identifier;
    private String location;
    private long loser_id;
    private long player1_id;
    private boolean player1_is_prereq_match_loser;
    private long player1_prereq_match_id;
    private int player1_votes;
    private long player2_id;
    private boolean player2_is_prereq_match_loser;
    private long player2_prereq_match_id;
    private int player2_votes;
    private int round;
    private String scheduled_time;
    private String started_at;
    private String state; // pending, open, closed
    private long tournament_id;
    private String underway_at; // could be time
    private String updated_at;
    private long winner_id;
    private String prerequisite_match_ids_csv;
    private String scores_csv;

    public int getAttachment_count() {
        return attachment_count;
    }

    public void setAttachment_count(int attachment_count) {
        this.attachment_count = attachment_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public boolean isHas_attachment() {
        return has_attachment;
    }

    public void setHas_attachment(boolean has_attachment) {
        this.has_attachment = has_attachment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getLoser_id() {
        return loser_id;
    }

    public void setLoser_id(long loser_id) {
        this.loser_id = loser_id;
    }

    public long getPlayer1_id() {
        return player1_id;
    }

    public void setPlayer1_id(long player1_id) {
        this.player1_id = player1_id;
    }

    public boolean isPlayer1_is_prereq_match_loser() {
        return player1_is_prereq_match_loser;
    }

    public void setPlayer1_is_prereq_match_loser(boolean player1_is_prereq_match_loser) {
        this.player1_is_prereq_match_loser = player1_is_prereq_match_loser;
    }

    public long getPlayer1_prereq_match_id() {
        return player1_prereq_match_id;
    }

    public void setPlayer1_prereq_match_id(long player1_prereq_match_id) {
        this.player1_prereq_match_id = player1_prereq_match_id;
    }

    public int getPlayer1_votes() {
        return player1_votes;
    }

    public void setPlayer1_votes(int player1_votes) {
        this.player1_votes = player1_votes;
    }

    public long getPlayer2_id() {
        return player2_id;
    }

    public void setPlayer2_id(long player2_id) {
        this.player2_id = player2_id;
    }

    public boolean isPlayer2_is_prereq_match_loser() {
        return player2_is_prereq_match_loser;
    }

    public void setPlayer2_is_prereq_match_loser(boolean player2_is_prereq_match_loser) {
        this.player2_is_prereq_match_loser = player2_is_prereq_match_loser;
    }

    public long getPlayer2_prereq_match_id() {
        return player2_prereq_match_id;
    }

    public void setPlayer2_prereq_match_id(long player2_prereq_match_id) {
        this.player2_prereq_match_id = player2_prereq_match_id;
    }

    public int getPlayer2_votes() {
        return player2_votes;
    }

    public void setPlayer2_votes(int player2_votes) {
        this.player2_votes = player2_votes;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getScheduled_time() {
        return scheduled_time;
    }

    public void setScheduled_time(String scheduled_time) {
        this.scheduled_time = scheduled_time;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getTournament_id() {
        return tournament_id;
    }

    public void setTournament_id(long tournament_id) {
        this.tournament_id = tournament_id;
    }

    public String getUnderway_at() {
        return underway_at;
    }

    public void setUnderway_at(String underway_at) {
        this.underway_at = underway_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public long getWinner_id() {
        return winner_id;
    }

    public void setWinner_id(long winner_id) {
        this.winner_id = winner_id;
    }

    public String getPrerequisite_match_ids_csv() {
        return prerequisite_match_ids_csv;
    }

    public void setPrerequisite_match_ids_csv(String prerequisite_match_ids_csv) {
        this.prerequisite_match_ids_csv = prerequisite_match_ids_csv;
    }

    public String getScores_csv() {
        return scores_csv;
    }

    public void setScores_csv(String scores_csv) {
        this.scores_csv = scores_csv;
    }

}
