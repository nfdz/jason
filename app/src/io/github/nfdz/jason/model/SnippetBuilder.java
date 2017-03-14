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

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a builder of Snippet.
 */
public class SnippetBuilder {
    
    public String name;
    public String language;
    public List<String> tags;
    public long creationTime;
    public String code;
    
    public SnippetBuilder() {
        name = "";
        language = "";
        code = "";
        tags = new ArrayList<String>(0);
        creationTime = 0L;
    }
    
    public static SnippetBuilder newBuilder() {
        return new SnippetBuilder();
    }
    
    public SnippetBuilder initWith(Snippet snippet) {
        name = snippet.getName();
        language = snippet.getLanguage();
        code = snippet.getCode();
        tags = snippet.getTags();
        creationTime = snippet.getCreationTime();
        return this;
    }
    
    public SnippetBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public SnippetBuilder setLanguage(String language) {
        this.language = language;
        return this;
    }
    
    public SnippetBuilder setCode(String code) {
        this.code = code;
        return this;
    }
    
    public SnippetBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }
    
    public SnippetBuilder setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }
    
    public Snippet build() throws BuilderException {
        if (name == null || name.isEmpty()) {
            throw new BuilderException("Invalid name field");
        } else if (language == null || language.isEmpty()) {
            throw new BuilderException("Invalid language field");
        } else if (code == null || code.isEmpty()) {
            throw new BuilderException("Invalid code field");
        } else if (tags == null) {
            throw new BuilderException("Invalid tags field");
        } else if (creationTime <= 0L) {
            throw new BuilderException("Invalid creationTime field");
        }
        return new Snippet(name, language, code, tags, creationTime);
    }
    
    public static class BuilderException extends Exception {
        private static final long serialVersionUID = 1L;

        public BuilderException(String msg) {
            super(msg);
        }
    }

}
