package com.kma.drive.util;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class JwtInterceptor implements Interceptor {
    private String jwtToken;

    public JwtInterceptor(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oleRequest = chain.request();

        Log.d("MinhNTn", "intercept: " + oleRequest.url());

        Request newRequest = oleRequest.newBuilder()
                .header("Authorization", "Bearer " + jwtToken)
                .build();

        return chain.proceed(newRequest);
    }
}
