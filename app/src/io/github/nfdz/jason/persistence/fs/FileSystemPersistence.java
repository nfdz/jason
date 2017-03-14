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
package io.github.nfdz.jason.persistence.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.nfdz.jason.model.Snippet;
import io.github.nfdz.jason.model.serialization.ISnippetSerializer;
import io.github.nfdz.jason.model.serialization.SerializationException;
import io.github.nfdz.jason.persistence.IChangesListener;
import io.github.nfdz.jason.persistence.ISnippetsPersistence;
import io.github.nfdz.jason.persistence.PersistenceException;

/**
 * This implementation
 */
public class FileSystemPersistence implements ISnippetsPersistence {

    private final static Logger LOGGER = Logger.getLogger(FileSystemPersistence.class.getName());
    
    private final static String FILENAME_EXCEPTION = "Name must contains only these characters: \nA-Z, a-z, 0-9, -, _ and white spaces.";
    private final static String SNIPPETS_FOLDER = "Snippets";
    private final static String SNIPPETS_EXTENSION = "jason";

    private final List<IChangesListener> mListeners;
    private final ISnippetSerializer mSerializer;
    
    private Path mSnippetsPath;
    
    public FileSystemPersistence(ISnippetSerializer serializer) {
        mListeners = new CopyOnWriteArrayList<>();
        mSerializer = serializer;
    }
    
    @Override
    public void initPersistence() throws PersistenceException {
        mSnippetsPath = Paths.get(SNIPPETS_FOLDER);
        try {
            mSnippetsPath = Files.createDirectory(mSnippetsPath);
        } catch(FileAlreadyExistsException e){
            // the directory already exists
            if (!Files.isDirectory(mSnippetsPath)) {
                String msg = "There is a file with the same name of snippets folder: " + SNIPPETS_FOLDER;
                throw new PersistenceException(msg);
            }
        } catch (IOException e) {
            //something else went wrong
            throw new PersistenceException(e);
        }
    }

    @Override
    public void insert(Snippet snippet) throws PersistenceException {
        try {
            String content = mSerializer.serializeSnippet(snippet);
            checkName(snippet.getName());
            Path file = Paths.get(mSnippetsPath.toString() + File.separator + snippet.getName() + "." + SNIPPETS_EXTENSION);
            if (Files.exists(file)) {
                throw new PersistenceException("There is other snippet with the same name: " + snippet.getName());
            }
            Files.write(file, content.getBytes());
            for (IChangesListener listener : mListeners) {
                listener.notifyInsertedSnippet(snippet);
            }
        } catch (IOException | SerializationException e) {
            throw new PersistenceException(e);
        }
    }
    
    private void checkName(String name) throws PersistenceException {
        for (char l : name.toCharArray()) {
            switch (l) {
                case '-':
                case '_':
                case ' ':
                    continue;
            }
            if (l >= 'a' && l <= 'z') continue;
            if (l >= 'A' && l <= 'Z') continue;
            if (l >= '0' && l <= '9') continue;
            throw new PersistenceException(FILENAME_EXCEPTION);
        }
    }

    @Override
    public void edit(Snippet original, Snippet edited) throws PersistenceException {
        checkName(edited.getName());
        remove(original);
        insert(edited);
    }

    @Override
    public void remove(Snippet snippet) throws PersistenceException {
        try {
            Path file = Paths.get(mSnippetsPath.toString() + File.separator + snippet.getName() + "." + SNIPPETS_EXTENSION);
            if (!Files.exists(file)) {
                throw new PersistenceException("There is not a snippet with that name: " + snippet.getName());
            }
            Files.delete(file);
            for (IChangesListener listener : mListeners) {
                listener.notifyRemovedSnippet(snippet);
            }
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Set<Snippet> getSnippets() throws PersistenceException {
        Set<Snippet> snippets = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(mSnippetsPath, "*.{" + SNIPPETS_EXTENSION + "}")) {
            for (Path entry: stream) {
                if (Files.isRegularFile(entry)) {
                    try {
                        String content = new String(Files.readAllBytes(entry));
                        Snippet snippet = mSerializer.deserializeSnippet(content);
                        snippets.add(snippet);
                    } catch (IOException e1) {
                        LOGGER.log(Level.INFO, "Can not read a snippet file: " + entry.toString(), e1);
                    } catch (SerializationException e2) {
                        LOGGER.log(Level.INFO, "Can not deserialize a snippet file: " + entry.toString(), e2);
                    } 
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Can not read a snippets directory. ", e);
        }
        
        return snippets;
    }

    @Override
    public void addListener(IChangesListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void removeListener(IChangesListener listener) {
        mListeners.remove(listener);
    }
}
