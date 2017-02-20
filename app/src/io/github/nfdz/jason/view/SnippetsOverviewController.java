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

import io.github.nfdz.jason.model.Snippet;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * This class is the controller of the SnippetsOverview view.
 */
public class SnippetsOverviewController {

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
    
    public SnippetsOverviewController() {
    }

    @FXML
    private void initialize() {
    	mNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Snippet,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Snippet, String> param) {
				return new ReadOnlyObjectWrapper<String>(param.getValue().getName());
			}
		});
    }

    public void setRepository(ObservableList<Snippet> repository) {
    	mSnippetsTable.setItems(repository);
    }
    
}
