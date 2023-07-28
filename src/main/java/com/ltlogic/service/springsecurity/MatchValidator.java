/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.springsecurity;

import com.ltlogic.constants.MatchSizeEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.User;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 *
 * @author raymond
 */
@Component
public class MatchValidator {
    public String validateNumberOfUsersPlayingInMatch(Match match, List<User> users, Errors errors){
        if(match != null && users != null) {
            if(null != match.getMatchInfo().getMatchSizeEnum()) {
                switch (match.getMatchInfo().getMatchSizeEnum()) {
                    case SINGLES:
                        if(users.size() != 1)
                            return "You must select 1 members to participate in the tournament.";
                        break;
                    case DOUBLES:
                        if(users.size() != 2)
                            return "You must select 2 members to participate in the tournament.";
                        break;
                    case THREES:
                        if(users.size() != 3)
                            return "You must select 3 members to participate in the tournament.";
                        break;
                    case FOURS:
                        if(users.size() != 4)
                            return "You must select 4 members to participate in the tournament.";
                        break;
                    default:
                        break;
                }
            }
        }

        return null;
    }
}
