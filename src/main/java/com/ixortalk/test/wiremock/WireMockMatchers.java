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
package com.ixortalk.test.wiremock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.ValueMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.jwt.JwtHelper.decode;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
import static org.springframework.security.oauth2.provider.token.AccessTokenConverter.AUTHORITIES;

public class WireMockMatchers {

    public static ValueMatcher<Request> jwtTokenWithAuthority(String role) {
        return request -> {
            try {
                String authorizationHeader = request.getHeader(AUTHORIZATION);
                String bearerToken = substringAfter(authorizationHeader, BEARER_TYPE + " ");
                List<String> authorities = (List<String>) new ObjectMapper().readValue(decode(bearerToken).getClaims(), Map.class).get(AUTHORITIES);
                return MatchResult.of(authorities != null && authorities.contains(role));
            } catch (IOException e) {
                throw new RuntimeException("Could not deserialize claims: " + e.getMessage(), e);
            }
        };
    }
}
