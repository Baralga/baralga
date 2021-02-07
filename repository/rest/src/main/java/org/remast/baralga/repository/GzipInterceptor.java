package org.remast.baralga.repository;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.GzipSource;
import okio.Okio;

public class GzipInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder newRequest = chain.request().newBuilder();
        newRequest.addHeader("Accept-Encoding", "gzip");
        Response response = chain.proceed(newRequest.build());

        if (isGzipped(response)) {
            return unzip(response);
        } else {
            return response;
        }
    }

    private Response unzip(final Response response) throws IOException {

        if (response.body() == null) {
            return response;
        }

        GzipSource gzipSource = new GzipSource(response.body().source());
        String bodyString = Okio.buffer(gzipSource).readUtf8();

        ResponseBody responseBody = ResponseBody.create(bodyString, response.body().contentType());

        Headers strippedHeaders = response.headers().newBuilder()
                .removeAll("Content-Encoding")
                .removeAll("Content-Length")
                .build();
        return response.newBuilder()
                .headers(strippedHeaders)
                .body(responseBody)
                .message(response.message())
                .build();

    }

    private Boolean isGzipped(Response response) {
        return response.header("Content-Encoding") != null && response.header("Content-Encoding").equals("gzip");
    }
}