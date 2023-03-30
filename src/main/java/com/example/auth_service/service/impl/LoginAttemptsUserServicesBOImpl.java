/**
 * @author - Chamath_Wijayarathna
 * Date :6/17/2022
 */

package com.cloudofgoods.auth.service.impl;

import com.cloudofgoods.auth.dao.QueryDAO;
import com.cloudofgoods.auth.dao.UserDetailRepository;
import com.cloudofgoods.auth.entity.User;
import com.cloudofgoods.auth.service.LoginAttemptsUserServicesBO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import static com.cloudofgoods.auth.configarations.CommonConfig.USER_NOT_FOUND_MSG;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LoginAttemptsUserServicesBOImpl implements LoginAttemptsUserServicesBO {

    public static final int MAX_FAILED_ATTEMPTS = 3;
    private final UserDetailRepository userDetailRepository;
    private final QueryDAO queryDAO;

    @Override
    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        if (user.getFailedAttempt() == 0) {
            queryDAO.createThrottling(user.getUsername());
        } else {
            queryDAO.updateThrottling(user.getUsername());
        }
        userDetailRepository.updateFailedAttempts(newFailAttempts, user.getUsername());
    }
    @Override
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        log.info("LOG::LoginAttemptsUserServicesBOImpl.lock >> Lock User When MAX FAILED ATTEMPTS and Set Event ");
        queryDAO.updateThrottlingAndLockUserAccount(user.getUsername());
        userDetailRepository.save(user);
    }
    @Override
    public User findByUsername(String userName) {
        return userDetailRepository.findByUsername(userName).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, userName)));
    }
    @Override
    public void updateAuthenticationType(User appUser) {
        try {
            Optional<User> user = userDetailRepository.findByUsername(appUser.getUsername());
            if (user.isEmpty()) userDetailRepository.save(appUser);
            else userDetailRepository.updateAuthenticationType(appUser.getUsername(), appUser.getAuthType());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
