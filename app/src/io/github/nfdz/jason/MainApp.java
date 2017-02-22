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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.nfdz.jason.model.Snippet;
import io.github.nfdz.jason.view.SnippetsOverviewController;
import io.github.nfdz.jason.view.SnippetsOverviewController.IOverviewListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// TODO: Persistencia en google drive y github. Por defecto sistema de ficheros.

/**
 * Main application class.
 */
public class MainApp extends Application {
	
	public static final String APP_TITLE = "JasonSnippets";
	
	private final static Logger LOGGER = Logger.getLogger(MainApp.class.getName());
	
	private final ObservableList<Snippet> mRepository = FXCollections.observableArrayList();;

    private final InternalListener mInternalListener;
    
    private Stage mPrimaryStage;
    private BorderPane mRootLayout;
    
    public MainApp() {
    	mInternalListener = new InternalListener();
    }
	
	@Override
	public void start(Stage primaryStage) {
		LOGGER.info("Starting application");
		
		mPrimaryStage = primaryStage;
		mPrimaryStage.setTitle(APP_TITLE);

        initRootLayout();
        SnippetsOverviewController controller = showSnippetsOverview();
        controller.setRepository(mRepository);
        controller.addListener(mInternalListener);
        loadRepository();
        viewLastSnippet(controller);
	}
	
	@Override
	public void stop() throws Exception {
		LOGGER.info("Stopping application");
		super.stop();
	}
	
	private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            mRootLayout = (BorderPane) loader.load();

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
            
            mRepository.add(new Snippet("aaaa1", "qqwerqwe", "qwer; qwer  ; qwerqwe", new ArrayList<>(),234234324));
            mRepository.add(new Snippet("bbbb2", "aasdfasdf", "qwer; qwer  ; qwerqwe", new ArrayList<>(),234234324));
            
        } catch (IOException e) {
        	LOGGER.log(Level.SEVERE, "Can not open snippets overview layout file.", e);
        	Platform.exit();
        }
        return controller;
    }
	
	private void loadRepository() {
		// TODO use persistence service
		
	}
	
	private void viewLastSnippet(SnippetsOverviewController controller) {
		Snippet lastSelectedSnippet = null;
		int hashCode = PreferencesUtils.getSelectedSnippetHashCode();
		for (Snippet snippet : mRepository) {
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
	}
}
