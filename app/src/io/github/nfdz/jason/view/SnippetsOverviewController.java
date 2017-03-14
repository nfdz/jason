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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.nfdz.jason.MainApp;
import io.github.nfdz.jason.SnippetsRepository;
import io.github.nfdz.jason.SnippetsRepository.IOperationCallback;
import io.github.nfdz.jason.model.Snippet;
import io.github.nfdz.jason.view.SnippetDialogController.OpenMode;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * This class is the controller of the SnippetsOverview view.
 */
public class SnippetsOverviewController {
    
    /** Interface of a listener that wants to know what snippet is showing */
    public static interface IOverviewListener {
        void selectedSnippet(Snippet snippet);
    }
    
    /** Tag separator used to format tag lists */
    private final static String TAG_SEPARATOR = ";";
    
    // default values when there is no snippet to show
    private final static String DEFAULT_NAME = "";
    private final static String DEFAULT_LANGUAGE = "";
    private final static String DEFAULT_TAGS = "";
    private final static String DEFAULT_CODE = "";
    
    private final static Logger LOGGER = Logger.getLogger(SnippetsOverviewController.class.getName());
    
    private final List<IOverviewListener> mListeners;
    
    private SnippetsRepository mRepository;

    @FXML
    private TableView<Snippet> mSnippetsTable;
    
    @FXML
    private TableColumn<Snippet, String> mNameColumn;
    
    @FXML
    private Label mNameLabel;
    
    @FXML
    private Label mLanguageLabel;
    
    @FXML
    private Label mTagsLabel;

    @FXML
    private TextArea mCodeText;

    @FXML
    private Button mRemoveButton;
    
    @FXML
    private Button mEditButton;
    
    @FXML
    private Button mViewButton;
    
    public SnippetsOverviewController() {
        mListeners = new CopyOnWriteArrayList<>();
    }

    @FXML
    private void initialize() {
        // initialize table
        mNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Snippet,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Snippet, String> param) {
                return new ReadOnlyObjectWrapper<String>(param.getValue().getName());
            }
        });
        
        // clear snippet details
        showSnippetDetails(null);

        // add listener of table selection changes
        mSnippetsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> { showSnippetDetails(newValue); notifyListeners(newValue);}); 
        // clear selection when double click
        mSnippetsTable.setOnMouseClicked(
                (e) -> { if(e.getClickCount() == 2) mSnippetsTable.getSelectionModel().select(null); });
    }
    
    /**
     * Inject snippets repository and set it in TableView.
     * @param repository
     */
    public void setRepository(SnippetsRepository repository) {
        mRepository = repository;
        mSnippetsTable.setItems(mRepository.getReadableList());
    }
    
    /**
     * Fill all text fields with given snippet information. If snippet is null,
     * there is no information to show and all fields will contain default value.
     * @param snippet
     */
    private void showSnippetDetails(Snippet snippet) {
        LOGGER.fine("Showing snippet: " + snippet);
        if (snippet == null) {
            mNameLabel.setText(DEFAULT_NAME);
            mLanguageLabel.setText(DEFAULT_LANGUAGE);
            mTagsLabel.setText(DEFAULT_TAGS);
            mCodeText.setText(DEFAULT_CODE);
            mRemoveButton.setDisable(true);
            mEditButton.setDisable(true);
            mViewButton.setDisable(true);
        } else {
            mNameLabel.setText(snippet.getName());
            mLanguageLabel.setText(snippet.getLanguage());
            mTagsLabel.setText(formatTagList(snippet.getTags()));
            mCodeText.setText(snippet.getCode());
            mRemoveButton.setDisable(false);
            mEditButton.setDisable(false);
            mViewButton.setDisable(false);
        }
    }
    
    /**
     * Format a list of tag in a String.
     * @param tags
     * @return String formated tag list
     */
    private String formatTagList(List<String> tags) {
        StringBuilder txt = new StringBuilder();
        Iterator<String> it = tags.iterator();
        while (it.hasNext()) {
            String tag = it.next();
            txt.append(tag);
            if (it.hasNext()) txt.append(TAG_SEPARATOR + " ");
        }
        return txt.toString();
    }
    
    /**
     * This method is called when user click remove button.
     */
    @FXML
    private void handleRemoveSnippet() {
        int selectedIndex = mSnippetsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) return;
        Snippet snippet = mSnippetsTable.getItems().get(selectedIndex);
        
        // ask confirmation
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(MainApp.APP_TITLE + " - Confirmation Remove Snippet");
        alert.setHeaderText("Are you sure you want to remove this snippet?");
        alert.setContentText(snippet.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // user chose OK
            LOGGER.fine("Removed snippet: " + snippet.getName());
            mRepository.removeSnippet(snippet, new IOperationCallback() {
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
                            alert.setTitle(MainApp.APP_TITLE + " - Remove Snippet Error");
                            alert.setHeaderText("There was an error removing snippet");
                            alert.setContentText(cause);
                            alert.showAndWait();
                        }
                     });
                }
            });
        } else {
            // user chose CANCEL or closed the dialog
        }
        
    }
    
    @FXML
    private void handleNewSnippet() {
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
            dialogStage.initOwner(mEditButton.getScene().getWindow());
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
    

    @FXML
    private void handleViewSnippet() {
        // TODO implement this
    }

    /**
     * This method is called when user click edit button.
     */
    @FXML
    private void handleEditSnippet() {
        int selectedIndex = mSnippetsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) return;
        Snippet original = mSnippetsTable.getItems().get(selectedIndex);
        Snippet noEdited = null;
        handleEditSnippet(original, noEdited);
    }
    
    private void handleEditSnippet(Snippet original, Snippet oldEdited) {
        Snippet edited;
        if (oldEdited != null) {
            edited = showSnippetEditDialog(oldEdited);
        } else {
            edited = showSnippetEditDialog(original);
        }
        
        if (edited != null && !edited.equals(original)) {
            LOGGER.fine("Edited snippet: " + edited.getName());
            mRepository.editSnippet(original, edited, new IOperationCallback() {
                @Override
                public void notifySuccess() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mSnippetsTable.getSelectionModel().select(edited);
                        }
                    });
                }
                @Override
                public void notifyFailure(String cause) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle(MainApp.APP_TITLE + " - Edit Snippet Error");
                            alert.setHeaderText("There was an error editing snippet");
                            alert.setContentText(cause);
                            alert.showAndWait();
                            handleEditSnippet(original, edited);
                        }
                     });
                }
            });
        }
    }
    
    /**
     * Open a snippet edit dialog
     * @param snippet
     * @return Edited snippet or null if there is no edition
     */
    private Snippet showSnippetEditDialog(Snippet snippet) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("SnippetDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.APP_TITLE + " - Edit Snippet");
            dialogStage.setMinHeight(page.getMinHeight());
            dialogStage.setMinWidth(page.getMinWidth());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mEditButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SnippetDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setOpenMode(OpenMode.EDITION);
            controller.setSnippet(snippet);

            dialogStage.showAndWait();

            return controller.getSnippet();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not open edit snippet dialog layout file.", e);
            return null;
        }
    }
    
    private void notifyListeners(Snippet snippet) {
        for (IOverviewListener listener : mListeners) {
            listener.selectedSnippet(snippet);
        }
    }
    
    /** Register a listener. It does not ensure first notification. */
    public void addListener(IOverviewListener listener) {
        mListeners.add(listener);
    }
    
    public void removeListener(IOverviewListener listener) {
        mListeners.remove(listener);
    }
    
    /**
     * Select given snippet in the view.
     * @param snippet or null (clear selection)
     */
    public void selectSnippet(Snippet snippet) {
        mSnippetsTable.getSelectionModel().select(snippet);
    }
}
