package com.remast.baralga.exporter.anukotimetracker.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;

import com.remast.baralga.exporter.anukotimetracker.model.AnukoInfo;

public class AnukoAccess {
    private String url;
    private String username;
    private String password;

    public AnukoAccess( String url, String username, String password ) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    public AnukoInfo getAnukoInfo( DateTime date ) throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("login", this.username));
        parameters.add(new BasicNameValuePair("password", this.password));
        parameters.add(new BasicNameValuePair("action", "status"));
        
        String dateString = date.getMonthOfYear() + "/"
            + date.getDayOfMonth() + "/" + date.getYear();
        parameters.add(new BasicNameValuePair("date", dateString));
        
        HttpGet httpget = new HttpGet( this.url + "?"
                + URLEncodedUtils.format(parameters, "UTF-8"));
        
        //System.out.println("executing request " + httpget.getURI());

        ResponseHandler<AnukoInfo> responseHandler = new AnukoInfoResponseHandler();
        AnukoInfo info = httpclient.execute(httpget, responseHandler);
        
        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
        
        return info;
    }

    public void setUrl(String text) {
        this.url = text != null ? text.trim() : "";
    }
    
    public String getUrl() {
        return this.url;
    }
}
