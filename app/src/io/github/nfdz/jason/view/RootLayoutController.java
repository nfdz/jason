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
package io.github.nfdz.jason.view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.nfdz.jason.MainApp;
import io.github.nfdz.jason.SnippetsRepository;
import io.github.nfdz.jason.SnippetsRepository.IOperationCallback;
import io.github.nfdz.jason.model.Snippet;
import io.github.nfdz.jason.view.SnippetDialogController.OpenMode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class is the controller of the root layout.
 */
public class RootLayoutController {
    
    private final static Logger LOGGER = Logger.getLogger(RootLayoutController.class.getName());
    
    private SnippetsRepository mRepository;
    
    /**
     * Inject snippets repository.
     * @param repository
     */
    public void setRepository(SnippetsRepository repository) {
        mRepository = repository;
    }

    /**
     * Opens snippet creation dialog.
     */
    @FXML
    public void handleNewSnippet() {
        Snippet noSnippet = null;
        handleNewSnippet(noSnippet);
    }
    
    private void handleNewSnippet(Snippet newSnippet) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("SnippetDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.APP_TITLE + " - New Snippet");
            dialogStage.setMinHeight(page.getMinHeight());
            dialogStage.setMinWidth(page.getMinWidth());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SnippetDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setOpenMode(OpenMode.CREATION);
            if (newSnippet != null) controller.setSnippet(newSnippet);
            dialogStage.showAndWait();
            
            Snippet snippet = controller.getSnippet();
            
            if (snippet != null) {
                mRepository.addSnippet(snippet, new IOperationCallback() {
                    @Override
                    public void notifySuccess() {
                        // nothing to do
                    }
                    @Override
                    public void notifyFailure(String cause) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert alert = new Alert(AlertType.ERROR);
                                alert.setTitle(MainApp.APP_TITLE + " - New Snippet Error");
                                alert.setHeaderText("There was an error creating new snippet");
                                alert.setContentText(cause);
                                alert.showAndWait();
                                handleNewSnippet(snippet);
                            }
                         });
                    }
                });
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not open new snippet dialog layout file.", e);
        }
    }
    
    /**
     * Opens preference dialog.
     */
    @FXML
    public void handlePreferences() {
        // TODO implement this
    }
    
    /**
     * Closes the application.
     */
    @FXML
    private void handleClose() {
        Platform.exit();
    }
    
    /**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(MainApp.APP_TITLE + " - About");
        alert.setHeaderText("About Jason Snippets Application");
        alert.setContentText("Author: nfdz\nWebsite: https://github.com/nfdz/jason");
        alert.showAndWait();
    }
    
    /**
     * Opens a help dialog.
     */
    @FXML
    private void handleHelp() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(MainApp.APP_TITLE + " - Help");
        alert.setHeaderText("Jason Snippets Application Help");
        alert.setContentText("TODO. \nVisit application website:\nhttps://github.com/nfdz/jason");
        alert.showAndWait();
    }

}
