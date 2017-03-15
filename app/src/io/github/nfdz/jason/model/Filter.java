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

public class Filter {

    private final String mText;
    private final FilterType mType;
    
    public Filter(String text, FilterType type) {
        mText = text;
        mType = type;
    }
    
    public String getText() {
        return mText;
    }
    
    public FilterType getType() {
        return mType;
    }    
    
    public static enum FilterType {
        
        NAME("Name"),
        LANGUAGE("Language"),
        TAGS("Tags");
        
        private final String mText;
        
        private FilterType(String text) {
            mText = text;
        }
        
        public String getText() {
            return mText;
        }
        
        public static FilterType parseText(String text) {
            if (text.equals(NAME.getText())) {
                return FilterType.NAME;
            } else if (text.equals(LANGUAGE.getText())) {
                return FilterType.LANGUAGE;
            } else if (text.equals(TAGS.getText())) {
                return FilterType.TAGS;
            } else {
                return null;
            }
        }
    }
}
