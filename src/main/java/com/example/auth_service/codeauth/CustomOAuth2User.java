package com.cloudofgoods.auth.codeauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Slf4j
public class CustomOAuth2User implements OAuth2User {
    private final String oauth2ClientName;
    private final OAuth2User oauth2User;

    public CustomOAuth2User(OAuth2User oauth2User, String oauth2ClientName) {
        this.oauth2User = oauth2User;
        this.oauth2ClientName = oauth2ClientName;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<? extends GrantedAuthority> authorities = oauth2User.getAuthorities();
        log.info("Log::CustomOAuth2User.getAuthorities >> " + authorities);
        return authorities;
    }

    public String getName() {
        String username = oauth2User.getAttribute("username");
        log.info("Log::CustomOAuth2User.getName >> " + username);
        return username;
    }

    public String getUserName() {
      String username = oauth2User.<String>getAttribute("username");
        log.info("Log::CustomOAuth2User.getUserName >> " + username);
        return username;
    }

    public String getOauth2ClientName() {
        log.info("Log::CustomOAuth2User.getOauth2ClientName" );
        return this.oauth2ClientName;
    }
}