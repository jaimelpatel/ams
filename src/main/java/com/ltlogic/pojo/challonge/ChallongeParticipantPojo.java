/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.pojo.challonge;

import javax.persistence.Embeddable;

/**
 * 
 * @author Hoang
 */
@Embeddable
public class ChallongeParticipantPojo {
    
    private boolean active;
    private String checked_in_at;
    private String created_at;
    private String final_rank;
    private String group_id;
    private String icon;
    private long id;
    private String invitation_id; 
    private String invite_email;
    private String misc;
    private String name;
    private boolean on_waiting_list;
    private int seed;
    private long tournament_id;
    private String updated_at;
    private String challonge_username;
    private String challonge_email_address_verified;
    private boolean removable;
    private boolean participatable_or_invitation_attached;
    private boolean confirm_remove;
    private boolean invitation_pending;
    private String display_name_with_invitation_email_address;
    private String email_hash;
    private String username;
    private String attached_participatable_portrait_url;
    private boolean can_check_in;
    private boolean checked_in;
    private boolean reactivatable;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getChecked_in_at() {
        return checked_in_at;
    }

    public void setChecked_in_at(String checked_in_at) {
        this.checked_in_at = checked_in_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFinal_rank() {
        return final_rank;
    }

    public void setFinal_rank(String final_rank) {
        this.final_rank = final_rank;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInvitation_id() {
        return invitation_id;
    }

    public void setInvitation_id(String invitation_id) {
        this.invitation_id = invitation_id;
    }

    public String getInvite_email() {
        return invite_email;
    }

    public void setInvite_email(String invite_email) {
        this.invite_email = invite_email;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOn_waiting_list() {
        return on_waiting_list;
    }

    public void setOn_waiting_list(boolean on_waiting_list) {
        this.on_waiting_list = on_waiting_list;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public long getTournament_id() {
        return tournament_id;
    }

    public void setTournament_id(long tournament_id) {
        this.tournament_id = tournament_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getChallonge_username() {
        return challonge_username;
    }

    public void setChallonge_username(String challonge_username) {
        this.challonge_username = challonge_username;
    }

    public String getChallonge_email_address_verified() {
        return challonge_email_address_verified;
    }

    public void setChallonge_email_address_verified(String challonge_email_address_verified) {
        this.challonge_email_address_verified = challonge_email_address_verified;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isParticipatable_or_invitation_attached() {
        return participatable_or_invitation_attached;
    }

    public void setParticipatable_or_invitation_attached(boolean participatable_or_invitation_attached) {
        this.participatable_or_invitation_attached = participatable_or_invitation_attached;
    }

    public boolean isConfirm_remove() {
        return confirm_remove;
    }

    public void setConfirm_remove(boolean confirm_remove) {
        this.confirm_remove = confirm_remove;
    }

    public boolean isInvitation_pending() {
        return invitation_pending;
    }

    public void setInvitation_pending(boolean invitation_pending) {
        this.invitation_pending = invitation_pending;
    }

    public String getDisplay_name_with_invitation_email_address() {
        return display_name_with_invitation_email_address;
    }

    public void setDisplay_name_with_invitation_email_address(String display_name_with_invitation_email_address) {
        this.display_name_with_invitation_email_address = display_name_with_invitation_email_address;
    }

    public String getEmail_hash() {
        return email_hash;
    }

    public void setEmail_hash(String email_hash) {
        this.email_hash = email_hash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAttached_participatable_portrait_url() {
        return attached_participatable_portrait_url;
    }

    public void setAttached_participatable_portrait_url(String attached_participatable_portrait_url) {
        this.attached_participatable_portrait_url = attached_participatable_portrait_url;
    }

    public boolean isCan_check_in() {
        return can_check_in;
    }

    public void setCan_check_in(boolean can_check_in) {
        this.can_check_in = can_check_in;
    }

    public boolean isChecked_in() {
        return checked_in;
    }

    public void setChecked_in(boolean checked_in) {
        this.checked_in = checked_in;
    }

    public boolean isReactivatable() {
        return reactivatable;
    }

    public void setReactivatable(boolean reactivatable) {
        this.reactivatable = reactivatable;
    }
    
}
