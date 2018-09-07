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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Configuration
@EnableAuthorizationServer
public class OAuth2EmbeddedTestServer {

    public static final String CLIENT_ID_ADMIN = "clientAdmin";
    public static final String CLIENT_SECRET_ADMIN = "clientAdminSecret";

    public static final String CLIENT_ID_USER = "clientUser";
    public static final String CLIENT_SECRET_USER = "clientUserSecret";

    public static final String CLIENT_ID_OTHER_USER = "otherClientUser";
    public static final String CLIENT_SECRET_OTHER_USER = "otherClientUserSecret";

    @Configuration
    @Order(LOWEST_PRECEDENCE - 1)
    protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager);
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
                        .withClient(CLIENT_ID_USER)
                        .secret(CLIENT_SECRET_USER)
                        .authorizedGrantTypes("client_credentials")
                        .scopes("openid")
                        .authorities("ROLE_USER")
                    .and()
                        .withClient(CLIENT_ID_OTHER_USER)
                        .secret(CLIENT_SECRET_OTHER_USER)
                        .authorizedGrantTypes("client_credentials")
                        .scopes("openid")
                        .authorities("ROLE_USER");
        }
    }

    @Configuration
    @Order(-21)
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {

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

    }

}