package com.ksr.knnclassifier.view;

import com.ksr.knnclassifier.controller.Controller;
import javafx.scene.Scene;

public abstract class View {

    protected Scene scene;
    protected Controller controller;

    public Scene getScene() {
        return scene;
    }

    public Controller getController() {
        return controller;
    }

}
