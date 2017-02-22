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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.nfdz.jason.MainApp;
import io.github.nfdz.jason.model.Snippet;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * This class is the controller of the SnippetDialog view.
 */
public class SnippetDialogController {
	
	/** This is the flag used to know what kind of dialog it is */
	public static enum OpenMode { CREATION, EDITION };
	
	/** Tag separator expected and used to show tag list */ 
	private final static String TAG_SEPARATOR = ";";

	@FXML
	private TextField mNameField;
	
	@FXML
	private TextField mLanguageField;
	
	@FXML
	private TextField mTagsField;
	
	@FXML
	private TextArea mCodeArea;
	
	@FXML
	private Button mFinishButton;
	
	private Stage mDialogStage;
	
	private Snippet mSnippet = null;
		 
	@FXML
    private void initialize() {
        mTagsField.setPromptText("Use " + TAG_SEPARATOR + " to separate tags");
    }
	
	/**
	 * Inject dialog stage. This is a mandatory dependency because it is used
	 * to close the dialog.
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		mDialogStage = dialogStage;
    }
	
	/**
	 * Set dialog open mode. It changes button and dialog texts.
	 * @param mode
	 */
	public void setOpenMode(OpenMode mode) {
		switch(mode) {
			case CREATION:
				mFinishButton.setText("Create");
				break;
			case EDITION:
				mFinishButton.setText("Edit");
				break;
		}
	}
	
	/**
	 * Set snippet to edit. It is used to fill all snippet fields.
	 * @param snippet
	 */
	public void setSnippet(Snippet snippet) {
		mNameField.setText(snippet.getName());
		mLanguageField.setText(snippet.getLanguage());
		mTagsField.setText(formatTagList(snippet.getTags()));
		mCodeArea.setText(snippet.getCode());
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
	 * Get edited snippet created by this dialog or null if there is
	 * no edited snippet.
	 * @return Snippet or null
	 */
	public Snippet getSnippet() {
        return mSnippet;
    }
	
	@FXML
    private void handleFinish() {
        if (isInputValid()) {
        	LocalDate now = LocalDate.now();
        	mSnippet = new Snippet(mNameField.getText(),
        			mLanguageField.getText(),
        			mCodeArea.getText(),
        			parseTags(mTagsField.getText()),
        			now.toEpochDay());
            mDialogStage.close();
        }
    }
	
	/**
	 * Create a list of tags from a raw text string. It uses
	 * TAG_SEPARATOR to split the string.
	 * @param rawTags
	 * @return List<String>
	 */
	private List<String> parseTags(String rawTags) {
		List<String> tagList = new ArrayList<String>();
		if (rawTags != null) {
			for (String tag : rawTags.split(TAG_SEPARATOR)) {
				tagList.add(tag.trim());
			}
		}
		return tagList;
	}
	
    @FXML
    private void handleCancel() {
        mDialogStage.close();
    }
    
    /**
     * Check if mandatory inputs are valid and if not it will notify to user using an error dialog.
     * @return true if it is valid and false if not.
     */
    private boolean isInputValid() {
        StringBuilder msg = new StringBuilder();

        if (mNameField.getText() == null || mNameField.getText().length() == 0) {
        	msg.append(" Name field can not be empty."); 
        }
        
        // TODO set by configuration if this field is mandatory == not empty
        if (mLanguageField.getText() == null || mLanguageField.getText().length() == 0) {
        	msg.append(" Language field can not be empty."); 
        }
        
        if (mCodeArea.getText() == null || mCodeArea.getText().length() == 0) {
        	msg.append(" Code text can not be empty."); 
        }

        if (msg.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(MainApp.APP_TITLE + " - Invalid Fields");
            alert.setHeaderText("There are invalid fields.");
            alert.setContentText(msg.toString());

            alert.showAndWait();
            return false;
        }
    }

}