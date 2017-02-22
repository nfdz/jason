/*
 * (C) Copyright 2017 Jason (https://github.com/nfdz/jason).
 *
 * Licensed under the GNU General Public License, Version 3;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Contributors:
 *     nfdz
 */
package io.github.nfdz.jason;

import java.util.prefs.Preferences;

public class PreferencesUtils {
    
    private final static String OPENED_SNIPPET_KEY = "openedSnippet";

    
    /**
     * Returns the hash code of the last opened snippet. If no such
     * preference can be found, -1 is returned.
     * @return
     */
    public static int getSelectedSnippetHashCode() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        return prefs.getInt(OPENED_SNIPPET_KEY, -1);
    }
    
    /**
     * Set the hash code of the currently opened snippet.
     * @param hashCode of the snippet or -1 if there is no opened snippet.
     */
    public static void setSelectedSnippetHashCode(int hashCode) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (hashCode == -1) {
            // there is no opened snippet, clear last one
            prefs.remove(OPENED_SNIPPET_KEY);
        } else {
            prefs.putInt(OPENED_SNIPPET_KEY, hashCode);
        }
    }
}
