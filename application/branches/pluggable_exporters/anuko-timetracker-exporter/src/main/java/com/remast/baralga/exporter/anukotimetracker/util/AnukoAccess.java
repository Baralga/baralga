package com.remast.baralga.exporter.anukotimetracker.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

import com.remast.baralga.exporter.anukotimetracker.model.AnukoActivity;
import com.remast.baralga.exporter.anukotimetracker.model.AnukoInfo;

public class AnukoAccess {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm");

    private String url;
    private final String username;
    private final String password;

    public AnukoAccess( String url, String username, String password ) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    public AnukoInfo getMergedAnukoInfo( DateTime startDate, DateTime endDate ) throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        AnukoInfo mergedInfo = new AnukoInfo();
        
        DateTime date = startDate;
        while( ! date.isAfter(endDate) ) {
            AnukoInfo info = _getAnukoInfo(httpclient, date);
            mergedInfo.merge( info );
            date = date.plusDays(1);
        }
        
        httpclient.getConnectionManager().shutdown();
        return mergedInfo;
    }
    
    private AnukoInfo _getAnukoInfo( HttpClient httpclient, DateTime date ) throws ClientProtocolException, IOException {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("login", this.username));
        parameters.add(new BasicNameValuePair("password", this.password));
        parameters.add(new BasicNameValuePair("action", "status"));
        
        String dateString = DATE_FORMAT.print(date);
        parameters.add(new BasicNameValuePair("date", dateString));
        
        HttpGet httpget = new HttpGet( this.url + "?"
                + URLEncodedUtils.format(parameters, "UTF-8"));
        
        //System.out.println("executing request " + httpget.getURI());

        ResponseHandler<AnukoInfo> responseHandler = new AnukoInfoResponseHandler();
        return httpclient.execute(httpget, responseHandler);
    }
    
    public void setUrl(String text) {
        this.url = text != null ? text.trim() : "";
    }
    
    public String getUrl() {
        return this.url;
    }

    public void submitActivities(List<ProjectActivity> activities, Map<Project, AnukoActivity> map) throws ClientProtocolException, IOException {
        
        DefaultHttpClient httpclient = new DefaultHttpClient();
        List<NameValuePair> baseParameters = new ArrayList<NameValuePair>();
        baseParameters.add(new BasicNameValuePair("login", this.username));
        baseParameters.add(new BasicNameValuePair("password", this.password));
        baseParameters = Collections.unmodifiableList(baseParameters);
        
        for( ProjectActivity activity : activities ) {
            AnukoActivity aactivity = map.get(activity.getProject());
            submitActivity( httpclient, activity, aactivity, baseParameters );
        }
        httpclient.getConnectionManager().shutdown();
    }

    private void submitActivity(DefaultHttpClient httpclient,
            ProjectActivity activity, AnukoActivity anukoActivity,
            List<NameValuePair> baseParameters) throws ClientProtocolException, IOException {
        List<NameValuePair> myBaseParams = new ArrayList<NameValuePair>( baseParameters );
        myBaseParams.add(new BasicNameValuePair("project", "" + anukoActivity.getFirstProject().getId()));
        myBaseParams.add(new BasicNameValuePair("activity", "" + anukoActivity.getId()));
        myBaseParams.add(new BasicNameValuePair("date", DATE_FORMAT.print(activity.getDay())));
        myBaseParams.add(new BasicNameValuePair("note", activity.getDescription()));
        myBaseParams = Collections.unmodifiableList(myBaseParams);
        
        {
            HttpPost post = new HttpPost(this.url);
            List<NameValuePair> params = new ArrayList<NameValuePair>( myBaseParams );
            params.add(new BasicNameValuePair("action", "start"));
            params.add(new BasicNameValuePair("start", TIME_FORMAT.print(activity.getStart())));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            post.setEntity(entity);
            HttpResponse response = httpclient.execute(post);
            response.getEntity().writeTo(System.out);
            response.getEntity().consumeContent();
        }
        
        {
            HttpPost post = new HttpPost(this.url);
            List<NameValuePair> params = new ArrayList<NameValuePair>( myBaseParams );
            params.add(new BasicNameValuePair("action", "stop"));
            params.add(new BasicNameValuePair("finish", TIME_FORMAT.print(activity.getEnd())));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            post.setEntity(entity);
            HttpResponse response = httpclient.execute(post);
            response.getEntity().writeTo(System.out);
            response.getEntity().consumeContent();
        }
    }
}
