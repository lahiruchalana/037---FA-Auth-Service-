package com.cloudofgoods.auth.configarations.security;

import com.cloudofgoods.auth.codeauth.OnAuthenticationSuccessHandler;
import com.cloudofgoods.auth.service.impl.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
@Slf4j
public class OAuth2ServerConfiguration {

    static final String[] AUTH_WHITELIST = {
            "/api/v5/oauth/user/customer/registration/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**",
            "/login/**"
    };
    private static final String RESOURCE_ID = "payment";

    @Configuration
    @EnableResourceServer
    @RequiredArgsConstructor
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
        private final CustomOAuth2UserService oauth2UserService;

        private final OnAuthenticationSuccessHandler oauthLoginSuccessHandler;
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            log.info("LOG::OAuth2ServerConfiguration.ResourceServerConfiguration.configure(ResourceServerSecurityConfigurer)");
            resources.resourceId(RESOURCE_ID);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            log.info("LOG::OAuth2ServerConfiguration.ResourceServerConfiguration.configure(HttpSecurity)");
//            http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();;
            http.authorizeRequests()
                    .antMatchers(AUTH_WHITELIST)
                    .permitAll()
                    .anyRequest().
                    fullyAuthenticated()
                    .and()
                    .oauth2Login()
                    .userInfoEndpoint()
                    .userService(oauth2UserService)
                    .and()
                    .successHandler(oauthLoginSuccessHandler);
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
        private final PasswordEncoder passwordEncoder;
        private final DataSource dataSource;
        private final AuthenticationManager authenticationManager;
        @Autowired
        private UserDetailsService userDetailsService;


        public AuthorizationServerConfiguration(AuthenticationManager authenticationManager, DataSource dataSource,
                                                PasswordEncoder passwordEncoder) {
            this.authenticationManager = authenticationManager;
            this.dataSource = dataSource;
            this.passwordEncoder = passwordEncoder;
            log.info("LOG::OAuth2ServerConfiguration.AuthorizationServerConfiguration.AuthorizationServerConfiguration");
        }

        @Bean
        TokenStore jdbcTokenStore() {
            log.info("LOG::OAuth2ServerConfiguration.AuthorizationServerConfiguration.jdbcTokenStore()");
            return new JdbcTokenStore(dataSource);
        }


        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) {
            log.info("LOG::OAuth2ServerConfiguration.AuthorizationServerConfiguration.configure(AuthorizationServerSecurityConfigurer)");
            security.checkTokenAccess("isAuthenticated()").tokenKeyAccess("permitAll()");
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            log.info("LOG::OAuth2ServerConfiguration.AuthorizationServerConfiguration.configure(ClientDetailsServiceConfigurer)");
            clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.pathMapping("/oauth/check_token", "/api/v5/authorize")
                    .pathMapping("/oauth/token", "/api/v5/token");
            log.info("LOG::OAuth2ServerConfiguration.AuthorizationServerConfiguration.configure(AuthorizationServerEndpointsConfigurer)");
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();

            tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(tokenEnhancer()));

            endpoints.tokenStore(jdbcTokenStore());
            log.info("LOG::AuthorizationServerConfiguration.configure");
            endpoints.accessTokenConverter(accessTokenConverter())
                    .tokenEnhancer(tokenEnhancerChain).
                    authenticationManager(authenticationManager).userDetailsService(userDetailsService);

        }

        @Bean
        public AccessTokenConverter accessTokenConverter() {
            log.info("LOG::OAuth2ServerConfiguration.AuthorizationServerConfiguration.AccessTokenConverter()");
            DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
            tokenConverter.setUserTokenConverter(new CustomUserAuthenticationConverter());
            return tokenConverter;
        }

        @Bean
        public TokenEnhancer tokenEnhancer() {
            log.info("LOG::OAuth2ServerConfiguration.AuthorizationServerConfiguration.TokenEnhancer()");
            return new CustomTokenEnhancer();
        }

    }
}