/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import javafx.scene.layout.StackPane;

public class MainUI extends StackPane implements AppModeListener
{

    InteractionModel iModel;
    ReportView reportView;
    EditorView editorView;
    /**
     * Sets up the views, model, IModel, and controller as well as linking
     */
    public MainUI()
    {
        BlobModel model = new BlobModel();
        BlobController controller = new BlobController();
        ReportController reportController = new ReportController();
        editorView = new EditorView(800,800);
        reportView = new ReportView(800,800);

        editorView.setMaxHeight(Double.MAX_VALUE);
        editorView.setMaxWidth(Double.MAX_VALUE);
        editorView.setMinHeight(0);
        editorView.setMinWidth(0);

        reportView.setMaxHeight(Double.MAX_VALUE);
        reportView.setMaxWidth(Double.MAX_VALUE);
        reportView.setMinHeight(0);
        reportView.setMinWidth(0);

        this.iModel = new InteractionModel();
        iModel.addModeSubscriber(this);
        controller.setModel(model);
        editorView.setModel(model);
        reportView.setModel(model);
        controller.setIModel(iModel);
        editorView.setIModel(iModel);
        reportView.setIModel(iModel);
        model.addSubscriber(editorView);
        iModel.addSubscriber(editorView);
        editorView.setController(controller);
        reportView.setController(reportController);
        // TODO: set report view controller
        this.getChildren().addAll( editorView, reportView);
        reportView.setVisible(false);
        editorView.setVisible(true);
        this.setOnKeyPressed(controller::handleDiagramKeyPress);
    }

    @Override
    public void AppModeChanged()
    {
        if (iModel.getAppMode() == InteractionModel.AppMode.REPORT)
        {
            editorView.setVisible(false);
            reportView.setVisible(true);
        } else
        {
            editorView.setVisible(true);
            reportView.setVisible(false);
        }
    }
}
