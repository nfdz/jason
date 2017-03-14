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

import java.util.Set;

import io.github.nfdz.jason.model.Snippet;

/**
 * This interface has methods needed to manage snippets persistence tasks.
 */
public interface ISnippetsPersistence {
    
    /**
     * Performs initialization operations.
     * @throws PersistenceException
     */
    void initPersistence() throws PersistenceException;
    
    /**
     * Inserts given snippet.
     * @param snippet
     * @throws PersistenceException
     */
    void insert(Snippet snippet) throws PersistenceException;
    
    /**
     * Edits a snippet.
     * @param original snippet
     * @param edited snippet
     * @throws PersistenceException
     */
    void edit(Snippet original, Snippet edited) throws PersistenceException;
    
    /**
     * Removes given snippet.
     * @param snippet
     * @throws PersistenceException
     */
    void remove(Snippet snippet) throws PersistenceException;
    
    /**
     * Gets all snippets stored in persistence.
     * @return Set of Snippet
     * @throws PersistenceException
     */
    Set<Snippet> getSnippets() throws PersistenceException;;
    
    /**
     * Registers given changes listener.
     * @param listener
     */
    void addListener(IChangesListener listener);
    
    /**
     * Unregisters given changes listener.
     * @param listener
     */
    void removeListener(IChangesListener listener);
}
