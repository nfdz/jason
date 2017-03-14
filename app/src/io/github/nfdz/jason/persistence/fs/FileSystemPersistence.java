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

import java.util.Set;

import io.github.nfdz.jason.model.Snippet;
import io.github.nfdz.jason.persistence.IChangesListener;
import io.github.nfdz.jason.persistence.ISnippetsPersistence;
import io.github.nfdz.jason.persistence.PersistenceException;

/**
 * This implementation
 */
public class FileSystemPersistence implements ISnippetsPersistence {

    @Override
    public void initPersistence() throws PersistenceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void insert(Snippet snippet) throws PersistenceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void edit(Snippet original, Snippet edited) throws PersistenceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void remove(Snippet snippet) throws PersistenceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Set<Snippet> getSnippets() throws PersistenceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addListener(IChangesListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeListener(IChangesListener listener) {
        // TODO Auto-generated method stub
        
    }



}
