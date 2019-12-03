/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.test.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Configuration
@EnableAuthorizationServer
public class OAuth2EmbeddedTestServer {

    public static final String CLIENT_ID_ADMIN = "clientAdmin";
    public static final String CLIENT_SECRET_ADMIN = "clientAdminSecret";

    public static final String CLIENT_WITHOUT_ROLES_ID = "clientWithoutRolesId";
    public static final String CLIENT_WITHOUT_ROLES_SECRET = "clientWithoutRolesSecret";

    public static final String USER_NAME = "usersUsername";
    public static final String USER_PASSWORD = "usersPassword";

    public static final String OTHER_USER_NAME = "otherUsersUsername";
    public static final String OTHER_USER_PASSWORD = "otherUsersPassword";

    public static final int TEST_WEB_SECURITY_CONFIG_ORDER = -21;

    @Configuration
    @Order(LOWEST_PRECEDENCE - 1)
    protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                    .tokenStore(tokenStore())
                    .accessTokenConverter(accessTokenConverter())
                    .authenticationManager(authenticationManager);
        }

        @Bean
        public TokenStore tokenStore() {
            return new JwtTokenStore(accessTokenConverter());
        }

        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            converter.setSigningKey("testSigningKey");
            return converter;
        }

        @Bean
        @Primary
        public DefaultTokenServices tokenServices() {
            DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
            defaultTokenServices.setTokenStore(tokenStore());
            defaultTokenServices.setSupportRefreshToken(true);
            return defaultTokenServices;
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .inMemory()
                        .withClient(CLIENT_ID_ADMIN)
                        .secret(CLIENT_SECRET_ADMIN)
                        .authorizedGrantTypes("client_credentials", "password")
                        .scopes("openid")
                        .authorities("ROLE_ADMIN")
                    .and()
                        .withClient(CLIENT_WITHOUT_ROLES_ID)
                        .secret(CLIENT_WITHOUT_ROLES_SECRET)
                        .authorizedGrantTypes("client_credentials")
                        .scopes("openid");
        }
    }

    @Configuration
    @Order(TEST_WEB_SECURITY_CONFIG_ORDER)
    @EnableWebSecurity
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {

        @Autowired(required = false)
        private List<InMemoryUsersConfigurer> inMemoryUsersConfigurers = newArrayList();

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers()
                    .antMatchers("/oauth/authorize", "/oauth/confirm_access")
                    .and()
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryUserDetailsManagerConfigurer = auth
                    .inMemoryAuthentication();

            inMemoryUserDetailsManagerConfigurer
                        .withUser(USER_NAME)
                        .password(USER_PASSWORD)
                        .authorities("ROLE_USER")
                    .and()
                        .withUser(OTHER_USER_NAME)
                        .password(OTHER_USER_PASSWORD)
                        .authorities("ROLE_USER");

            inMemoryUsersConfigurers.forEach(configurer -> configurer.configure(inMemoryUserDetailsManagerConfigurer));
        }
    }
}