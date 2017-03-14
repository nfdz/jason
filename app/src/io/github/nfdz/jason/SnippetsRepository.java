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
package io.github.nfdz.jason;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.github.nfdz.jason.model.Snippet;
import io.github.nfdz.jason.persistence.IChangesListener;
import io.github.nfdz.jason.persistence.ISnippetsPersistence;
import io.github.nfdz.jason.persistence.PersistenceException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class manages a repository of snippets.
 */
public class SnippetsRepository {
    
    /**
     * Interface to be notified about repository operation result.
     */
    public static interface IOperationCallback {
        void notifySuccess();
        void notifyFailure(String cause);
    }

    private final InternalListener mListener;
    private final ISnippetsPersistence mPersistence;
    private final ObservableList<Snippet> mSnippetList;
    private final Executor mExecutor;

    /**
     * Constructor.
     * @param persistence implementation that will manage persistence issues.
     */
    public SnippetsRepository(ISnippetsPersistence persistence) {
        mListener = new InternalListener();
        mPersistence = persistence;
        mSnippetList = FXCollections.observableArrayList();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Start repository. That include initialize persistence, get all snippets
     * and subscribe listeners.
     */
    public void start(IOperationCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mPersistence.initPersistence();
                    mSnippetList.addAll(mPersistence.getSnippets());
                    mPersistence.addListener(mListener);
                    callback.notifySuccess();
                } catch (PersistenceException e) {
                    callback.notifyFailure(e.getMessage());
                }
            }
        });
    }

    /**
     * Unsubscribe listeners and clear list.
     */
    public void stop() {
        mPersistence.removeListener(mListener);
        mSnippetList.clear();
    }
    
    /**
     * 
     * @param snippet
     * @param callback This callback will not be called from a graphic thread.
     */
    public void addSnippet(Snippet snippet, IOperationCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mPersistence.insert(snippet);
                    callback.notifySuccess();
                } catch (PersistenceException e) {
                    callback.notifyFailure(e.getMessage());
                }
            }
        });
    }
    
    /**
     * 
     * @param snippet
     * @param callback This callback will not be called from a graphic thread.
     */
    public void removeSnippet(Snippet snippet, IOperationCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mPersistence.remove(snippet);
                    callback.notifySuccess();
                } catch (PersistenceException e) {
                    callback.notifyFailure(e.getMessage());
                }
            }
        });
    }
    
    /**
     * 
     * @param original
     * @param edited
     * @param callback This callback will not be called from a graphic thread.
     */
    public void editSnippet(Snippet original, Snippet edited, IOperationCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mPersistence.edit(original, edited);
                    callback.notifySuccess();
                } catch (PersistenceException e) {
                    callback.notifyFailure(e.getMessage());
                }
            }
        });
    }
    
    /**
     * Get a readable version of the list that contains all snippets.
     * This list is observable and will be updated automatically.
     * @return ObservableList of Snippets.
     */
    public ObservableList<Snippet> getReadableList() {
        return FXCollections.unmodifiableObservableList(mSnippetList);
    }
    
    private class InternalListener implements IChangesListener {

        @Override
        public void notifyInsertedSnippet(Snippet snippet) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mSnippetList.add(snippet);
                }
            });
        }
        
        @Override
        public void notifyRemovedSnippet(Snippet snippet) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mSnippetList.remove(snippet);
                }
            });
        }
        
        @Override
        public void notifyEditedSnippet(Snippet original, Snippet edited) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mSnippetList.remove(original);
                    mSnippetList.add(edited);
                }
            });
        }
    }
}
