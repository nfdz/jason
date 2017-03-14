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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.nfdz.jason.SnippetsRepository.IOperationCallback;
import io.github.nfdz.jason.model.Filter;
import io.github.nfdz.jason.model.Snippet;
import io.github.nfdz.jason.model.SortType;
import io.github.nfdz.jason.model.Filter.FilterType;
import io.github.nfdz.jason.model.serialization.JsonSerializer;
import io.github.nfdz.jason.persistence.ISnippetsPersistence;
import io.github.nfdz.jason.persistence.fs.FileSystemPersistence;
import io.github.nfdz.jason.view.RootLayoutController;
import io.github.nfdz.jason.view.SnippetsOverviewController;
import io.github.nfdz.jason.view.SnippetsOverviewController.IOverviewListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// TODO Persistence: google drive and github.

/**
 * Main application class.
 */
public class MainApp extends Application {

    public final static String APP_TITLE = "JasonSnippets";
    private final static Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    private final InternalListener mInternalListener;
    
    private SnippetsRepository mRepository;
    private ISnippetsPersistence mPersistence;

    private Stage mPrimaryStage;
    private BorderPane mRootLayout;

    public MainApp() {
        mInternalListener = new InternalListener();
    }

    @Override
    public void start(Stage primaryStage) {
        LOGGER.info("Starting application");

        mPersistence = resolvePersistence();
        mRepository = new SnippetsRepository(mPersistence);
        
        mPrimaryStage = primaryStage;
        mPrimaryStage.setTitle(APP_TITLE);
        mPrimaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));

        initRootLayout();
        SnippetsOverviewController controller = showSnippetsOverview();
        initRepository(controller);
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Stopping application");
        mRepository.stop();
        super.stop();
        System.exit(0);
    }
    
    private ISnippetsPersistence resolvePersistence() {
        // TODO use persistence defined in preferences
        return new FileSystemPersistence(new JsonSerializer());
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            mRootLayout = (BorderPane) loader.load();
            RootLayoutController controller = loader.getController();
            controller.setRepository(mRepository);

            Scene scene = new Scene(mRootLayout);
            mPrimaryStage.setScene(scene);
            mPrimaryStage.setMinHeight(mRootLayout.getMinHeight());
            mPrimaryStage.setMinWidth(mRootLayout.getMinWidth());
            mPrimaryStage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not open root layout file.", e);
            Platform.exit();
        }
    }

    private SnippetsOverviewController showSnippetsOverview() {
        SnippetsOverviewController controller = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SnippetsOverview.fxml"));
            AnchorPane snippetsOverview = (AnchorPane) loader.load();
            mRootLayout.setCenter(snippetsOverview);
            controller = loader.getController();
            controller.setRepository(mRepository);
            controller.addListener(mInternalListener);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not open snippets overview layout file.", e);
            Platform.exit();
        }
        return controller;
    }

    private void initRepository(SnippetsOverviewController controller) {
        mRepository.start(new IOperationCallback() {
            @Override
            public void notifySuccess() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        resumeApp(controller);
                    }
                });
            }
            @Override
            public void notifyFailure(String cause) {
                LOGGER.log(Level.SEVERE, "Can not initialize snippets repository. " + cause);
                Platform.exit();
            }
        });
    }

    /**
     * This method gets last information from preferences and try to resume as the user left it.
     * @param controller
     */
    private void resumeApp(SnippetsOverviewController controller) {
        // last selected filter
        Filter filter = PreferencesUtils.getSelectedFilter();
        if (filter == null) filter = new Filter("", FilterType.NAME);
        controller.selectFilter(filter);
        
        // last selected
        SortType sort = PreferencesUtils.getSelectedSort();
        if (sort == null) sort = SortType.NAME;
        controller.selectSort(sort);
        
        // last selected snippet
        Snippet lastSelectedSnippet = null;
        int hashCode = PreferencesUtils.getSelectedSnippetHashCode();
        for (Snippet snippet : mRepository.getReadableList()) {
            if (snippet.hashCode() == hashCode) {
                lastSelectedSnippet = snippet;
                break;
            }
        }
        controller.selectSnippet(lastSelectedSnippet);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class InternalListener implements IOverviewListener {
        @Override
        public void selectedSnippet(Snippet snippet) {
            PreferencesUtils.setSelectedSnippetHashCode(snippet != null ? snippet.hashCode() : -1);
        }

        @Override
        public void selectedFilter(Filter filter) {
            PreferencesUtils.setSelectedFilter(filter);
        }

        @Override
        public void selectedSort(SortType sort) {
            PreferencesUtils.setSelectedSort(sort);
        }
    }
}
