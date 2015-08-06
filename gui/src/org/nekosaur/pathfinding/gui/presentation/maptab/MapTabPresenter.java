package org.nekosaur.pathfinding.gui.presentation.maptab;

import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.nekosaur.pathfinding.gui.business.Controller;
import org.nekosaur.pathfinding.gui.business.events.EditMapLoadEvent;
import org.nekosaur.pathfinding.gui.presentation.maps.IMap;
import org.nekosaur.pathfinding.gui.presentation.maps.user.UserGridMap;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author nekosaur
 */
public class MapTabPresenter implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private HBox editPane;

    @FXML
    private HBox searchPane;

    @Inject
    private Controller controller;

    IMap editMap;
    IMap searchMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller.registerEvents(this);
    }

    @Subscribe
    public void handleEditMapLoadEvent(EditMapLoadEvent e) {
        if (editMap != null) {
            editPane.getChildren().remove(editMap);
            editMap = null;
        }

        System.out.println("Loading edit map");

        editMap = new UserGridMap(rootPane.getPrefWidth(), rootPane.getPrefHeight(), e.getData());
        editPane.getChildren().add((Pane)editMap);
    }




}
