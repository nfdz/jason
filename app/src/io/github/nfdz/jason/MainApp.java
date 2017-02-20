package io.github.nfdz.jason;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private static final String APP_TITLE = "JasonSnippets";
	private static final int MIN_WIDTH = 700;
	private static final int MIN_HEIGHT = 400;
	
    private Stage mPrimaryStage;
    private BorderPane mRootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		
		mPrimaryStage = primaryStage;
		mPrimaryStage.setTitle(APP_TITLE);

        initRootLayout();
        showSnippetsOverview();
	}
	
	public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            mRootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(mRootLayout);
            mPrimaryStage.setScene(scene);
            mPrimaryStage.setMinHeight(MIN_HEIGHT);
            mPrimaryStage.setMinWidth(MIN_WIDTH);
            mPrimaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public void showSnippetsOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SnippetsOverview.fxml"));
            AnchorPane snippetsOverview = (AnchorPane) loader.load();

            mRootLayout.setCenter(snippetsOverview);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
