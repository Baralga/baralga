package com.remast.baralga.exporter.anukotimetracker.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class JdomResponseHandler implements ResponseHandler<Document> {

    @Override
    public Document handleResponse(HttpResponse response)
            throws ClientProtocolException, IOException {
        if(response.getEntity().getContentLength() == 0 ) {
            return null;
        }
        SAXBuilder builder = new SAXBuilder();
        HttpEntity entity = response.getEntity();
        String charSet = EntityUtils.getContentCharSet(entity);
        charSet = charSet != null ? charSet : HTTP.DEFAULT_CONTENT_CHARSET;
        Reader reader = new InputStreamReader(entity.getContent(), charSet);
        try {
            return builder.build(reader);
        } catch (JDOMException e) {
            throw new IllegalArgumentException("Response is no valid XML", e);
        }
    }

}
