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

import java.util.Comparator;

public class SnippetNameComparator implements Comparator<Snippet> {

    @Override
    public int compare(Snippet snippet1, Snippet snippet2) {
        return snippet1.getName().compareTo(snippet2.getName());
    }

}
