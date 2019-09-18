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

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.CLIENT_ID_ADMIN;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.OTHER_USER_NAME;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.USER_NAME;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.CLIENT_SECRET_ADMIN;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.OTHER_USER_PASSWORD;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.USER_PASSWORD;
import static com.jayway.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;

public class OAuth2TestTokens {

    public static OAuth2AccessToken adminToken() {
        return getClientCredentialsAccessToken(CLIENT_ID_ADMIN, CLIENT_SECRET_ADMIN);
    }

    public static OAuth2AccessToken userToken() {
        return getPasswordGrantAccessToken(USER_NAME, USER_PASSWORD);
    }

    public static OAuth2AccessToken otherUserToken() {
        return getPasswordGrantAccessToken(OTHER_USER_NAME, OTHER_USER_PASSWORD);
    }

    public static OAuth2AccessToken getClientCredentialsAccessToken(String clientId, String clientSecret) {
        return given()
                .parameters("grant_type", "client_credentials")
                .auth()
                .preemptive()
                .basic(clientId, clientSecret)
                .when()
                .post("/oauth/token")
                .as(OAuth2AccessToken.class);
    }

    public static OAuth2AccessToken getPasswordGrantAccessToken(String username, String password) {
        return given()
                .parameters("grant_type", "password")
                .parameters("username", username)
                .parameters("password", password)
                .auth()
                .preemptive()
                .basic(CLIENT_ID_ADMIN, CLIENT_SECRET_ADMIN)
                .when()
                .post("/oauth/token")
                .as(OAuth2AccessToken.class);
    }

    public static String authorizationHeader(OAuth2AccessToken oAuth2AccessToken) {
        return authorizationHeader(oAuth2AccessToken.getValue());
    }

    public static String authorizationHeader(String accessToken) {
        return format("%s %s", BEARER_TYPE, accessToken);
    }
}
