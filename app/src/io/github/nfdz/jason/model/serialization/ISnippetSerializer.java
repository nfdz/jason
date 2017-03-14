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

import io.github.nfdz.jason.model.Snippet;

/**
 * Interface that defines all methods to serialize and deserialize snippets.
 */
public interface ISnippetSerializer {
    
    /**
     * This method serializes a snippet in a string.
     * @param snippet
     * @return serialized string snippet
     * @throws SerializationException
     */
    String serializeSnippet(Snippet snippet) throws SerializationException;
    
    /**
     * This method deserializes a string snippet.
     * @param serializedSnippet
     * @return deserialized snippet
     * @throws SerializationException
     */
    Snippet deserializeSnippet(String serializedSnippet) throws SerializationException;
}
