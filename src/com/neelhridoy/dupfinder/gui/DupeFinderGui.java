package com.neelhridoy.dupfinder.gui;

import com.neelhridoy.dupfinder.task.DupeFinderTask;
import com.neelhridoy.dupfinder.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

/**
 * User: PRADATTA
 * Date: 16/6/13
 * Time: 2:25 PM
 */
public class DupeFinderGui {
    private final Collection<String> driveToSearch = new HashSet<>();
    Timer blinkTimer;
    String activeText = null;
    private DupeFinderTask task;
    private JTextField fileSource;
    private JButton browse;
    private JButton find;
    private JPanel mainPanel;
    private JProgressBar progressBar;
    private JTextPane result;
    private JLabel fileCountMessage;
    private JLabel count;
    private JPanel statusPanel;
    private JPanel driveListPanel;
    private JPanel checkBoxPanel;
    private JLabel title;
    private JButton stop;
    private JLabel banner;
    private JPanel headerPanel;
    private JLabel fileLabel;
    private String originalText;

    public DupeFinderGui() {
        setDefaultStyleUI();
        toggleToolState(true);
        progressBar.setVisible(false);
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setVisible(false);
                result.setText("");
                fileSource.setText("");
                count.setText("0 ");
                fileDialog();
            }
        });

        find.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result.setText("");
                if (!fileSource.getText().isEmpty()) {
                    /*File fileToScan = new File(fileSource.getText());
                    if (fileToScan.isDirectory()) {
                        iterateFileToCompare(fileToScan.listFiles());
                    } else*/
                        findDupe(fileSource.getText());
                } else {
                    Util.appendToPane(result, Util.getString("application.dupe.finder.scanning.result.line.separator"), Color.RED.darker(), true);
                    Util.appendToPane(result, Util.getString("application.dupe.finder.no.file.selected.error.message"), Color.RED.darker(), true);
                    Util.appendToPane(result, Util.getString("application.dupe.finder.scanning.result.line.separator"), Color.RED.darker(), true);
                }
            }
        });
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                task.cancel(true);
                progressBar.setVisible(false);
                toggleToolState(true);
            }
        });
    }

    private void iterateFileToCompare(File[] files){
        if (files == null)
            return;
        for (File file : files) {
            if (file.isDirectory()) {
                iterateFileToCompare(file.listFiles());
            } else {
                findDupe(file.getAbsolutePath());
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(Util.getString("application.look.and.feel.class.name"));
        JFrame frame = new JFrame(Util.getString("duplicate.file.finder.title"));
        DupeFinderGui dupeFinderGui = new DupeFinderGui();
        frame.setContentPane(dupeFinderGui.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setMinimumSize(new Dimension(700, 500));
        frame.setLocationRelativeTo(null);
        dupeFinderGui.loadDriveList();
        URL imgURL = DupeFinderGui.class.getResource(Util.getString("application.dupe.finder.frame.icon"));
        Image image = new ImageIcon(imgURL).getImage();
        frame.setIconImage(image);
        frame.setVisible(true);
    }

    private void findDupe(String filePath) {
        toggleToolState(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        progressBar.setString(Util.getString("application.progressbar.text"));
        task = new DupeFinderTask(filePath, driveToSearch);
        task.addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            Integer percentage = (Integer) evt.getNewValue();
                            if (percentage == 100) {
                                progressBar.setString(Util.getString("application.dupe.finder.completed.message"));
                                progressBar.setIndeterminate(false);
                                progressBar.setValue(100);
                            }
                        }
                        if ("filecount".equals(evt.getPropertyName())) {
                            count.setText(evt.getNewValue().toString() + " ");
                        }
                        if ("finished".equals(evt.getPropertyName())) {
                            toggleToolState(true);
                        }
                        if ("blink".equals(evt.getPropertyName())) {
                            blinkText(Util.getString("application.result.console.blink.text"), 3, 300);
                        }
                        if ("blinkstop".equals(evt.getPropertyName())) {
                            blinkStop(3);
                        }
                        if ("publish_newline".equals(evt.getPropertyName())) {
                            Color color = null;
                            if (evt.getNewValue() == null)
                                color = Color.BLACK;
                            else
                                color = (Color) evt.getNewValue();
                            Util.appendToPane(result, evt.getOldValue().toString(), color);
                        }
                        if ("publish".equals(evt.getPropertyName())) {
                            Color color = null;
                            if (evt.getNewValue() == null)
                                color = Color.BLACK;
                            else
                                color = (Color) evt.getNewValue();
                            Util.appendToPane(result, evt.getOldValue().toString(), color, false);
                        }
                    }
                });
        task.execute();
    }

    private void setDefaultStyleUI() {
        fileSource.setFont(new Font(Util.getString("application.result.pane.font"), Font.PLAIN, 12));
    }

    protected void toggleToolState(boolean state) {
        for (int i = 0; i < checkBoxPanel.getComponentCount(); i++) {
            checkBoxPanel.getComponent(i).setEnabled(state);
        }
        checkBoxPanel.updateUI();
        browse.setEnabled(state);
        find.setEnabled(state);
        stop.setEnabled(!state);
    }

    private void loadDriveList() {
        File[] roots = File.listRoots();
        for (int i = 0; i < roots.length; i++) {
            File root = roots[i];
            JLabel driveLabel = new JLabel(root.toString().replace("\\", ""));
            DriveCheckBox checkBox = new DriveCheckBox(root.toString());
            checkBox.setSelected(true);
            checkBox.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    DriveCheckBox source = (DriveCheckBox) e.getSource();
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        driveToSearch.add(source.getValue());
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        driveToSearch.remove(source.getValue());
                    }
                    if (driveToSearch.size() == 0) {
                        find.setEnabled(false);
                    } else {
                        find.setEnabled(true);
                    }
                }
            });
            driveToSearch.add(root.toString());
            checkBoxPanel.add(driveLabel);
            checkBoxPanel.add(checkBox);
            if (i < roots.length - 1)
                checkBoxPanel.add(new JLabel(", "));
        }
    }

    private void fileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle(Util.getString("application.dupe.finder.file.dialog.title"));
        fileChooser.setApproveButtonText(Util.getString("application.dupe.finder.file.dialog.select"));
        fileChooser.showOpenDialog(this.mainPanel);
        if (fileChooser.getSelectedFile() != null) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            fileSource.setText(filePath);
        }
    }

    public synchronized void blinkText(final String blinkMsg, final int blinkCharCount, final int blinkRate) {
        final int len = result.getDocument().getLength();
        result.setSelectionStart(len - blinkCharCount);
        result.setSelectionEnd(len);
        try {
            originalText = result.getText(len - blinkCharCount, blinkCharCount);
            activeText = result.getText(len - blinkCharCount, blinkCharCount);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        blinkTimer = new Timer(blinkRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                result.setSelectionStart(len - blinkCharCount);
                result.setSelectionEnd(len);
                try {
                    activeText = result.getText(len - blinkCharCount, blinkCharCount);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                if (activeText.equals(originalText)) {
                    result.setEditable(true);
                    result.replaceSelection(blinkMsg);
                    activeText = blinkMsg;
                    result.setEditable(false);
                } else {
                    result.setEditable(true);
                    result.replaceSelection(originalText);
                    activeText = originalText;
                    result.setEditable(false);
                }
                result.repaint();
            }
        });
        blinkTimer.start();
    }

    public synchronized void blinkStop(int blinkCharCount) {
        if (blinkTimer != null) {
            int len = result.getDocument().getLength();
            result.setSelectionStart(len - blinkCharCount);
            result.setSelectionEnd(len);
            try {
                if (len > 3) {
                    if (result.getText(len - blinkCharCount, blinkCharCount).equals("...")) {
                        result.setEditable(true);
                        result.replaceSelection("   ");
                        result.setEditable(false);
                    }
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            } finally {
                blinkTimer.stop();
            }
        }
    }

    private class DriveCheckBox extends JCheckBox {
        String value;

        DriveCheckBox(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
