package com.ltlogic.service.core;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.PasswordResetStatusEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.constants.TokenVerificationEnum;
import com.ltlogic.db.entity.Bank;
import com.ltlogic.db.entity.PasswordResetToken;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.VerificationToken;
import com.ltlogic.db.entity.rank.user.UserRankEarnings;
import com.ltlogic.db.entity.rank.user.UserRankXP;
import com.ltlogic.db.repository.BankRepository;
import com.ltlogic.db.repository.TeamInviteRepository;
import com.ltlogic.db.repository.PasswordResetTokenRepository;
import com.ltlogic.db.repository.RoleRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRankRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.db.repository.VerificationTokenRepository;
import com.ltlogic.pojo.RegistrationPojo;
import com.ltlogic.pojo.UserPojo;
import static java.lang.Math.toIntExact;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserRankRepository userRankRepo;
    @Autowired
    private BankRepository bankRepo;
    @Autowired
    private VerificationTokenRepository verTokenRepo;
    @Autowired
    private PasswordResetTokenRepository pasTokenRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TeamRepository teamRepo;
    @Autowired
    private TeamInviteRepository teamInviteRepository;
    @Autowired
    private SecurityServiceImpl securityServiceImpl;

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public static final String PASSSWORD_MATCH = "match";
    public static final String PASSWORD_MISMATCH = "mismatch";

    public User findByPk(long pk) {
        return userRepo.findByPk(pk);
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User findByUsernameLowercase(String username) {
        return userRepo.findByUsernameLowercase(username);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public List<User> getAllUsersInList(List<Long> userList) {
        return userRepo.getAllUsersInList(userList);
    }

    public boolean emailExist(String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    public void persistUser(User user) {
        userRepo.persistUser(user);
    }

    public User registerUser(RegistrationPojo registrationPojo) {
        User user = new User();
        UserPojo up = new UserPojo();
        user.setUserInfo(up);
        user.setUsername(registrationPojo.getUsername());
        user.setUsernameLowercase(registrationPojo.getUsername().toLowerCase());
        user.setPassword(bCryptPasswordEncoder.encode(registrationPojo.getPasswordConfirm()));
        user.setEmail(registrationPojo.getEmail());
        user.getUserInfo().setRegion(RegionEnum.getRegionEnumById(Integer.parseInt(registrationPojo.getRegionId())));
        //user.setPasswordConfirm(registrationPojo.getPasswordConfirm());
        LOG.info("//////////////////////////////////////////////TIME ZONE ID: " + registrationPojo.getTimeZoneEnumId());
        if (registrationPojo.getTimeZoneEnumId() != 0) {
            user.getUserInfo().setTimeZone(TimeZoneEnum.getTimeZonesEnumById(registrationPojo.getTimeZoneEnumId()));
        }
        //user.getRoles().add(roleRepo.findRoleByEnum(RoleEnum.ROLE_USER));
        Bank bank = new Bank();
        BigDecimal bd = new BigDecimal(0.00).setScale(2);
        bank.setTotalAmount(bd);
        user.setBank(bank);
        userRepo.persistUser(user);
        bank.setUser(user);
        bankRepo.persistBank(bank);

        Long userTableCount = userRepo.getCountOfUserTable();
        System.out.println("COUNT USER-----------------------------------------------: " + userTableCount);
        int count = toIntExact(userTableCount);

        UserRankXP userRankXP = new UserRankXP();
        userRankXP.setRank(count);
        userRankXP.setUser(user);
        user.setRankXP(userRankXP);
        userRankRepo.persistUserRankXP(userRankXP);

        UserRankEarnings userRankEarnings = new UserRankEarnings();
        userRankEarnings.setRank(count);
        userRankEarnings.setUser(user);
        user.setRankEarnings(userRankEarnings);
        userRankRepo.persistUserRankEarnings(userRankEarnings);

        return user;
    }

    public void editUserInfo(UserPojo userInfo, long pk) {
        User currentUser = userRepo.findByPk(pk);
        currentUser.setUserInfo(userInfo);
    }

    public String editUserPassword(long pk, String oldPassword, String newPassword) throws Exception {
        User currentUser = userRepo.findByPk(pk);

        if (currentUser == null) {
            throw new Exception("User could not be found.");
        }

        try {
            String storedPassword = currentUser.getPassword();
            if (bCryptPasswordEncoder.matches(oldPassword, storedPassword)) {
                currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
                return PASSSWORD_MATCH;
            } else {
                throw new Exception("Old Password does not match.");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public PasswordResetStatusEnum resetUserPasswordByPasswordResetToken(String token, String newPassword) throws Exception {
        PasswordResetToken passwordResetToken = pasTokenRepo.findByTokenString(token);
        if (passwordResetToken == null) {
            throw new Exception("Password Reset Token is not found");
        }

        User currentUser = passwordResetToken.getUser();
        if (currentUser == null) {
            throw new Exception("Could not find user.");
        }

        String storedPassword = currentUser.getPassword();
//        if (bCryptPasswordEncoder.matches(oldPassword, storedPassword)) {
        currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
//            securityServiceImpl.autologin(currentUser.getUsername(), newPassword);
        return PasswordResetStatusEnum.PASSSWORD_MATCH;
//        } else {
//            return PasswordResetStatusEnum.PASSWORD_MISMATCH;
//        }
    }

    public boolean isUserLeaderOfTeam(long userPk, long teamPk) {
        Team t = teamRepo.findTeamByPk(teamPk);
        return userPk == t.getTeamLeaderPk();
    }

    public boolean isUserEligibleForPlay() {
        //if user does not have a gt set or xbox or ps then:
        return false;
    }

    //################################################################# Email Account Verification ##############################################################
    public void createVerificationTokenForUser(User user, String token) {
        VerificationToken myToken = new VerificationToken(user, token);
        user.setvToken(myToken);
        verTokenRepo.saveToken(myToken);
    }

    public VerificationToken generateNewVerificationToken(String existingToken) {
        VerificationToken token = verTokenRepo.findByTokenString(existingToken);
        token.updateToken(UUID.randomUUID().toString());
        return token;
    }

    public User getUserFromVerificationToken(String verificationToken) {
        final VerificationToken token = verTokenRepo.findByTokenString(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    //Add check for SQL-injection here
    public TokenVerificationEnum validateVerificationToken(String token) {
        final VerificationToken verificationToken = verTokenRepo.findByTokenString(token);
        if (verificationToken == null) {
            LOG.info("TOKEN_INVALID RESULT");
            return TokenVerificationEnum.TOKEN_INVALID;
        }

        if ((DateTimeUtil.getDefaultLocalDateTimeNow().isAfter(verificationToken.getExpiryDateTime()))) {
            verTokenRepo.delete(verificationToken);
            LOG.info("TOKEN_EXPIRED RESULT");
            return TokenVerificationEnum.TOKEN_EXPIRED;
        }
        final User user = verificationToken.getUser();
        user.setIsVerified(true);
        return TokenVerificationEnum.TOKEN_VALID;
    }
    //################################################################# Email Account Verification ##############################################################

    //################################################################# Reset Password Verification ##############################################################
    public PasswordResetToken createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(user, token);
        pasTokenRepo.saveToken(myToken);
        return myToken;
    }

    public PasswordResetToken generateNewPasswordResetToken(String existingToken) {
        PasswordResetToken token = pasTokenRepo.findByTokenString(existingToken);
        token.updateToken(UUID.randomUUID().toString());
        return token;
    }

    public User getUserFromPasswordResetToken(String passwordResetToken) {
        final PasswordResetToken token = pasTokenRepo.findByTokenString(passwordResetToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    //Add check for SQL-injection here
    public TokenVerificationEnum validatePasswordResetToken(String token) {
        final PasswordResetToken passwordResetToken = pasTokenRepo.findByTokenString(token);
        if (passwordResetToken == null) {
            LOG.info("TOKEN_INVALID RESULT");
            return TokenVerificationEnum.TOKEN_INVALID;
        }

        if ((DateTimeUtil.getDefaultLocalDateTimeNow().isAfter(passwordResetToken.getExpiryDateTime()))) {
            pasTokenRepo.delete(passwordResetToken);
            LOG.info("TOKEN_EXPIRED RESULT");
            return TokenVerificationEnum.TOKEN_EXPIRED;
        }
        //final User user = passwordResetToken.getUser();
        return TokenVerificationEnum.TOKEN_VALID;
    }
//################################################################# Reset Password Verification ##############################################################

    public boolean isUserEligibleToPlayOnXboxOne(long userPk) {
        User user = userRepo.findByPk(userPk);
        boolean isUserEligibleForPlayForXboxOne = true;
        if (user.getUserInfo().getXboxOneGamerTag().trim().isEmpty() || user.getUserInfo().getXboxOneGamerTag() == null) {
            isUserEligibleForPlayForXboxOne = false;
        }
        return isUserEligibleForPlayForXboxOne;
    }

    public boolean isUserEligibleToPlayOnPS4(long userPk) {
        User user = userRepo.findByPk(userPk);
        boolean isUserEligibleForPlayForPS4 = true;
        if (user.getUserInfo().getPlayStation4Name().trim().isEmpty() || user.getUserInfo().getPlayStation4Name() == null) {
            isUserEligibleForPlayForPS4 = false;
        }
        return isUserEligibleForPlayForPS4;
    }

    public void addUserToPinnedUsersList(long userPk, long userBeingAddedToListPk) {
        User user = userRepo.findByPk(userPk);
        user.getPinnedUsersPkList().add(userBeingAddedToListPk);
    }

    public void removeUserFromUsersPinnedList(long userPk, long userBeingRemovedFromListPk) {
        User user = userRepo.findByPk(userPk);
        user.getPinnedUsersPkList().remove(userBeingRemovedFromListPk);
    }

    public List<Long> getUsersPinnedUserList(long userPk) {
        User user = userRepo.findByPk(userPk);
        return user.getPinnedUsersPkList();
    }

    public List<User> getListOfUserObjectsFromListOfUserPks(ArrayList<Long> userPks) {
        List<User> listOfUsers = userRepo.getUsersByUserPks(userPks);
        return listOfUsers;
    }

    public List<User> getAllUsersOnTeam(long teamPk) {
        return userRepo.getAllUsersOnTeam(teamPk);
    }

    public List<User> getUsersOnTeamOrderByMatchAcceptor(long teamPk, long matchAcceptorPk) {
        return userRepo.getUsersOnTeamOrderByMatchAcceptor(teamPk, matchAcceptorPk);
    }

    public List<User> getUsersBySearchedUsername(String username) {
        return userRepo.getUsersBySearchedUsername(username);
    }

    public void setUserNotificationNumber(User user, int notificationNumber) {
        user.setNumberOfNotifications(notificationNumber);
    }
}
