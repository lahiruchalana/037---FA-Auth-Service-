package com.cloudofgoods.auth.configarations.security;

import com.cloudofgoods.auth.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.Map;

@Slf4j
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = (Map<String, Object>) super.convertUserAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        log.info("LOG: CustomUserAuthenticationConverter.convertUserAuthentication >> Add additional info");
        response.put("user_id", user.getId());
        response.put("email", user.getEmail());

        return response;
    }
}
