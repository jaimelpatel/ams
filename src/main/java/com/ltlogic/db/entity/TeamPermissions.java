/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.constants.TeamPermissionsEnum;
import com.ltlogic.db.superentity.SuperEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author Jaimel
 */

@NamedQueries({
    @NamedQuery(
            name = "TeamPermissions.getTeamPermissionsByUserPkAndTeamPk",
            query = "SELECT t FROM TeamPermissions t WHERE t.user.pk = :userPk AND t.team.pk = :teamPk"
    )
})
@Entity
public class TeamPermissions extends SuperEntity{
    
//    @ElementCollection
//    @CollectionTable(name="team_permissions_list", joinColumns=@JoinColumn(name="pk"))
//    @Column(name="team_permission")
//    @Enumerated(EnumType.STRING)
//    private List<TeamPermissionsEnum> teamPermissions = new ArrayList<>();

    private boolean hasLeaderPermissions;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", referencedColumnName = "pk")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_pk", referencedColumnName = "pk")
    private Team team;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isHasLeaderPermissions() {
        return hasLeaderPermissions;
    }

    public void setHasLeaderPermissions(boolean hasLeaderPermissions) {
        this.hasLeaderPermissions = hasLeaderPermissions;
    }
    
}
