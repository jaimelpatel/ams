/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.constants.DisputeStatus;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.NotificationTypeEnum;
import com.ltlogic.db.entity.CloudMedia;
import com.ltlogic.db.entity.Conversation;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Notification;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.ConversationRepository;
import com.ltlogic.db.repository.DisputeRepository;
import com.ltlogic.db.repository.MatchCancellationRepository;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.NotificationRepository;
import com.ltlogic.db.repository.RoleRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.service.common.CommonService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
//import com.ltlogic.service.utility.CloudStorageHelper;
import java.util.List;
import java.util.Set;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hoang
 */
@Service
@Transactional
public class DisputeService {

    @Autowired
    private MatchRepository matchRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private MatchCancellationRepository cancellationRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TeamRepository teamRepo;
    @Autowired
    private DisputeRepository disputeRepo;
    @Autowired
    private CommonService commonService;
    @Autowired
    private NotificationRepository notificationRepo;
    @Autowired
    private ConversationRepository conversationRepository;

    public Dispute findDisputeByPk(long pk) {
        return disputeRepo.findDisputeByPk(pk);
    }

    public Dispute findDisputeByDisputeId(int disputeId) {
        return disputeRepo.findDisputeByDisputeId(disputeId);
    }

    public List<Dispute> getAllMatchDisputesByMatchPk(long matchPk) {
        return disputeRepo.getAllMatchDisputesByMatchPk(matchPk);
    }

    public List<Dispute> getAllOpenMatchDisputesByMatchType(MatchTypeEnum matchType) {
        return disputeRepo.getAllOpenMatchDisputesByMatchType(matchType);
    }

    public Dispute findDisputeForMatchAndTeam(long matchPk, long teamPk) {
        return disputeRepo.findDisputeForMatchAndTeam(matchPk, teamPk);
    }

    public Dispute createDispute(long userPk, long matchPk, long teamPk, String reasonForDispute, List<String> links, boolean isReportMatchDispute) {
        Dispute dispute = new Dispute();
        dispute.setDisputeStatus(DisputeStatus.OPEN);
        boolean doesIdExist = false;
        while (!doesIdExist) {
            int disputeId = commonService.generateRandomId();
            Dispute foundDispute = disputeRepo.findDisputeByDisputeId(disputeId);
            if (foundDispute == null) {
                dispute.setDisputeId(disputeId);
                doesIdExist = true;
            }
        }
        Match match = matchRepo.findMatchByPk(matchPk);
        match.getMatchInfo().setMatchStatus(MatchStatusEnum.DISPUTED);
        User user = userRepo.findByPk(userPk);
        Team team = teamRepo.findTeamByPk(teamPk);
        match.setIsDisputed(true);
        dispute.setReasonForDispute(reasonForDispute);
        if (links != null && !links.isEmpty()) {
            dispute.setLinks(links);
        }
        disputeRepo.persistDispute(dispute);
        associateMatchToDispute(dispute, match);
        associateUserToDispute(dispute, user);

        associateTeamToDispute(dispute, team);
        if (!isReportMatchDispute) {
            setDisputedMatchNotificationForDisputeStarter(user, match, dispute);
        }
        if (match.getPkOfTeamThatCreatedMatch() == teamPk) {
            Dispute dispute1 = disputeRepo.findDisputeForMatchAndTeam(match.getPk(), match.getPkOfTeamThatAcceptedMatch());
            if (!isReportMatchDispute && dispute1 == null) {
                for (long pk : match.getPksOfAcceptorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(pk);
                    setDisputedMatchNotificationForDisputeSecond(u, match);
                }
            }
        } else {
            Dispute dispute2 = disputeRepo.findDisputeForMatchAndTeam(match.getPk(), match.getPkOfTeamThatCreatedMatch());
            if (!isReportMatchDispute && dispute2 == null) {
                for (long pk : match.getPksOfCreatorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(pk);
                    setDisputedMatchNotificationForDisputeSecond(u, match);
                }
            }
        }
        int totalDisputedMatchCount = team.getDisputedMatches() + 1;
        team.setDisputedMatches(totalDisputedMatchCount);

        return dispute;
    }

    private void setTeamDisputePercentage(Team team) {
        int totalMatchCount = matchRepo.findMatchesByTeamPk(team.getPk()).size();
        System.out.println("totalMatchCount: " + totalMatchCount);
        BigDecimal totalMatchCountBd = new BigDecimal(totalMatchCount).setScale(2, RoundingMode.HALF_UP);
        System.out.println("totalMatchCountBd: " + totalMatchCountBd);
        int totalDisputedMatchCount = team.getDisputedMatches() + 1;
        System.out.println("totalDisputedMatchCount: " + totalDisputedMatchCount);
        BigDecimal totalDisputedMatchCountBd = new BigDecimal(totalDisputedMatchCount).setScale(2, RoundingMode.HALF_UP);
        team.setDisputedMatches(totalDisputedMatchCountBd.intValue());
        System.out.println("totalDisputedMatchCountBd: " + totalDisputedMatchCountBd);
        BigDecimal disputePercentage = null;
        if (totalMatchCount == 0 || totalDisputedMatchCount == 0) {
            disputePercentage = new BigDecimal(0).setScale(0, RoundingMode.HALF_UP);
            System.out.println("disputePercentage2: " + disputePercentage);
        } else {
            BigDecimal disputeDecimal = totalDisputedMatchCountBd.divide(totalMatchCountBd).setScale(2, RoundingMode.HALF_UP);
            System.out.println("disputeDecimal: " + disputeDecimal);
            disputePercentage = disputeDecimal.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
            System.out.println("disputePercentage1: " + disputePercentage);
        }
        System.out.println("disputePercentageFINAL: " + disputePercentage);
        team.setDisputePercentage(disputePercentage);
    }

    public void updateDisputeByPk(long disputePk, String reason, List<String> links) {

        Dispute disputeEntity = disputeRepo.findDisputeByPk(disputePk);

        if (disputeEntity == null) {
            return;
        }

        disputeEntity.setReasonForDispute(reason);

        //update links here
        List<String> listFromDB = disputeEntity.getLinks();
        if (listFromDB == null) {
            listFromDB = new ArrayList<>();
        }

        listFromDB.clear();
        listFromDB.addAll(links);
    }

    public void createDisputeConversation(Team team, Dispute dispute, String message, boolean isTeam) {
        //List<Conversation> disputeConversations = conversationRepository.getAllConversationForTeamAndDispute(team.getPk(), dispute.getPk());
        Conversation c = new Conversation();
        c.setMessage(message);
        c.setIsTeam(isTeam);
        conversationRepository.associateConversationAndDispute(c, dispute);
        conversationRepository.associateConversationAndTeam(c, team);

        conversationRepository.persistConversation(c);
    }

    public List<Conversation> getAllConversationForTeamAndDispute(long teampk, long disputePk) {
        return conversationRepository.getAllConversationForTeamAndDispute(teampk, disputePk);
    }

    private void setDisputedMatchNotificationForDisputeStarter(User user, Match match, Dispute dispute) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.DISPUTED_MATCH);
        notification.setNotificationMessage("You have submitted a dispute ticket for a match.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notification.setDispute(dispute);
        dispute.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    private void setDisputedMatchNotificationForDisputeSecond(User user, Match match) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.DISPUTED_MATCH_BY_OTHER_TEAM);
        notification.setNotificationMessage("You have a match that has been disputed. Please submit a dispute ticket.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public List<Dispute> getAllDisputesForAUserPk(long userPk) {
        List<Dispute> disputes = disputeRepo.findAllDisputesForAUserPk(userPk);
        return disputes;
    }

    public List<String> getDisputePicturesForDispute(long disputePk) throws Exception {
        Dispute dispute = disputeRepo.findDisputeByPk(disputePk);
        if (dispute == null) {
            throw new Exception("Error trying to get pictures for non-existent dispute i.e. dispute is null");
        }
        Set<CloudMedia> cloudMedias = dispute.getCloudMedia();
        List<String> displayUrls = null;
        if (cloudMedias.size() > 0) {
            displayUrls = new ArrayList<>();
            for (CloudMedia cm : cloudMedias) {
                displayUrls.add(cm.getDisplayUrl());
            }
        }
        return displayUrls;
    }

    public Dispute getDisputeForUserAndMatchPk(long userPk, long matchPk) {
        Dispute dispute = disputeRepo.findDisputeByUserAndMatchPk(userPk, matchPk);
        return dispute;
    }

    public List<String> getDisputePicturesForUserAndMatchPk(long userPk, long matchPk) throws Exception {
        Dispute dispute = getDisputeForUserAndMatchPk(userPk, matchPk);
        if (dispute == null) {
            throw new Exception("Error trying to get pictures for non-existent dispute i.e. dispute is null for user pk: " + userPk + " and match pk: " + matchPk);
        }
        Set<CloudMedia> cloudMedias = dispute.getCloudMedia();
        List<String> displayUrls = null;
        if (cloudMedias.size() > 0) {
            displayUrls = new ArrayList<>();
            for (CloudMedia cm : cloudMedias) {
                displayUrls.add(cm.getDisplayUrl());
            }
        }
        return displayUrls;
    }

    public int getTotalOpenMatchDisputesByUserPk(long userPk) {
        return disputeRepo.getTotalOpenMatchDisputesByUserPk(userPk);
    }

    public List<Dispute> getAllOpenMatchDisputesByUserPkAndPageNumber(long userPk, int pageNumber) {
        List<Dispute> disputes = disputeRepo.getAllOpenMatchDisputesByUserPkAndPageNumber(userPk, pageNumber);
        return disputes;
    }

    public void associateMatchToDispute(Dispute dispute, Match match) {
        dispute.setMatch(match);
        match.getDisputes().add(dispute);
    }

    public void associateUserToDispute(Dispute dispute, User user) {
        dispute.setUser(user);
        user.getDisputes().add(dispute);
    }

    public void associateTeamToDispute(Dispute dispute, Team team) {
        dispute.setTeam(team);
        team.getDisputes().add(dispute);
    }

    public void submitProof(List<byte[]> medias) {
        for (byte[] item : medias) {
            //cloudStorage.uploadImageToCloudStorage(null, null, null);
        }
    }

//    public void resolveDispute(long matchPk, long teamPk) {
//        Match match = matchRepo.findMatchByPk(matchPk);
//        Set<Dispute> disuptesForMatch = match.getDisputes();
//        for (Dispute d : disuptesForMatch) {
//            d.setIsResolved(true);
//            if (d.getTeam().getPk() != teamPk) {
//                d.setWonDispuse(true);
//            }
//        }
//    }
    public void resolveDispute(Match match, Team team) {
        if (match.getDisputes() != null && !match.getDisputes().isEmpty()) {
            // DO NOT ADD/REMOVE TO THE LIST IN THE LOOP, ONLY UPDATE FIELDS
            for (Dispute d : match.getDisputes()) {
                d.setDisputeStatus(DisputeStatus.RESOLVED);
                setDisputedResolvedByAdminNotification(d.getUser(), d.getMatch(), d);
            }

            if (team.getPk() == match.getPkOfTeamThatCreatedMatch()) {
                Team t = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
                int disputedMatchCount = t.getDisputedMatches() - 1;
                t.setDisputedMatches(disputedMatchCount);

//                int totalMatchCount = matchRepo.findMatchesByTeamPkInActiveDisputedAndEndedStatus(team.getPk()).size();
//                int totalDisputedMatchCount = team.getDisputedMatches();
//                if (totalDisputedMatchCount == 0) {
//                    team.setDisputePercentage(new BigDecimal(0).setScale(0, RoundingMode.HALF_UP));
//                } else {
//                    BigDecimal disputeDecimal = new BigDecimal(totalDisputedMatchCount).divide(new BigDecimal(totalMatchCount)).setScale(2, RoundingMode.HALF_UP);
//                    BigDecimal disputePercentage = disputeDecimal.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
//                    team.setDisputePercentage(disputePercentage);
//                }
            } else {
                Team t = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
                int disputedMatchCount = t.getDisputedMatches() - 1;
                t.setDisputedMatches(disputedMatchCount);

//                int totalMatchCount = matchRepo.findMatchesByTeamPkInActiveDisputedAndEndedStatus(team.getPk()).size();
//                int totalDisputedMatchCount = team.getDisputedMatches();
//                if (totalDisputedMatchCount == 0) {
//                    team.setDisputePercentage(new BigDecimal(0).setScale(0, RoundingMode.HALF_UP));
//                } else {
//                    BigDecimal disputeDecimal = new BigDecimal(totalDisputedMatchCount).divide(new BigDecimal(totalMatchCount)).setScale(2, RoundingMode.HALF_UP);
//                    BigDecimal disputePercentage = disputeDecimal.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
//                    team.setDisputePercentage(disputePercentage);
//                }
            }
        }
    }

    private void setDisputedResolvedByAdminNotification(User user, Match match, Dispute dispute) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.DISPUTED_MATCH);
        notification.setNotificationMessage("You have a match dispute that has been resolved by an admin.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notification.setDispute(dispute);
        dispute.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }
}
