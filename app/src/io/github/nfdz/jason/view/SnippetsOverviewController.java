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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.nfdz.jason.MainApp;
import io.github.nfdz.jason.SnippetsRepository;
import io.github.nfdz.jason.SnippetsRepository.IOperationCallback;
import io.github.nfdz.jason.model.Filter;
import io.github.nfdz.jason.model.Filter.FilterType;
import io.github.nfdz.jason.model.Snippet;
import io.github.nfdz.jason.model.SnippetDateComparator;
import io.github.nfdz.jason.model.SnippetLanguageComparator;
import io.github.nfdz.jason.model.SnippetNameComparator;
import io.github.nfdz.jason.model.SortType;
import io.github.nfdz.jason.view.SnippetDialogController.OpenMode;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
        void selectedFilter(Filter filter);
        void selectedSort(SortType sort);
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
    
    private FilteredList<Snippet> mFilteredData;
    
    private SortedList<Snippet> mSortedData;
    
    private FilterType mFilterType;

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
    
    @FXML
    private ComboBox<String> mSortCombo;

    @FXML
    private TextField mFilterField;
    
    @FXML
    private ComboBox<String> mFilterCombo;
    
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
        
        // initialize sort combo
        mSortCombo.getItems().addAll(
                SortType.NAME.getText(),
                SortType.LANGUAGE.getText(),
                SortType.DATE.getText()
        );
        
        // initialize filter combo
        mFilterCombo.getItems().addAll(
                FilterType.NAME.getText(),
                FilterType.LANGUAGE.getText(),
                FilterType.TAGS.getText()                
        );
                
        // clear snippet details
        showSnippetDetails(null);

        // add listener of table selection changes
        mSnippetsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> { showSnippetDetails(newValue); notifySelectedSnippet(newValue);}); 
        // clear selection when double click
        mSnippetsTable.setOnMouseClicked(
                (e) -> { if(e.getClickCount() == 2) mSnippetsTable.getSelectionModel().select(null); });  
        
        // add listener of filter text field
        mFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (mFilteredData != null) {
                updateFilter(new Filter(newValue, mFilterType));
            }
        });
        
        // add listener of filter combo box
        mFilterCombo.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
                if (mFilteredData != null) {
                    FilterType filter = FilterType.parseText(newValue);
                    if (filter != mFilterType) {
                        mFilterType = filter;
                        updateFilter(new Filter(mFilterField.getText(), mFilterType));
                    }
                }
            }
        });
        
        // add listener of sort combo box
        mSortCombo.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
                if (mSortedData != null) {
                    SortType sort = SortType.parseText(newValue);
                    switch (sort) {
                        case NAME:
                            mSortedData.setComparator(new SnippetNameComparator());
                            break;
                        case LANGUAGE:
                            mSortedData.setComparator(new SnippetLanguageComparator());
                            break;
                        case DATE:
                            mSortedData.setComparator(new SnippetDateComparator());
                            break;
                        default:
                            
                    }
                    notifySelectedSort(sort);
                }
            }    
        });
        
    }
    
    private void updateFilter(Filter filter) {
        mFilteredData.setPredicate(snippet -> {
            // if filter is empty, display all snippets
            if (filter == null || 
                filter.getText() == null ||
                filter.getText().isEmpty() ||
                filter.getType() == null) {
                return true;
            }
            
            // compare name, language and tags with filter text
            String lowerCaseFilter = filter.getText().toLowerCase();

            switch(filter.getType()) {
                case NAME: return snippet.getName().toLowerCase().contains(lowerCaseFilter);
                case LANGUAGE: return snippet.getLanguage().toLowerCase().contains(lowerCaseFilter);
                case TAGS:
                    for (String tag : snippet.getTags()) {
                        if (tag.toLowerCase().contains(lowerCaseFilter)) return true; // filter matches this tag
                    }
            }
            return false; // does not match
        });
        notifySelectedFilter(filter);
    }
    
    /**
     * Inject snippets repository and set it in TableView.
     * @param repository
     */
    public void setRepository(SnippetsRepository repository) {
        mRepository = repository;
        mFilteredData = new FilteredList<>(mRepository.getReadableList(), s -> true);
        mSortedData = new SortedList<>(mFilteredData);
        mSnippetsTable.setItems(mSortedData);
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
    
    /**
     * Create a temporary file that contents the code of the given snippet. 
     * @param snippet
     * @return path to file
     * @throws IOException
     */
    private static String createSnippetCodeTempFile(Snippet snippet) throws IOException {
        String extension = "." + snippet.getLanguage().toLowerCase();
        Path path = Files.createTempFile(UUID.randomUUID().toString(), extension);
        Files.write(path, snippet.getCode().getBytes(StandardCharsets.UTF_8));
        File file = path.toFile();
        file.deleteOnExit();
        return file.getAbsolutePath();
    }    

    /**
     * TODO This method should use information from user preferences to determinate 
     * code editor application.
     */
    @FXML
    private void handleViewSnippet() {
        int selectedIndex = mSnippetsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) return;
        Snippet snippet = mSnippetsTable.getItems().get(selectedIndex);

        // create temp file with snippet
        String pathTempFile;
        try {
            pathTempFile = createSnippetCodeTempFile(snippet);
        } catch (IOException e1) {
            LOGGER.log(Level.WARNING, "Can not create a temp file with snippet code.", e1);
            return;
        }

        // try to open sublime
        try{
            new ProcessBuilder("sublime", pathTempFile).start();
            return;
        } catch (IOException  e) {
            LOGGER.log(Level.FINE, "Can not open sublime text editor.", e);
        }
        
        // try to open atom
        try{
            new ProcessBuilder("atom", pathTempFile).start();
            return;
        } catch (IOException  e) {
            LOGGER.log(Level.FINE, "Can not open atom editor.", e);
        }
        
        // open default built-in snippet dialog
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("SnippetDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
    
            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.APP_TITLE + " - View Snippet");
            dialogStage.setMinHeight(page.getMinHeight());
            dialogStage.setMinWidth(page.getMinWidth());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mViewButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
    
            SnippetDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setOpenMode(OpenMode.VISUALIZATION);
            controller.setSnippet(snippet);
            dialogStage.showAndWait();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not open view snippet dialog layout file.", e);
        }
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

    private void notifySelectedSnippet(Snippet snippet) {
        for (IOverviewListener listener : mListeners) {
            listener.selectedSnippet(snippet);
        }
    }
    
    private void notifySelectedFilter(Filter filter) {
        for (IOverviewListener listener : mListeners) {
            listener.selectedFilter(filter);
        }
    }
    
    private void notifySelectedSort(SortType sort) {
        for (IOverviewListener listener : mListeners) {
            listener.selectedSort(sort);
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
    
    public void selectFilter(Filter filter) {
        mFilterField.textProperty().set(filter.getText());
        mFilterCombo.setValue(filter.getType().getText());
    }
    
    public void selectSort(SortType sort) {
        mSortCombo.setValue(sort.getText());
    }
}
