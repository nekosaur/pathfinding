package org.nekosaur.pathfinding.gui.presentation.main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import org.nekosaur.pathfinding.gui.business.Controller;
import org.nekosaur.pathfinding.gui.presentation.control.ControlView;
import org.nekosaur.pathfinding.gui.presentation.maptab.MapTabView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author nekosaur
 */

public class MainPresenter implements Initializable {

    @FXML
    private SplitPane splitPane;

    @FXML
    private Pane paneLeft;

    @FXML
    private Pane paneRight;

    @Inject
    private Controller controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MapTabView mapView = new MapTabView();
        ControlView controlView = new ControlView();

        Pane mapNode = (Pane)mapView.getView();
        Node controlNode = controlView.getView();

        mapNode.prefWidthProperty().bind(paneLeft.widthProperty());

        paneLeft.getChildren().add(mapNode);
        paneRight.getChildren().add(controlNode);

        splitPane.setDividerPositions(0.75f);
    }

    public void shutdown() {
        //controller.shutdown();
    }

}
