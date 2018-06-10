package com.ksr.knnclassifier;

import com.ksr.knnclassifier.view.MenuView;
import com.ksr.knnclassifier.view.View;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static View menuView;

    public static void main(final String[] args) {
        launch(args);
    }

    public static View getMenuView() {
        return menuView;
    }

    public void start(final Stage primaryStage) {
        try {
            menuView = new MenuView("/views/MenuScene.fxml");
        } catch (Exception e) {
            logger.error(e.toString());
        }

        menuView.getController().setStage(primaryStage);

        primaryStage.setTitle("KNN Classifier");
        primaryStage.setScene(menuView.getScene());
        primaryStage.show();
    }

}
