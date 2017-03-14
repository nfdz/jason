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
package io.github.nfdz.jason.model.serialization;

/**
 * Custom exception for snippet serialization operations.
 */
public class SerializationException extends Exception {
    private static final long serialVersionUID = 1L;

    public SerializationException(String msg) {
        super(msg);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
    
    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
