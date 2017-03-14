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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.nfdz.jason.model.Snippet;

/**
 * Dummy implementation of snippets persistence. It stores all data in memory.
 */
public class DummyPersistence implements ISnippetsPersistence{
    
    private final Set<Snippet> mSnippets = new HashSet<Snippet>();
    private final List<IChangesListener> mListeners = new CopyOnWriteArrayList<>();

    @Override
    public void initPersistence() {
        // nothing to do
    }

    @Override
    public void insert(Snippet snippet) throws PersistenceException {
        if (mSnippets.add(snippet)) {
            for (IChangesListener listener : mListeners) {
                listener.notifyInsertedSnippet(snippet);
            }
        } else {
            throw new PersistenceException("Snippet is already stored.");
        }
    }

    @Override
    public void edit(Snippet original, Snippet edited) throws PersistenceException {
        boolean removedOriginal = mSnippets.remove(original);
        if (removedOriginal) {
            if (mSnippets.add(edited)) {
                for (IChangesListener listener : mListeners) {
                    listener.notifyEditedSnippet(original, edited);
                }
            } else {
                throw new PersistenceException("Edited snippet is already stored.");
            }
        } else {
            throw new PersistenceException("Original snippet is not stored in persistence.");
        }
    }

    @Override
    public void remove(Snippet snippet) throws PersistenceException {
        if (mSnippets.remove(snippet)) {
            for (IChangesListener listener : mListeners) {
                listener.notifyRemovedSnippet(snippet);
            }
        } else {
            throw new PersistenceException("Snippet is not stored in persistence.");
        }
    }

    @Override
    public Set<Snippet> getSnippets() {
        return Collections.unmodifiableSet(mSnippets);
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
