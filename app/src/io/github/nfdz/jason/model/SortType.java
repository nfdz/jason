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
package io.github.nfdz.jason.model;

public enum SortType {
    
    NAME("Name"),
    LANGUAGE("Language"),
    DATE("Modification date");
    
    private final String mText;
    
    private SortType(String text) {
        mText = text;
    }
    
    public String getText() {
        return mText;
    }
    
    /**
     * This method returns the enum that has got the same given text.
     * @return SortType related enum or null
     */
    public static SortType parseText(String text) {
        if (text.equals(NAME.getText())) {
            return SortType.NAME;
        } else if (text.equals(LANGUAGE.getText())) {
            return SortType.LANGUAGE;
        } else if (text.equals(DATE.getText())) {
            return SortType.DATE;
        } else {
            return null;
        }
    }
}
