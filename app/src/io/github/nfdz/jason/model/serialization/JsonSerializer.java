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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import io.github.nfdz.jason.model.Snippet;
import io.github.nfdz.jason.model.SnippetBuilder;
import io.github.nfdz.jason.model.SnippetBuilder.BuilderException;

/**
 * This implementation is based in JSON format. It uses an external library com.google.gson.
 */
public class JsonSerializer implements ISnippetSerializer {

    @Override
    public String serializeSnippet(Snippet snippet) throws SerializationException {
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SnippetBuilder builder = SnippetBuilder.newBuilder().initWith(snippet);
        String serialized = gson.toJson(builder);
        return serialized;
    }

    @Override
    public Snippet deserializeSnippet(String serializedSnippet) throws SerializationException {
        try {
            Gson gson = new Gson();
            SnippetBuilder snippetBuilder = gson.fromJson(serializedSnippet, SnippetBuilder.class);
            return snippetBuilder.build();
        } catch (JsonSyntaxException e1) {
            throw new SerializationException(e1);
        } catch (BuilderException e2) {
            throw new SerializationException(e2);
        }
    }
}
