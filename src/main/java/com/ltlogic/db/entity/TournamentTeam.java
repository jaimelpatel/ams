/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity;

import com.ltlogic.db.entity.challonge.ParticipantResponse;
import com.ltlogic.db.entity.challonge.TournamentResponse;
import com.ltlogic.db.superentity.SuperEntity;
import com.ltlogic.pojo.TeamPojo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.envers.NotAudited;

/**
 * 
 * @author Hoang
 */

@NamedQueries({
    @NamedQuery(
            name = "TournamentTeam.getAllEligibleTournamentTeamsForTournament",
            query = "SELECT t FROM TournamentTeam t WHERE t.tournament.pk = :pk AND t.teamCancelled IS false AND t.allPlayersAccepted IS true ORDER BY t.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "TournamentTeam.getAllTournamentTeamsForTournament",
            query = "SELECT t FROM TournamentTeam t WHERE t.tournament.pk = :pk AND t.teamCancelled IS false ORDER BY t.rowCreatedTimestamp DESC"
    ),
    //Needs testing -- doesnt work
//    @NamedQuery(
//            name = "TournamentTeam.findTournamentTeamByTournamentPkAndUserPk",
//            query = "SELECT t FROM TournamentTeam t WHERE t.tournament.pk = :tournamentPk AND :userPk IN t.pksOfTeamMembersPlaying"
//    ),
    @NamedQuery(
            name = "TournamentTeam.getAllTournamentTeamsForTeam",
            query = "SELECT t FROM TournamentTeam t where t.team.pk = :pk AND t.teamCancelled IS false"
    ),
    @NamedQuery(
            name = "TournamentTeam.getAllTournamentTeamsForTeamByTournamentPendingStatus",
            query = "SELECT t FROM TournamentTeam t where t.team.pk = :teamPk AND t.teamCancelled IS false AND t.tournament.tournamentInfo.tournamentStatus = :pendingStatus"
    ),
        @NamedQuery(
            name = "TournamentTeam.getAllTournamentTeamsForTeamByTournamentActiveStatus",
            query = "SELECT t FROM TournamentTeam t where t.team.pk = :teamPk AND t.teamCancelled IS false AND t.tournament.tournamentInfo.tournamentStatus = :activeStatus"
    ),
        @NamedQuery(
            name = "TournamentTeam.getTournamentTeamByNameAndTournament",
            query = "SELECT t FROM TournamentTeam t where t.team.teamPojo.teamName = :teamName AND t.tournament = :tournament AND t.teamCancelled IS false"
    )
        
})

@Entity
public class TournamentTeam extends SuperEntity{
    
    @ElementCollection
    @CollectionTable(name = "list_Of_Pks_Of_Team_Members_Playing", joinColumns = @JoinColumn(name = "tournament_pk"))
    @Column(name = "pks_Of_Team_Members_Playing")
    
    private List<Long> pksOfTeamMembersPlaying = new ArrayList<>();
    
    private int numOfPlayers;
    
    private int numOfPlayersNeedingToAccept;
    
    private int tournamentSeed;
    
    private boolean allPlayersAccepted;
    
    private boolean isEligible = false;
    
    private boolean teamCancelled;
     
    private String name;
    
    @ElementCollection
    @CollectionTable(name = "list_Of_Pks_Of_Players_Who_Accepted_Tournament_Invite", joinColumns = @JoinColumn(name = "tournament_pk"))
    @Column(name = "pks_Of_Players_Who_Accepted_Tournament_Invite")
    
    private List<Long> pksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam = new ArrayList<>();
  
    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_pk", referencedColumnName = "pk")
    private Tournament tournament;
    
    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_pk", referencedColumnName = "pk")
    private Team team;
    
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tournamentTeam")
    private Set<TournamentInvite> tournamentInvites = new HashSet<>(0);
    
    @OneToOne(mappedBy = "tournamentTeam", fetch = FetchType.LAZY)
    private ParticipantResponse participantResponse;

    public List<Long> getPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam() {
        return pksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam;
    }

    public void setPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam(List<Long> pksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam) {
        this.pksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam = pksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public ParticipantResponse getParticipantResponse() {
        return participantResponse;
    }

    public void setParticipantResponse(ParticipantResponse participantResponse) {
        this.participantResponse = participantResponse;
    }
    
    public boolean isIsEligible() {
        return isEligible;
    }

    public void setIsEligible(boolean isEligible) {
        this.isEligible = isEligible;
    }

    public List<Long> getPksOfTeamMembersPlaying() {
        return pksOfTeamMembersPlaying;
    }

    public void setPksOfTeamMembersPlaying(List<Long> pksOfTeamMembersPlaying) {
        this.pksOfTeamMembersPlaying = pksOfTeamMembersPlaying;
    }

    public boolean isTeamCancelled() {
        return teamCancelled;
    }

    public void setTeamCancelled(boolean teamCancelled) {
        this.teamCancelled = teamCancelled;
    }
    
    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Set<TournamentInvite> getTournamentInvites() {
        return tournamentInvites;
    }

    public void setTournamentInvites(Set<TournamentInvite> tournamentInvites) {
        this.tournamentInvites = tournamentInvites;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayersNeedingToAccept() {
        return numOfPlayersNeedingToAccept;
    }

    public void setNumOfPlayersNeedingToAccept(int numOfPlayersNeedingToAccept) {
        this.numOfPlayersNeedingToAccept = numOfPlayersNeedingToAccept;
    }

    public boolean isAllPlayersAccepted() {
        return allPlayersAccepted;
    }

    public void setAllPlayersAccepted(boolean allPlayersAccepted) {
        this.allPlayersAccepted = allPlayersAccepted;
    }

    public int getTournamentSeed() {
        return tournamentSeed;
    }

    public void setTournamentSeed(int tournamentSeed) {
        this.tournamentSeed = tournamentSeed;
    }
}
