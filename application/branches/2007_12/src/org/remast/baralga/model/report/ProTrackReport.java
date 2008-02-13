package org.remast.baralga.model.report;

import java.io.File;

import org.remast.baralga.model.ProTrack;

public interface ProTrackReport {

    public void export(ProTrack data, File file) throws Exception;
}
