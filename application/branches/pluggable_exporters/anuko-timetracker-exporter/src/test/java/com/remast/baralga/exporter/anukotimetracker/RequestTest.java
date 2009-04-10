package com.remast.baralga.exporter.anukotimetracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jdesktop.swingx.JXFrame;
import org.jdom.JDOMException;
import org.junit.Test;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;

import com.remast.baralga.exporter.anukotimetracker.gui.ProjectMappingPanel;
import com.remast.baralga.exporter.anukotimetracker.model.AnukoInfo;
import com.remast.baralga.exporter.anukotimetracker.util.AnukoInfoResponseHandler;

/**
 * Unit test for simple App.
 */
public class RequestTest {
    /**
     * Rigourous Test :-)
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws JDOMException 
     */
    @Test
    public void testApp() throws ClientProtocolException, IOException, JDOMException {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("login", "kutzi_user"));
        parameters.add(new BasicNameValuePair("password", "moin"));
        parameters.add(new BasicNameValuePair("action", "status"));
        //'&date='+(dd.getMonth()+1)+'/'+dd.getDate()+'/'+dd.getFullYear()+
        
        HttpGet httpget = new HttpGet("http://timetracker.wrconsulting.com/wginfo.php?"
                + URLEncodedUtils.format(parameters, "UTF-8"));
        
        System.out.println("executing request " + httpget.getURI());

        // Create a response handler
        //ResponseHandler<String> responseHandler = new BasicResponseHandler();
        ResponseHandler<AnukoInfo> responseHandler = new AnukoInfoResponseHandler();
        AnukoInfo info = httpclient.execute(httpget, responseHandler);
        displayMapper(info);
        System.out.println(info);
        
        
        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();        
    }
    
    private void displayMapper( AnukoInfo info ) {
        initLookAndFeel();
        ProTrack data = new ProTrack();
        data.add(new Project(1, "foo", "bar"));
        data.add(new Project(1, "B-Project B", "BBBB!"));
        
        JXFrame frame = new JXFrame();
        frame.setSize(530, 720);
        frame.setResizable(true);
        frame.add(new ProjectMappingPanel(info, data, null ));
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
    }

    private static void initLookAndFeel() {
        try {
            // a) Try windows
            UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel"); //$NON-NLS-1$
        } catch (Exception e) {
            // b) Try system look & feel
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }
}
