package com.neelhridoy.dupfinder.util;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: PRADATTA
 * Date: 9/1/13
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    private static ResourceBundle resourceBundle;


    static {
        Locale locale = Locale.getDefault();
        resourceBundle = PropertyResourceBundle.getBundle("resourcebundle", locale);

    }


    public static String getString(String key) {
        return resourceBundle.getString(key);
    }

    public static void appendToPane(JTextPane resultArea, String s) {
        appendToPane(resultArea, s, Color.BLACK);
    }

    public static void appendToPane(JTextPane resultArea, String msg, Color color) {
        appendToPane(resultArea, msg, color, true);
    }

    public synchronized static void appendToPane(JTextPane resultArea, String msg, Color color, boolean isTrue) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet asset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        asset = sc.addAttribute(asset, StyleConstants.FontFamily, Util.getString("application.result.pane.font"));
        asset = sc.addAttribute(asset, StyleConstants.FontSize, Util.getString("application.result.pane.font.size"));
        asset = sc.addAttribute(asset, StyleConstants.Alignment, StyleConstants.ALIGN_LEFT);
        int len = resultArea.getDocument().getLength();
        if (isTrue && len > 0)
            msg = "\n" + msg;
        resultArea.setCaretPosition(len);
        resultArea.setCharacterAttributes(asset, false);
        resultArea.setEditable(true);
        resultArea.replaceSelection(msg);
        resultArea.setEditable(false);
    }

}
