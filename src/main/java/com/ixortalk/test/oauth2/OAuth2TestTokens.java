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

import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.*;
import static com.jayway.restassured.RestAssured.given;

public class OAuth2TestTokens {

    public static OAuth2AccessToken adminToken() {
        return getAccessToken(CLIENT_ID_ADMIN, CLIENT_SECRET_ADMIN);
    }

    public static OAuth2AccessToken userToken() {
        return getAccessToken(CLIENT_ID_USER, CLIENT_SECRET_USER);
    }

    public static OAuth2AccessToken getAccessToken(String clientId, String clientSecret) {
        return given()
                .parameters("grant_type", "client_credentials")
                .auth()
                .preemptive()
                .basic(clientId, clientSecret)
                .when()
                .post("/oauth/token")
                .as(OAuth2AccessToken.class);
    }
}
