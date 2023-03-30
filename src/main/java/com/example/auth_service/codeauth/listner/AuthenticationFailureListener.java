package com.cloudofgoods.auth.codeauth.listner;

import com.cloudofgoods.auth.codeauth.CustomLoginFailureHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFailureListener.class);

    private final CustomLoginFailureHandler loginFailureHandler;

    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {

        log.info("Log::Authentication failed for user: " + event.getAuthentication().getPrincipal());
        try {
            log.info("Log::AuthenticationFailureListener.onApplicationEvent >> Call to Authentication Failure Handler");
            loginFailureHandler.onAuthenticationFailure(event);
        } catch (IOException | ServletException e) {
            log.error("Log::AuthenticationFailureListener.onApplicationEvent >> RuntimeException");
            throw new RuntimeException(e);
        }
    }
}