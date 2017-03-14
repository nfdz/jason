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
package io.github.nfdz.jason.persistence;

/**
 * Custom exception for snippet persistence operations.
 */
public class PersistenceException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenceException(String msg) {
        super(msg);
    }
    
    public PersistenceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
