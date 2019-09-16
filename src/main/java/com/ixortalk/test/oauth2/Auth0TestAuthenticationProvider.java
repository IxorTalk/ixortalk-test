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

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.spring.security.api.authentication.JwtAuthentication;
import com.auth0.spring.security.api.authentication.PreAuthenticatedAuthenticationJsonWebToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static java.util.stream.Collectors.toSet;

public class Auth0TestAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return new Auth0TestAuthentication(authentication);

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }

    private static class Auth0TestAuthentication implements Authentication, JwtAuthentication {

        private final Authentication authentication;

        private boolean authenticated;

        public Auth0TestAuthentication(Authentication authentication) {
            this.authentication = authentication;
            this.authenticated = true;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return JWT.decode(extractToken(authentication))
                    .getClaim("authorities")
                    .asList(String.class)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(toSet());
        }

        @Override
        public Object getCredentials() {
            return authentication.getCredentials();
        }

        @Override
        public Object getDetails() {
            return authentication.getDetails();
        }

        @Override
        public Object getPrincipal() {
            return authentication.getPrincipal();
        }

        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            this.authenticated = isAuthenticated;
        }

        @Override
        public String getName() {
            return "auth0-test-authentication-provider";
        }

        @Override
        public String getToken() {
            return extractToken(authentication);
        }

        @Override
        public String getKeyId() {
            throw new UnsupportedOperationException("Not supported when using " + Auth0TestAuthentication.class);
        }

        @Override
        public Authentication verify(JWTVerifier verifier) throws JWTVerificationException {
            throw new UnsupportedOperationException("Not supported when using " + Auth0TestAuthentication.class);
        }

        private static String extractToken(Authentication authentication) {
            return ((PreAuthenticatedAuthenticationJsonWebToken) authentication).getToken();
        }
    }
}
