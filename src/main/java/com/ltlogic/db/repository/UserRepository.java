package com.ltlogic.db.repository;

import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> getAllUsers() {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getAllUsers", User.class);
        return query.getResultList();
    }

    public void persistUser(User c) {
        entityManager.persist(c);
    }

    public User findByPk(long pk) {
        return entityManager.find(User.class, pk);
    }

    public User findByUsername(String userName) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUserByUsername", User.class);
        query.setParameter("username", userName);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
//    public User findByUsernameLowercase(String userName) {
//        TypedQuery<User> query = entityManager.createNamedQuery("User.getUserByUsernameLowercase", User.class);
//        query.setParameter("username", userName.toLowerCase());
//        try {
//            return query.getSingleResult();
//        } catch (NoResultException | NonUniqueResultException ex) {
//            return null;
//        }
//    }

    public User findByUsernameLowercase(String userName) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUserByUsernameLowercase", User.class);
        query.setParameter("username", userName.toLowerCase());
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<User> getUsersByUserPks(List<Long> userPks) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUsersByPks", User.class);
        query.setParameter("userPks", userPks);
        return query.getResultList();
    }

    public User findByEmail(String email) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUserByEmail", User.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public boolean userWithEmailExists(String email) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUserByEmail", User.class);
        query.setParameter("email", email);
        try {
            User user = query.getSingleResult();
            if (user != null) {
                return true;
            }
        } catch (NoResultException ex) {
            return false;
        }
        return false;
    }

    public boolean userWithUsernameExists(String userName) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUserByUsernameLowercase", User.class);
        query.setParameter("username", userName);
        try {
            User user = query.getSingleResult();
            if (user != null) {
                return true;
            }
        } catch (NoResultException ex) {
            return false;
        }
        return false;
    }

    public User userWithUsernameExistsUser(String userName) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUserByUsernameLowercase", User.class);
        query.setParameter("username", userName);
        try {
            User user = query.getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (NoResultException ex) {
            return null;
        }
        return null;
    }

    public User findUserByUsernameAndPassword(String username, String password) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUserByUsernameAndPassword", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<User> getAllUsersOnTeam(long teamPk) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUsersOnTeam", User.class);
        query.setParameter("teamPk", teamPk);
        return query.getResultList();
    }

    public List<User> getUsersOnTeamOrderByMatchAcceptor(long teamPk, long matchAcceptorPk) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUsersOnTeamOrderByMatchAcceptor", User.class);
        query.setParameter("teamPk", teamPk);
        query.setParameter("matchAcceptorPk", matchAcceptorPk);
        return query.getResultList();
    }

    public List<User> getAllUsersInList(List<Long> userList) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getAllUsersInList", User.class);
        query.setParameter("userList", userList);
        return query.getResultList();
    }

    public List<User> getUsersByMatchPkAndTeamPk(long matchPk, long teamPk) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUsersByMatchPkAndTeamPk", User.class);
        query.setParameter("teamPk", teamPk);
        query.setParameter("matchPk", matchPk);
        return query.getResultList();
    }

    public List<User> getUsersByMatchPk(long matchPk) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUsersByMatchPk", User.class);
        query.setParameter("matchPk", matchPk);
        return query.getResultList();
    }

    public Long getCountOfUserTable() {
        Query query = entityManager.createQuery("SELECT count(*) FROM User u");
        Long count = (Long) query.getSingleResult();
        return count;
    }

    public List<User> getTop100UsersByXP() {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getTop100UsersByXP", User.class);
        query.setMaxResults(100);
        return query.getResultList();
    }

    public List<User> getTop100UsersByEarnings() {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getTop100UsersByEarnings", User.class);
        query.setMaxResults(100);
        return query.getResultList();
    }

    public List<User> getUsersBySearchedUsername(String username) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.getUsersBySearchedUsername", User.class);
        query.setParameter("username", "%" + username + "%");
        return query.getResultList();
    }

}
