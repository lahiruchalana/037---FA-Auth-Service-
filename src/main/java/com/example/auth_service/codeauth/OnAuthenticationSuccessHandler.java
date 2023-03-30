package com.cloudofgoods.auth.codeauth;

import com.cloudofgoods.auth.entity.User;
import com.cloudofgoods.auth.enumpackage.AppUserRoleEnum;
import com.cloudofgoods.auth.enumpackage.AuthenticationType;
import com.cloudofgoods.auth.service.LoginAttemptsUserServicesBO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
// Success Handler Design For Social Login
public class OnAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final LoginAttemptsUserServicesBO userService;

    public static AuthenticationType getAuth(String oauth2ClientName) {
      log.info("OnAuthenticationSuccessHandler getAuth oauth2ClientName " + oauth2ClientName);
        for (AuthenticationType auth : AuthenticationType.values()) {
            String aut = String.valueOf(auth);
            if (aut.equals(oauth2ClientName)) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        log.info("OnAuthenticationSuccessHandler onAuthenticationSuccess ");
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        String oauth2ClientName = oauth2User.getOauth2ClientName();
        String username = oauth2User.getUserName();
        String name = oauth2User.getName();
        String fName = String.valueOf(oauth2User.getAttributes().get(1));

        User user = new User(fName, name, username, AppUserRoleEnum.ROLE_USER, getAuth(oauth2ClientName));
        userService.updateAuthenticationType(user);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}