package com.cloudofgoods.auth.codeauth;

import com.cloudofgoods.auth.entity.User;
import com.cloudofgoods.auth.service.LoginAttemptsUserServicesBO;
import com.cloudofgoods.auth.service.impl.LoginAttemptsUserServicesBOImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import java.io.IOException;

import static com.cloudofgoods.auth.configarations.CommonConfig.USER_NOT_FOUND_MSG;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomLoginFailureHandler {

    protected final AuthenticationManager authenticationManager;
    private final LoginAttemptsUserServicesBO userServiceBO;

    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) throws IOException, ServletException {
        log.info("Log::CustomLoginFailureHandler.onAuthenticationFailure");
        Object username = event.getAuthentication().getPrincipal();
        User user = null;
        try {
            log.info("Log::CustomLoginFailureHandler.onAuthenticationFailure.tryBlock findByUsername(userName)");
            user = userServiceBO.findByUsername(username.toString());
        } catch (UsernameNotFoundException e) {
            log.error("Log::CustomLoginFailureHandler.onAuthenticationFailure >> UsernameNotFoundException");
            throw (new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
        }

        if (user != null) {
            log.info("Log::CustomLoginFailureHandler.onAuthenticationFailure.user != Null");
            if (user.isEnabled() && user.isAccountNonLocked()) {
                log.info("Log::CustomLoginFailureHandler.onAuthenticationFailure increase Login Attempts");
                if (user.getFailedAttempt() < LoginAttemptsUserServicesBOImpl.MAX_FAILED_ATTEMPTS - 1) {
                    userServiceBO.increaseFailedAttempts(user);
                } else {
                    user.setAccountNonLocked(true);
                    log.info("Log::CustomLoginFailureHandler.onAuthenticationFailure.user Lock");
                    userServiceBO.lock(user);
                }
            }
        }
    }
}