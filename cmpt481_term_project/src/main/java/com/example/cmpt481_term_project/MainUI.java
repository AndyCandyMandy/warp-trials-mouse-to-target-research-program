package com.example.cmpt481_term_project;

import javafx.scene.layout.StackPane;

public class MainUI extends StackPane {

    public MainUI() {

        BlobModel model = new BlobModel();
        BlobController controller = new BlobController();
        BlobView view = new BlobView();
        InteractionModel iModel = new InteractionModel();

        controller.setModel(model);
        view.setModel(model);
        controller.setIModel(iModel);
        view.setIModel(iModel);
        model.addSubscriber(view);
        iModel.addSubscriber(view);

        view.setController(controller);

        this.getChildren().add(view);

        this.setOnKeyPressed(controller::handleDiagramKeyPress);
    }
}
