package com.neelhridoy.dupfinder.task;

import com.neelhridoy.dupfinder.gui.DupeFinderGui;
import com.neelhridoy.dupfinder.util.Util;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: PRADATTA
 * Date: 16/6/13
 * Time: 4:56 PM
 */
public class DupeFinderTask extends SwingWorker<List<String>, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DupeFinderGui.class);
    private final String filePath;
    private final Collection<String> driveToSearch;
    File sourceFile;
    boolean driveLevelFileFoundFlag = false;
    private int progress = 0;
    private List<String> foundFiles;
    private long fileCount;

    public DupeFinderTask(String filePath, Collection<String> drives) {
        this.filePath = filePath;
        sourceFile = new File(filePath);
        this.driveToSearch = drives;
    }

    @Override
    protected List<String> doInBackground() {
        return findDupeForSingleFile();
    }

    private List<String> findDupeForSingleFile() {
        fileCount = 0;
        this.firePropertyChange("filecount", null, fileCount);
        foundFiles = new ArrayList<>();
        LOGGER.info(Util.getString("application.dupe.finder.scanning.started"));
        firePropertyChange("publish_newline", "Scanning started ...", null);

        LOGGER.info(Util.getString("application.dupe.finder.scanning.result.line.separator.long"));
        LOGGER.info(MessageFormat.format(Util.getString("application.dupe.finder.file.scanning.info"), filePath));
        LOGGER.info(Util.getString("application.dupe.finder.scanning.result.line.separator.long"));

        firePropertyChange("publish_newline", Util.getString("application.dupe.finder.scanning.result.line.separator.long"), Color.MAGENTA.darker().darker());
        firePropertyChange("publish_newline", Util.getString("application.dupe.finder.file.scanning.general.info") , Color.GREEN.darker().darker());
        firePropertyChange("publish", filePath, Color.BLUE);
        firePropertyChange("publish_newline", Util.getString("application.dupe.finder.scanning.result.line.separator.long"), Color.MAGENTA.darker().darker());


        for (String driveRoot : driveToSearch) {
            driveLevelFileFoundFlag = false;
            if (isCancelled()) return null;
            File root = new File(driveRoot);
            try {
                if (isCancelled()) return null;
                LOGGER.info(MessageFormat.format(Util.getString("application.dupe.finder.scanning.message"), root));
                firePropertyChange("publish_newline", MessageFormat.format(Util.getString("application.dupe.finder.scanning.message") + Util.getString("application.result.console.blink.char"), root), null);
                firePropertyChange("blink", null, null);
                iterateFileToCompare(root.listFiles());

            } catch (IllegalArgumentException ie) {
                LOGGER.warn(MessageFormat.format(Util.getString("application.dupe.finder.root.scanning.error,message"), root));
                firePropertyChange("publish_newline", MessageFormat.format(Util.getString("application.dupe.finder.root.scanning.error,message"), root), null);
            } catch (InterruptedException e) {
                return null;
            } finally {
                if (isCancelled()) return null;
                firePropertyChange("blinkstop", null, null);
            }
        }
        return null;
    }

    private void iterateFileToCompare(File[] path) throws InterruptedException {
        if (isCancelled()) throw new InterruptedException();
        if (path == null)
            return;
        for (File file : path) {
            if (isCancelled()) throw new InterruptedException();
            if (file.isDirectory()) {
                iterateFileToCompare(file.listFiles());
            } else {
                processFile(file);
            }
        }
    }

    private void processFile(File file) throws InterruptedException {
        if (isCancelled()) throw new InterruptedException();
        try {
            fileCount++;
            this.firePropertyChange("filecount", null, fileCount);
            if (!file.getAbsolutePath().equals(filePath)) {
                if (isCancelled()) throw new InterruptedException();
                if (file.length() == sourceFile.length()) {
                    if (isCancelled()) throw new InterruptedException();
                    String sourceMD5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(new FileInputStream(sourceFile));
                    String fileMD5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(new FileInputStream(file));
                    if (isCancelled()) throw new InterruptedException();
                    if (sourceMD5.equals(fileMD5) || file.getName().equals(sourceFile.getName())) {
                        if (isCancelled()) throw new InterruptedException();
                        if (FileUtils.contentEquals(sourceFile, file)) {
                            if (isCancelled()) throw new InterruptedException();
                            firePropertyChange("blinkstop", null, null);
                            LOGGER.info(MessageFormat.format(Util.getString("application.duplicate.file.found.message"), file.getAbsolutePath()));
                            firePropertyChange("publish_newline", Util.getString("application.duplicate.file.found.general.message"), Color.GREEN.darker().darker());
                            firePropertyChange("publish", file.getAbsolutePath(), Color.BLUE);
                            foundFiles.add(file.getAbsolutePath());
                            driveLevelFileFoundFlag = true;
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            LOGGER.warn(ioe.getLocalizedMessage(), ioe.fillInStackTrace());
        }
    }

    @Override
    protected void done() {
        firePropertyChange("blinkstop", null, null);
        setProgress(100);
        firePropertyChange("publish_newline", "\n" + Util.getString("application.dupe.finder.scanning.result.scanning.completed"), Color.GREEN.darker().darker());
        LOGGER.info(Util.getString("application.dupe.finder.scanning.result.scanning.completed"));
        LOGGER.info(Util.getString("application.dupe.finder.scanning.result.line.separator"));
        if (foundFiles.size() == 0) {
            firePropertyChange("publish_newline",
                    "\n" + Util.getString("application.dupe.finder.scanning.result.line.separator") + "\n" +
                            Util.getString("application.dupe.finder.scanning.result.no.dupe.found") +
                            "\n" + Util.getString("application.dupe.finder.scanning.result.line.separator") + "\n",
                    Color.RED.darker());
            LOGGER.info(Util.getString("application.dupe.finder.scanning.result.no.dupe.found"));
            LOGGER.info(Util.getString("application.dupe.finder.scanning.result.line.separator"));
        } else {
            firePropertyChange("publish_newline",
                    "\n" + Util.getString("application.dupe.finder.scanning.result.line.separator") + "\n"
                            + MessageFormat.format(Util.getString("application.dupe.finder.scanning.result.dupe.found"), foundFiles.size()) +
                            "\n" + Util.getString("application.dupe.finder.scanning.result.line.separator") + "\n",
                    Color.GREEN.darker().darker());
            LOGGER.info(foundFiles.size() + " duplicate file(s) found.");
            LOGGER.info(Util.getString("application.dupe.finder.scanning.result.line.separator"));
        }
        firePropertyChange("finished", null, null);
    }


}
