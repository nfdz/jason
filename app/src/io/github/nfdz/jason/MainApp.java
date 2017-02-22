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
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main application class.
 */
public class MainApp extends Application {
	
	public static final String APP_TITLE = "JasonSnippets";
	
	private final static Logger LOGGER = Logger.getLogger(MainApp.class.getName());
	
	private final ObservableList<Snippet> mRepository = FXCollections.observableArrayList();;
	
    private Stage mPrimaryStage;
    private BorderPane mRootLayout;
    
    public MainApp() {
    }
	
	@Override
	public void start(Stage primaryStage) {
		LOGGER.info("Starting application");
		
		mPrimaryStage = primaryStage;
		mPrimaryStage.setTitle(APP_TITLE);

        initRootLayout();
        showSnippetsOverview();
	}
	
	@Override
	public void stop() throws Exception {
		LOGGER.info("Stopping application");
		super.stop();
	}
	
	public void initRootLayout() {
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
        }
    }
	
	public void showSnippetsOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SnippetsOverview.fxml"));
            AnchorPane snippetsOverview = (AnchorPane) loader.load();

            mRootLayout.setCenter(snippetsOverview);
            
            SnippetsOverviewController controller = loader.getController();
            controller.setRepository(mRepository);
        } catch (IOException e) {
        	LOGGER.log(Level.SEVERE, "Can not open snippets overview layout file.", e);
        }
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
