/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import org.springframework.stereotype.Service;

/**
 *
 * @author jaimel
 */
public class HttpClient {

    public static final String USR = "NLGOnline";
    public static final String PWD = "1UEDTwbVClxEhStDyJImrNHLpnLBcMj1kWuRmHeh";
    public static final String BASE_URL = "api.challonge.com";
    public static final String SCHEME = "https";
    public static final String VERSION = "v1";

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpClient.class);

    public static HttpUrl httpUrlBuilder(String urlEndPoint) {
        return httpUrlBuilderWithQuery(urlEndPoint, null);
    }

    public static HttpUrl httpUrlBuilderWithQuery(String urlEndPoint, Map<String, String> queryParam) {

        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(BASE_URL)
                .addPathSegment(VERSION)
                .addPathSegments(urlEndPoint);
        if (queryParam != null) {
            for (Map.Entry<String, String> entry : queryParam.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return urlBuilder.build();
    }

    public static Request.Builder requestBuilder(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache");
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    private static OkHttpClient createAuthenticatedClient(final String username,
            final String password) {
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                if (responseCount(response) >= 3) {
                    return null;
                }
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }

    public static String makeGetRestCall(HttpUrl url) {
        OkHttpClient client = createAuthenticatedClient(USR, PWD);
        MediaType mediaType = MediaType.parse("application/json");
        Request request = requestBuilder(url)
                .get()
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                return responseBody;
            } else {
                response.body().close();
                return null;
            }
        } catch (IOException ex) {
            log.error("Error while making GET request: " + ex.getMessage(), ex);
        }
        return null;
    }

    public static String makePostRestCall(HttpUrl url, String requestBody) {
        OkHttpClient client = createAuthenticatedClient(USR, PWD);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBody);

        Request request = requestBuilder(url)
                .post(body)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                return responseBody;
            } else {
                response.body().close();
                return null;
            }
        } catch (IOException ex) {
            log.error("Error while making POST request: " + ex.getMessage(), ex);
        }
        return null;
    }

    public static String makePutRestCall(HttpUrl url, String requestBody) {
        OkHttpClient client = createAuthenticatedClient(USR, PWD);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBody);

        Request request = requestBuilder(url)
                .put(body)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                return responseBody;
            } else {
                response.body().close();
                return null;
            }
        } catch (IOException ex) {
            log.error("Error while making PUT request: ", ex);
        }
        return null;
    }

    public static String makeDeleteRestCall(HttpUrl url) {
        OkHttpClient client = createAuthenticatedClient(USR, PWD);
        MediaType mediaType = MediaType.parse("application/json");

        Request request = requestBuilder(url)
                .delete()
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                return responseBody;
            } else {
                response.body().close();
                return null;
            }
        } catch (IOException ex) {
            log.error("Error while making DELETE request: ", ex);
        }
        return null;
    }

}
