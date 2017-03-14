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

import io.github.nfdz.jason.model.Filter;
import io.github.nfdz.jason.model.SortType;
import io.github.nfdz.jason.model.Filter.FilterType;

public class PreferencesUtils {
    
    private final static String OPENED_SNIPPET_KEY = "openedSnippet";
    private final static String FILTER_TYPE_KEY = "filterType";
    private final static String FILTER_TEXT_KEY = "filterText";
    private final static String SORT_KEY = "sortBy";

    
    /**
     * Returns the hash code of the last opened snippet. If no such
     * preference can be found, -1 is returned.
     * @return int snippet hash code
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

    /**
     * Gets the last selected filter stored. If no such preference can be found
     * it will return null.
     * @return Filter or null
     */
    public static Filter getSelectedFilter() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String text = prefs.get(FILTER_TEXT_KEY, "");
        String typeText = prefs.get(FILTER_TYPE_KEY, "");
        FilterType type = FilterType.parseText(typeText);
        return type == null ? null : new Filter(text, type);
    }
    
    public static void setSelectedFilter(Filter filter) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (filter == null || 
            filter.getText() == null ||
            filter.getText().isEmpty() ||
            filter.getType() == null) {
            // there is no filter, clear last one
            prefs.remove(FILTER_TEXT_KEY);
        } else {
            prefs.put(FILTER_TEXT_KEY, filter.getText());
            prefs.put(FILTER_TYPE_KEY, filter.getType().getText());
        }
    }

    /**
     * Gets the last selected sort stored. If no such preference can be found
     * it will return null.
     * @return SortType or null
     */
    public static SortType getSelectedSort() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        return SortType.parseText(prefs.get(SORT_KEY, ""));
    }
    
    public static void setSelectedSort(SortType sort) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (sort == null) {
            // there is no sort, clear last one
            prefs.remove(SORT_KEY);
        } else {
            prefs.put(SORT_KEY, sort.getText());
        }
    }
}
