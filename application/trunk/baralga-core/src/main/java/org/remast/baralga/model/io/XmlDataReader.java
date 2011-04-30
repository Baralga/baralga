//---------------------------------------------------------
// $Id$ 
// 
// (c) 2011 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.baralga.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

public class XmlDataReader {
	
    /**
     * Actually read the data from file.
     * @throws IOException
     */
    public void read(final File file) throws IOException {
        final InputStream fis = new FileInputStream(file);
        try {
            read(fis);
        } catch (IOException e) {
            throw new IOException("The file " + (file != null ? file.getName() : "<null>") + " does not contain valid Baralga data.", e);
        } finally {
        	fis.close();
        }
    }

    /**
     * Read the data from an {@link InputStream}.
     * @throws IOException
     */
    public void read(final InputStream in) throws IOException {
    }

	public Collection<Project> getProjects() {
		return null;
	}

	public Collection<ProjectActivity> getActivities() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
