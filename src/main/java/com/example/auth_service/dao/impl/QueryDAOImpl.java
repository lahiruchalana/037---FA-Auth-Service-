package com.cloudofgoods.auth.dao.impl;

import com.cloudofgoods.auth.dao.QueryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class QueryDAOImpl implements QueryDAO {
    private final EntityManager entityManager;
    @Override
    public void createThrottling(String userName){

        entityManager.createNativeQuery("CREATE EVENT "+ userName +" ON SCHEDULE AT CURRENT_TIMESTAMP " +
                "+ INTERVAL 1 MINUTE DO UPDATE auth_user u " +
                " SET u.failedAttempt = 0," +
                " u.lockTime = null WHERE u.user_name =?1" ).setParameter(1, userName).executeUpdate();

    }
    @Override
    public void updateThrottling(String userName){

        entityManager.createNativeQuery("ALTER EVENT "+ userName +" ON SCHEDULE AT CURRENT_TIMESTAMP " +
                "+ INTERVAL 1 MINUTE DO UPDATE auth_user u " +
                " SET u.failedAttempt = 0," +
                " u.lockTime = null WHERE u.user_name =?1" ).setParameter(1, userName).executeUpdate();
    }

    @Override
    public void updateThrottlingAndLockUserAccount(String userName) {
        entityManager.createNativeQuery("ALTER EVENT "+ userName +" ON SCHEDULE AT CURRENT_TIMESTAMP " +
                "+ INTERVAL 1 MINUTE DO UPDATE auth_user u " +
                " SET u.account_non_locked = true , u.failedAttempt = 0," +
                " u.lockTime = null WHERE u.user_name =?1" ).setParameter(1, userName).executeUpdate();
    }
}
